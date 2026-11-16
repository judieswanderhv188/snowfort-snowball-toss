package com.snowfort.toss.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowfort.toss.core.config.GameConfig
import com.snowfort.toss.domain.model.Projectile
import com.snowfort.toss.domain.model.Puff
import com.snowfort.toss.domain.model.RoundResult
import com.snowfort.toss.domain.model.SnowTarget
import com.snowfort.toss.domain.model.Vec2
import com.snowfort.toss.domain.repository.ProgressRepository
import com.snowfort.toss.domain.repository.SettingsRepository
import com.snowfort.toss.domain.usecase.BuildYardUseCase
import com.snowfort.toss.domain.usecase.ScoreRoundUseCase
import com.snowfort.toss.domain.usecase.StepPhysicsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val buildYard: BuildYardUseCase,
    private val stepPhysics: StepPhysicsUseCase,
    private val scoreRound: ScoreRoundUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<RoundResult>(Channel.BUFFERED)
    val events: Flow<RoundResult> = _events.receiveAsFlow()

    private var loop: Job? = null
    private var elapsedMs = 0
    private var resolveMs = 0
    private var wind = 0f
    private var maxDots = 12
    private var nextId = 1
    private var hitsThisThrow = 0
    private var aimX = 0f
    private var aimY = 0f

    private val origin = Vec2(GameConfig.ORIGIN_X, GameConfig.ORIGIN_Y)

    init {
        prepare()
    }

    fun resume() {
        if (_state.value.phase == GamePhase.FINISHED || _state.value.targets.isEmpty()) {
            prepare()
        }
        startLoop()
    }

    fun pause() {
        loop?.cancel()
        loop = null
    }

    fun setPaused(paused: Boolean) {
        _state.update { it.copy(paused = paused) }
    }

    fun onAimStart() {
        if (!_state.value.canThrow) return
        aimX = 0f
        aimY = 0f
        _state.update { it.copy(phase = GamePhase.CHARGING, aimActive = true, power = 0f) }
    }

    fun onAimDrag(deltaX: Float, deltaY: Float) {
        if (_state.value.phase != GamePhase.CHARGING) return
        aimX += deltaX
        aimY += deltaY
        val length = hypot(aimX, aimY)
        val power = (min(length, GameConfig.DRAG_MAX_NORM) / GameConfig.DRAG_MAX_NORM)
            .coerceIn(0f, 1f)
        val angle = if (length > 0.0001f) atan2(-aimY, -aimX) else 0f
        val velocity = stepPhysics.launchVelocity(power, angle)
        val dots = if (_state.value.arcAssist && power > 0.05f) {
            stepPhysics.trajectory(origin, velocity, maxDots, wind)
        } else {
            emptyList()
        }
        _state.update { it.copy(power = power, angle = angle, previewDots = dots) }
    }

    fun onAimRelease() {
        val current = _state.value
        if (current.phase != GamePhase.CHARGING) return
        if (current.power < 0.08f) {
            _state.update {
                it.copy(
                    phase = GamePhase.AIMING,
                    aimActive = false,
                    power = 0f,
                    previewDots = emptyList()
                )
            }
            return
        }
        release(current.power, current.angle)
    }

    fun onThrowClick() {
        val current = _state.value
        if (current.phase == GamePhase.CHARGING) {
            onAimRelease()
            return
        }
        if (!current.canThrow) return
        release(GameConfig.DEFAULT_POWER, defaultAngleRadians())
    }

    private fun defaultAngleRadians(): Float =
        GameConfig.DEFAULT_ANGLE_DEG * 3.1415927f / 180f

    private fun release(power: Float, angle: Float) {
        val current = _state.value
        if (current.snowballsLeft <= 0) return
        val triple = current.tripleReady
        val spread = GameConfig.TRIPLE_SPREAD_DEG * 3.1415927f / 180f
        val angles = if (triple) listOf(angle - spread, angle, angle + spread) else listOf(angle)
        val balls = angles.map { fanAngle ->
            Projectile(
                id = nextId++,
                position = origin,
                velocity = stepPhysics.launchVelocity(power, fanAngle)
            )
        }
        hitsThisThrow = 0
        _state.update {
            it.copy(
                phase = GamePhase.FLYING,
                projectiles = balls,
                snowballsLeft = it.snowballsLeft - 1,
                shots = it.shots + 1,
                streak = if (triple) 0 else it.streak,
                frost = if (triple) 0 else it.frost,
                aimActive = false,
                power = 0f,
                previewDots = emptyList()
            )
        }
        startLoop()
    }

    private fun prepare() {
        val progress = progressRepository.load()
        val settings = settingsRepository.load()
        val yard = buildYard(progress.currentYard)
        wind = yard.wind
        maxDots = yard.previewDots
        elapsedMs = 0
        resolveMs = 0
        hitsThisThrow = 0
        aimX = 0f
        aimY = 0f
        _state.value = GameUiState(
            yard = yard.index,
            targets = yard.targets,
            snowballsLeft = yard.snowballs,
            arcAssist = settings.arcAssist
        )
    }

    private fun startLoop() {
        if (loop?.isActive == true) return
        loop = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.TICK_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val current = _state.value
        if (current.paused || current.phase == GamePhase.FINISHED) return
        elapsedMs += GameConfig.TICK_MS.toInt()

        var targets = current.targets.map { it.copy(hidden = computeHidden(it, current.yard)) }
        var puffs = current.puffs
            .map { it.copy(ageMs = it.ageMs + GameConfig.TICK_MS.toInt()) }
            .filter { it.ageMs < 340 }
        var projectiles = current.projectiles
        var hits = current.hits

        if (projectiles.isNotEmpty()) {
            val moved = projectiles.map { stepPhysics(it, GameConfig.TICK_SECONDS, wind) }
            val living = mutableListOf<Projectile>()
            val mutableTargets = targets.toMutableList()
            moved.forEach { ball ->
                val index = mutableTargets.indexOfFirst { target ->
                    !target.knocked && !target.hidden && overlaps(ball, target)
                }
                when {
                    index >= 0 -> {
                        mutableTargets[index] = mutableTargets[index].copy(knocked = true)
                        hits += 1
                        hitsThisThrow += 1
                        puffs = puffs + Puff(nextId++, ball.position, 0, true)
                    }

                    stepPhysics.isOutOfPlay(ball) ->
                        puffs = puffs + Puff(nextId++, ball.position, 0, false)

                    else -> living += ball
                }
            }
            targets = mutableTargets
            projectiles = living
        }

        var phase = current.phase
        if (phase == GamePhase.FLYING && projectiles.isEmpty()) {
            phase = GamePhase.RESOLVING
            resolveMs = GameConfig.RESOLVE_MS
        }

        _state.update {
            it.copy(
                targets = targets,
                projectiles = projectiles,
                puffs = puffs,
                hits = hits,
                phase = phase
            )
        }

        if (phase == GamePhase.RESOLVING) {
            resolveMs -= GameConfig.TICK_MS.toInt()
            if (resolveMs <= 0) {
                settleThrow()
            }
        }
    }

    private fun settleThrow() {
        val current = _state.value
        val scored = hitsThisThrow > 0
        val streak = if (scored) current.streak + 1 else 0
        val frost = min(streak, GameConfig.FROST_SEGMENTS)
        val cleared = current.targets.all { it.knocked }
        if (cleared || current.snowballsLeft <= 0) {
            finishRound(cleared, current)
            return
        }
        _state.update {
            it.copy(phase = GamePhase.AIMING, streak = streak, frost = frost)
        }
    }

    private fun finishRound(cleared: Boolean, snapshot: GameUiState) {
        val result = scoreRound(
            yard = snapshot.yard,
            hits = snapshot.hits,
            shots = snapshot.shots,
            snowballsLeft = snapshot.snowballsLeft,
            cleared = cleared
        )
        _state.update {
            it.copy(
                phase = GamePhase.FINISHED,
                projectiles = emptyList(),
                previewDots = emptyList(),
                aimActive = false
            )
        }
        pause()
        viewModelScope.launch {
            val progress = progressRepository.load()
            progressRepository.save(scoreRound.applyTo(progress, result))
            _events.send(result)
        }
    }

    private fun overlaps(ball: Projectile, target: SnowTarget): Boolean {
        val dx = ball.position.x - target.position.x
        val dy = (ball.position.y - target.position.y) * 0.62f
        return hypot(dx, dy) < GameConfig.BALL_RADIUS + GameConfig.TARGET_RADIUS
    }

    private fun computeHidden(target: SnowTarget, yard: Int): Boolean {
        if (yard < GameConfig.HIDE_START_YARD) return false
        if (target.knocked) return false
        val cycle = (900 - yard * 8).coerceAtLeast(420) * 2
        val position = abs((elapsedMs + target.phaseMs) % cycle)
        return position > cycle * 55 / 100
    }

    override fun onCleared() {
        super.onCleared()
        pause()
    }
}

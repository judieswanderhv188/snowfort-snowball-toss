package com.snowfort.toss.data.sample

import com.snowfort.toss.domain.model.FortPiece
import com.snowfort.toss.domain.model.Mitten

object SampleData {

    val mittens: List<Mitten> = listOf(
        Mitten(id = 0, name = "RED KNIT", price = 6, tint = 0xFFE06828),
        Mitten(id = 1, name = "PINE GREEN", price = 10, tint = 0xFF5F9E6A),
        Mitten(id = 2, name = "FROST BLUE", price = 14, tint = 0xFF8FD8F2),
        Mitten(id = 3, name = "COCOA", price = 18, tint = 0xFF8A5A3B),
        Mitten(id = 4, name = "CANDY STRIPE", price = 24, tint = 0xFFEE8FA8),
        Mitten(id = 5, name = "MIDNIGHT", price = 30, tint = 0xFF592AAB)
    )

    val fortPieces: List<FortPiece> = listOf(
        FortPiece(id = 0, name = "SNOW BLOCK", spriteKey = "wall", unlockYards = 0),
        FortPiece(id = 1, name = "WATCH BANNER", spriteKey = "banner", unlockYards = 3),
        FortPiece(id = 2, name = "ICE BRICK", spriteKey = "ice", unlockYards = 8),
        FortPiece(id = 3, name = "CROWN TOWER", spriteKey = "banner", unlockYards = 15),
        FortPiece(id = 4, name = "FROST SPIRE", spriteKey = "ice", unlockYards = 24)
    )
}

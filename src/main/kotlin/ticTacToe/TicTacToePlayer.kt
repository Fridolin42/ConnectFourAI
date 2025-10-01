package de.fridolin1.ticTacToe

enum class TicTacToePlayer(val status: Array<Double>, val isEmpty: Boolean, val char: Char) {
    CIRCLE(arrayOf(1.0, 0.0), false, 'O'),
    CROSS(arrayOf(0.0, 1.0), false, 'X'),
    NONE(arrayOf(1.1, 1.1), true, '.')
}
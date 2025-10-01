package de.fridolin1.ticTacToe

import kotlin.random.Random

class TicTacToe {
    val gameField = Array(3) { Array(3) { TicTacToePlayer.NONE } }

    val firstPlayer = TicTacToePlayer.CIRCLE
    var currentPlayer = firstPlayer
        private set
    var victory = false
        private set
    var gameIsFull = false
        private set
    var running = true
        private set

    fun setField(x: Int, y: Int): Boolean {
        if (x !in (0 until 3) || y !in (0 until 3)) return false
        if (gameField[x][y].isEmpty) {
            gameField[x][y] = currentPlayer

            victory = checkWin(x, y)
            gameIsFull = isFull()
            running = !victory && !gameIsFull

            if (running) switchPlayer()
            return true
        }
        return false
    }

    fun isFull(): Boolean {
        return gameField.all { it.all { field -> !field.isEmpty } }
    }

    fun checkWin(x: Int, y: Int): Boolean {
        val player = gameField[x][y]

        // Horizontal (row y)
        val h = (0 until 3).all { gameField[it][y] == player }
        // Vertical (col x)
        val v = (0 until 3).all { gameField[x][it] == player }

        // Diagonals
        val d1 = (0 until 3).all { gameField[it][it] == player }
        val d2 = (0 until 3).all { gameField[it][2 - it] == player }

        return h || v || (x == y && d1) || (x + y == 2 && d2)
    }

    fun switchPlayer() {
        currentPlayer =
            if (currentPlayer == TicTacToePlayer.CIRCLE) TicTacToePlayer.CROSS
            else TicTacToePlayer.CIRCLE
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (row in gameField) {
            for (field in row) {
                builder.append(field.char)
                builder.append(" ")
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    fun randomAction(): Pair<Int, Int> {
        val actions = ArrayList<Pair<Int, Int>>()
        for (x in 0 until 3) {
            for (y in 0 until 3) {
                if (gameField[x][y].isEmpty)
                    actions.add(Pair(x, y))
            }
        }
        return actions[Random.nextInt(0, actions.size)]
    }
}
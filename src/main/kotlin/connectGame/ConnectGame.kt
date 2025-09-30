package de.fridolin1.connectGame

import kotlin.math.max
import kotlin.random.Random

class ConnectGame(val width: Int = 7, val height: Int = 6) {
    val gameField = Array(width) { Array(height) { FieldStatus.EMPTY } }
    val firstPlayer = FieldStatus.RED
    var currentPlayer = firstPlayer
        private set
    var running = true
        private set
    var victory = false
        private set
    var full = false
        private set


    fun drop(columnNumber: Int): Boolean {
        if (!running) return false
        if (columnNumber !in 0..<width) return false
        val dropResult = internalDrop(columnNumber)
        if (dropResult.first) {
            this.victory = getMaxConnectionLength(columnNumber, dropResult.second, this.currentPlayer) >= 4
            this.full = isFull()
            this.running = !this.victory && !this.full
            if (this.running) switchPlayer()
        }
        return dropResult.first
    }

    private fun internalDrop(columnNumber: Int): Pair<Boolean, Int> {
        val column = this.gameField[columnNumber]
        for (y in 0..<this.height) {
            if (column[y] != FieldStatus.EMPTY) continue
            column[y] = this.currentPlayer
            return Pair(true, y)
        }
        return Pair(false, -1)
    }

    fun switchPlayer() {
        currentPlayer = if (currentPlayer == FieldStatus.RED) FieldStatus.GREEN else FieldStatus.RED
    }

    fun isFull(): Boolean {
        return gameField.all { column -> column.all { it != FieldStatus.EMPTY } }
    }

    fun getMaxConnectionLength(x: Int, y: Int, player: FieldStatus): Int {
        var maxLength = 0
        ConnectionDirection.entries
            .forEach { maxLength = max(getMaxConnectionLengthByDirection(x, y, player, it), maxLength) }
        return maxLength
    }

    fun getMaxConnectionLengthByDirection(x: Int, y: Int, player: FieldStatus, direction: ConnectionDirection): Int {
        var count = 1
        for (i in 1..3) {
            val currentX = x + direction.dx * i
            val currentY = y + direction.dy * i
            if (currentX !in 0..<width) break
            if (currentY !in 0..<height) break
            if (gameField[currentX][currentY] != player) break
            count += 1
        }
        for (i in 1..3) {
            val currentX = x - direction.dx * i
            val currentY = y - direction.dy * i
            if (currentX !in 0..<width) break
            if (currentY !in 0..<height) break
            if (gameField[currentX][currentY] != player) break
            count += 1
        }
        return count
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (row in height - 1 downTo 0) {
            for (col in 0 until width) {
                sb.append(
                    when (gameField[col][row]) {
                        FieldStatus.EMPTY -> "."
                        FieldStatus.RED -> "R"
                        FieldStatus.GREEN -> "G"
                    }
                )
                sb.append(' ')
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    fun randomDrop(): Int {
        val actions = (0..<width).filter { gameField[it][height - 1] == FieldStatus.EMPTY }
        val action = actions[Random.nextInt(0, actions.size)]
        drop(action)
        return action
    }
}
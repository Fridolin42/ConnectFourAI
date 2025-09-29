package de.fridolin1.connectGame

enum class FieldStatus {
    EMPTY, RED, GREEN;

    fun intValue() = when (this) {
        RED -> 1
        GREEN -> -1
        EMPTY -> 0
    }
}
package de.fridolin1.connectGame

enum class ConnectionDirection(val dx: Int, val dy: Int) {
    HORIZONTAL(1, 0),
    VERTICAL(0, 1),
    DESCENDING(1, 1),
    ASCENDING(1, -1)
}
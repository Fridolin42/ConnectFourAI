package de.fridolin1.color

import java.awt.Color
import kotlin.random.Random

fun getRandomColor(): Pair<Int, Color> {
    val s = Random.nextInt(25, 100) / 100f
    val b = Random.nextInt(25, 100) / 100f

    val cID = Random.nextInt(0, 6)
    val color = when (cID) {
        0 -> getRandomRedColor(s, b)
        1 -> getRandomYellowColor(s, b)
        2 -> getRandomGreenColor(s, b)
        3 -> getRandomCyanColor(s, b)
        4 -> getRandomBlueColor(s, b)
        5 -> getRandomMagentaColor(s, b)
        else -> throw Exception("Oh no")
    }

    return Pair(cID, color)
}

fun getRandomRedColor(s: Float, b: Float): Color {
    return if (Random.nextBoolean())
        Color.getHSBColor(Random.nextInt(0, 30) / 360.0f, s, b)
    else
        Color.getHSBColor(Random.nextInt(330, 360) / 360f, s, b)
}

fun getRandomYellowColor(s: Float, b: Float): Color {
    return Color.getHSBColor(Random.nextInt(30, 90) / 360f, s, b)
}

fun getRandomGreenColor(s: Float, b: Float): Color {
    return Color.getHSBColor(Random.nextInt(90, 150) / 360f, s, b)
}

fun getRandomCyanColor(s: Float, b: Float): Color {
    return Color.getHSBColor(Random.nextInt(150, 210) / 360f, s, b)
}

fun getRandomBlueColor(s: Float, b: Float): Color {
    return Color.getHSBColor(Random.nextInt(210, 270) / 360f, s, b)
}

fun getRandomMagentaColor(s: Float, b: Float): Color {
    return Color.getHSBColor(Random.nextInt(270, 330) / 360f, s, b)
}
package de.fridolin1.aiApplications

import de.fridolin1.ai.ANN
import de.fridolin1.color.getRandomColor
import de.fridolin1.save
import kotlin.math.pow

fun trainColorNamerAI() {
    val ann = ANN(3, 6, 9, 9, 9, 6)

    val iterations = 2.0.pow(25)
    repeat(iterations.toInt()) {
        val p = it / iterations * 100
        print("\rProgress: %.2f%%".format(p))

        val c = getRandomColor()
        val inputVector = arrayOf(c.second.red / 255.0, c.second.green / 255.0, c.second.blue / 255.0)
        val expectedOutput = Array(6) { i -> if (i == c.first) 1.0 else 0.0 }
        ann.train(inputVector, expectedOutput)
    }

    println("\nDone with Training")

    save(ann, "ColorDetectorV2.json")

    var correct = 0
    repeat(1000) {
        val c = getRandomColor()
        val inputVector = arrayOf(c.second.red / 255.0, c.second.green / 255.0, c.second.blue / 255.0)
        val output = ann.compute(inputVector)
        val indexMax = output.indices.maxBy { i -> output[i] }
        if (indexMax == c.first) correct++
    }
    println("\nCorrect: $correct/1000")
}
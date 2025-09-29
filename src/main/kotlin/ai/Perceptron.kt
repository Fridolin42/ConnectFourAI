package de.fridolin1.ai

import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class Perceptron(val dimension: Int, val activationFunctions: ActivationFunctions) {
    companion object {
        const val ALPHA = 0.01
    }

    var bias = 0.0
    val weights = Array(dimension) { Random.nextDouble(0.8, 1.25) / dimension }

    fun predict(inputVector: Array<Double>): Double {
        if (inputVector.size != dimension) throw IllegalArgumentException("Input vector must have $dimension dimension, but has ${inputVector.size} dimension")
        val sum = inputVector.mapIndexed { index, value -> value * weights[index] }.sum() + bias
        return activationFunctions.function(sum)
    }

    fun train(trainingsData: List<Pair<Array<Double>, Double>>) {
        for ((input, expected) in trainingsData) {
            val actualResult = predict(input)
            val error = expected - actualResult

            for (weightIndex in weights.indices) {
                weights[weightIndex] += ALPHA * error * input[weightIndex]
            }
            bias += ALPHA * error
        }
    }
}

enum class ActivationFunctions(val function: (Double) -> Double) {
    SIGNUM({
        when {
            it > 0 -> 1.0; it < 0 -> -1.0; else -> 0.0
        }
    }),
    HEAVISIDE({
        if (it >= 0) 1.0 else 0.0
    }),
    ReLU({ max(0.0, it) }),
    SIGMOID({
        1 / (1 + Math.E.pow(-it))
    })
}
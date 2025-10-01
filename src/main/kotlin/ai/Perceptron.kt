package de.fridolin1.ai

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.random.Random

@Serializable
class Perceptron : Cloneable {
    companion object {
        const val ALPHA = 0.025
    }

    fun sigmoid(x: Double): Double {
        return 1 / (1 + Math.E.pow(-x))
    }

    var bias = 0.0
    val weights: Array<Double>
    val dimension: Int

    constructor(dimension: Int) {
        this.dimension = dimension
        this.weights = Array(dimension) { Random.nextDouble(0.8, 1.25) / dimension }
    }

    constructor(dimension: Int, bias: Double, weights: Array<Double>) {
        this.bias = bias
        this.weights = weights
        this.dimension = dimension
    }

    public override fun clone(): Perceptron {
        return Perceptron(dimension, bias, weights.copyOf())
    }

    fun predict(inputVector: Array<Double>): Double {
        if (inputVector.size != dimension) throw IllegalArgumentException("Input vector must have $dimension dimension, but has ${inputVector.size} dimension")
        val sum = inputVector.mapIndexed { index, value -> value * weights[index] }.sum() + bias
        return sigmoid(sum)
    }

    fun train(input: Array<Double>, targetOrError: Double, actualResult: Double, outputLayer: Boolean): Array<Double> {
        val delta: Double
        if (outputLayer)
            delta = (targetOrError - actualResult) * actualResult * (1 - actualResult)
        else
            delta = targetOrError * actualResult * (1 - actualResult)

        val backpropError = weights.map { it * delta }.toTypedArray()

        for (i in weights.indices) {
            weights[i] += ALPHA * delta * input[i]
        }
        bias += ALPHA * delta

        return backpropError
    }

    fun train(trainingsData: List<Pair<Array<Double>, Double>>) {
        for ((input, expected) in trainingsData) {
            train(input, expected, predict(input), true)
        }
    }

    override fun toString(): String {
        return "Perceptron(dimension=$dimension, bias=$bias, weights=${weights.contentToString()})"
    }

    fun setWeightsToAVG(list: List<Perceptron>) {
        for (i in weights.indices) {
            weights[i] = 0.0
            for (j in list.indices) {
                weights[i] += list[j].weights[i]
            }
            weights[i] /= list.size
        }
        bias = 0.0
        for (i in list.indices) {
            bias += list[i].bias
        }
        bias /= list.size
    }
}
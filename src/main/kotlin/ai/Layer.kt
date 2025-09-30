package de.fridolin1.ai

import kotlinx.serialization.Serializable

@Serializable
class Layer(val perceptronAmount: Int, val inputVectorDimension: Int) {
    val perceptrons = Array(perceptronAmount) { Perceptron(inputVectorDimension) }
    fun compute(inputVector: Array<Double>): Array<Double> {
        if (inputVector.size != inputVectorDimension) throw IllegalArgumentException("Input vector must have $inputVectorDimension dimension, but has ${inputVector.size} dimension")
        val outputVector = Array(perceptronAmount) { perceptrons[it].predict(inputVector) }
        return outputVector
    }

    fun train(
        inputVector: Array<Double>,
        expectedOutput: Array<Double>,
        actualOutput: Array<Double>,
        outputLayer: Boolean
    ): Array<Double> {
        if (inputVector.size != inputVectorDimension) throw IllegalArgumentException("Input vector must have $inputVectorDimension dimension, but has ${inputVector.size} dimension")
        val accumulatedBackpropError = Array(inputVectorDimension) { 0.0 }
        for ((i, perceptron) in perceptrons.withIndex()) {
            val backpropError = perceptron.train(inputVector, expectedOutput[i], actualOutput[i], outputLayer)
            for (j in backpropError.indices) {
                accumulatedBackpropError[j] += backpropError[j]
            }
        }
        return accumulatedBackpropError
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Perceptrons: $perceptronAmount ")
        perceptrons.forEach { builder.append(it).append("; ") }
        return builder.toString()
    }
}
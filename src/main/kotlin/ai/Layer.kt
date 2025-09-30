package de.fridolin1.ai

import kotlinx.serialization.Serializable

@Serializable
class Layer : Cloneable {
    val perceptrons: Array<Perceptron>
    val perceptronAmount: Int
    val inputVectorDimension: Int

    constructor(perceptronAmount: Int, inputVectorDimension: Int) {
        this.perceptrons = Array(perceptronAmount) { Perceptron(inputVectorDimension) }
        this.perceptronAmount = perceptronAmount
        this.inputVectorDimension = inputVectorDimension
    }

    constructor(perceptrons: Array<Perceptron>, perceptronAmount: Int, inputVectorDimension: Int) {
        this.perceptrons = perceptrons
        this.perceptronAmount = perceptronAmount
        this.inputVectorDimension = inputVectorDimension
    }

    public override fun clone(): Layer {
        return Layer(perceptrons.map { it.clone() }.toTypedArray(), perceptronAmount, inputVectorDimension)
    }

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

    fun setWeightsToAVG(list: List<Layer>) {
        for (i in perceptrons.indices) {
            val perceptronList = mutableListOf<Perceptron>()
            for (j in list.indices) {
                perceptronList.add(list[j].perceptrons[i])
            }
            perceptrons[i].setWeightsToAVG(perceptronList)
        }
    }
}
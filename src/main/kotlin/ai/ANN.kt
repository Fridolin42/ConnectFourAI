package de.fridolin1.ai

import kotlinx.serialization.Serializable

@Serializable
class ANN(val inputVectorDimension: Int, vararg val neurons: Int) {
    val layers = Array(neurons.size) {
        Layer(
            neurons[it],
            if (it == 0) inputVectorDimension else neurons[it - 1]
        )
    }

    fun compute(inputVector: Array<Double>): Array<Double> {
        var currentVector = inputVector
        for (layer in layers) {
            currentVector = layer.compute(currentVector)
        }
        return currentVector
    }

    fun train(inputVector: Array<Double>, expectedOutput: Array<Double>) {
        val inAndOutputVectors =
            Array(neurons.size + 1) { Array(if (it == 0) inputVectorDimension else neurons[it - 1]) { 0.0 } }
        inAndOutputVectors[0] = inputVector

        var currentVector = inputVector
        for ((i, layer) in layers.withIndex()) {
            currentVector = layer.compute(currentVector)
            inAndOutputVectors[i + 1] = currentVector
        }

        synchronized(this) {
            currentVector = expectedOutput
            for (i in layers.size - 1 downTo 0) {
                val layer = layers[i]
                currentVector =
                    layer.train(inAndOutputVectors[i], currentVector, inAndOutputVectors[i + 1], i == layers.size - 1)
            }
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        layers.forEach { builder.append(it).append("\n") }
        return builder.toString()
    }
}
package de.fridolin1.ai

import kotlin.math.pow
import kotlin.random.Random

class ReinforcedLearning(val ann: ANN, val discount: Double) {
    val replayBuffer = ArrayList<ReplayBufferElement>()

    fun step(inputVector: Array<Double>): Int {
        val outputVector = ann.compute(inputVector)
        val action = outputVector.indices.maxBy { outputVector[it] + noise() }
        replayBuffer.add(ReplayBufferElement(inputVector, outputVector, action))
        return action
    }

    fun noise(): Double = Random.nextDouble(-0.2, 0.2)

    fun reward(reward: Double) {
        val bufferSize = replayBuffer.size
        for ((i, element) in replayBuffer.withIndex()) {
            val output = element.output.copyOf()
            output[element.action] += discount.pow(bufferSize - i - 1) * reward
            ann.train(element.input, output)
        }
        replayBuffer.clear()
    }

    fun overrideLastAction(action: Int) {
        replayBuffer.last().action = action
    }

    fun clearBuffer() = replayBuffer.clear()
}

data class ReplayBufferElement(val input: Array<Double>, val output: Array<Double>, var action: Int) {
    //auto generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReplayBufferElement

        if (action != other.action) return false
        if (!input.contentEquals(other.input)) return false
        if (!output.contentEquals(other.output)) return false

        return true
    }

    //auto generated
    override fun hashCode(): Int {
        var result = action
        result = 31 * result + input.contentHashCode()
        result = 31 * result + output.contentHashCode()
        return result
    }
}
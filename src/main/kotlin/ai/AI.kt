package de.fridolin1.ai

import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.asJavaRandom

class AI {

    private val neuronLayers: List<Array<Double>>
    private val neuronConnections: List<Array<Array<Double>>>
    private val inputNeurons = 7 * 6 + 1
    private val alpha = 0.0001

    constructor() {
        this.neuronLayers = listOf(
            // 7 width + 1 player color
            Array(this.inputNeurons) { 0.0 }, //input layer
            Array(7 * 5) { 0.0 },
//            Array(7 * 4) { 0.0 },
            Array(7 * 3) { 0.0 },
//            Array(7 * 2) { 0.0 },
            Array(7 * 1) { 0.0 } //output Layer
        )

        this.neuronConnections = (0..<neuronLayers.size - 1).map { index ->
            Array(this.neuronLayers[index].size) {
                Array(this.neuronLayers[index + 1].size) {
//                    this.random(19.0 / 20.0, 20.0 / 19.0, true) / this.neuronLayers[index].size.toDouble()
                    Random.asJavaRandom().nextGaussian() / this.neuronLayers[index].size.toDouble()
                }
            }
        }.toList()
    }

    constructor(neuronList: List<Array<Double>>, neuronConnections: List<Array<Array<Double>>>) {
        this.neuronLayers = neuronList
        this.neuronConnections = neuronConnections
    }

    private fun random(min: Double, max: Double, negativeMirrorPossible: Boolean = false): Double {
        val rand = Random.nextDouble(min, max)
        if (negativeMirrorPossible)
            return if (Random.nextBoolean()) rand else -rand
        return rand
    }

    fun threshold(value: Double, threshold: Double): Double = if (value < threshold) 0.0 else value

    /**
     * @param input The input layer for the neuronal network
     * @param targetNeurons The index of the wanted output neuron
     */

    fun train(input: Array<Double>, targetNeurons: Array<Int>): Boolean {
        //forward propagation and error calculation
        val activations = ArrayList<Array<Double>>()
        var currentLayerFor = input
        activations.add(currentLayerFor.copyOf())

        for (computingStep in 0..<neuronConnections.size) {

            val newLayer = neuronLayers[computingStep + 1].copyOf()
            val connectionWeights = neuronConnections[computingStep]

            for (i in 0..<currentLayerFor.size) {
                for (j in 0..<newLayer.size) {
                    newLayer[j] += currentLayerFor[i] * connectionWeights[i][j]
                }
            }

            currentLayerFor = newLayer
            activations.add(currentLayerFor.copyOf())
        }

        var currentLayer = currentLayerFor.copyOf()
            .map { threshold(it, 0.1) }
            .mapIndexed { index, value ->
                (targetNeurons[index] - value) * this.alpha
            }.toTypedArray()

        //back propagation
        for (computingStep in neuronConnections.size - 1 downTo 0) {
            val prevLayer = neuronLayers[computingStep].copyOf()
            for (i in 0..<prevLayer.size) {
                for (j in 0..<currentLayer.size) {
                    val weights = neuronConnections[computingStep]
                    val neuronValue = weights[i][j] * currentLayer[j]
                    prevLayer[i] += neuronValue

                    //adjust connection weight
                    weights[i][j] -= currentLayer[j] * activations[computingStep][i] * this.alpha
                }
                //adjust bias weight (layer), don't change the input layer, it wouldn't change anything
            }
            if (computingStep > 0) {
//                    neuronLayers[computingStep][i] = (prevLayer[i] + prevLayer[i] * this.alpha)
                val biases = neuronLayers[computingStep + 1]
                for (i in currentLayer.indices) {
                    biases[i] -= alpha * currentLayer[i]
                }
            }

            currentLayer = prevLayer
        }

        return targetNeurons[currentLayerFor.indices.maxBy { currentLayerFor[it] }] > 0
    }

    fun compute(input: Array<Double>): Array<Double> {
        var currentLayer = input

        for (computingStep in 0..<neuronConnections.size) {

            val newLayer = neuronLayers[computingStep + 1].copyOf()
            val connectionWeights = neuronConnections[computingStep]

            for (i in 0..<currentLayer.size) {
                for (j in 0..<newLayer.size) {
                    newLayer[j] += currentLayer[i] * connectionWeights[i][j]
                }
            }

            currentLayer = newLayer
        }
        currentLayer = currentLayer.map { threshold(it, 0.0) }.toTypedArray()
        return currentLayer
    }

    fun saveAI(fileName: String) {
        val file = File(fileName)
        val writer = file.bufferedWriter()
        writer.append("AI\n")
        writer.append("\n")
        writer.append(Json.encodeToString(neuronLayers))
        writer.append("\n")
        writer.append(Json.encodeToString(neuronConnections))
        writer.close()
    }

    fun avgOfLayer(n: Int): Double {
        return this.neuronLayers[n].map { abs(it) }.average()
    }

    fun avgOfConnections(n: Int): Double {
        return this.neuronConnections[n].map { it.map { number -> abs(number) }.average() }.average()
    }
}
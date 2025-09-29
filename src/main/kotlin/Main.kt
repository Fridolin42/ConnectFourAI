package de.fridolin1

import de.fridolin1.ai.AI
import de.fridolin1.ai.ActivationFunctions
import de.fridolin1.ai.Perceptron
import de.fridolin1.connectGame.ConnectGame
import de.fridolin1.connectGame.FieldStatus
import java.util.*
import kotlin.concurrent.atomics.ExperimentalAtomicApi

fun main() {
    val perceptron = Perceptron(3, ActivationFunctions.HEAVISIDE)

    val data = listOf(
        formatColor(255, 153, 51, true),
        formatColor(255, 204, 0, true),
        formatColor(255, 102, 0, true),
        formatColor(255, 153, 0, true),
        formatColor(204, 102, 0, true),
        formatColor(255, 204, 102, true),
        formatColor(102, 102, 51, false),
        formatColor(153, 255, 102, false),
        formatColor(0, 255, 153, false),
        formatColor(102, 204, 255, false),
        formatColor(51, 51, 255, false),
        formatColor(153, 102, 255, false),
        formatColor(153, 0, 153, false),
        formatColor(255, 153, 153, false),
        formatColor(102, 0, 51, false),
        formatColor(255, 0, 0, false),
        formatColor(0, 255, 0, false),
        formatColor(200, 0, 0, false),
        formatColor(0, 200, 0, false),
        formatColor(128, 255, 0, false),
        formatColor(32, 255, 0, false),
    )

    repeat(128) {
        perceptron.train(data)
    }

    println(perceptron.weights.contentToString())
    println(perceptron.bias)

    var correct = 0
    data.forEach { if (perceptron.predict(it.first) == it.second) correct++ else println("Wrong: ${it.second}") }
    println("$correct/${data.size}")
}

fun formatColor(r: Int, g: Int, b: Int, isOrange: Boolean): Pair<Array<Double>, Double> {
    return Pair(arrayOf(r / 255.0, g / 255.0, b / 255.0), if (isOrange) 1.0 else 0.0)
}

fun playHumanGame(hints: Boolean) {
    val scanner = Scanner(System.`in`)
    val game = ConnectGame()

    while (game.running) {
        while (true) {
            println()
            println(game)
            println("You are color ${game.currentPlayer}")
            if (hints)
                println("Best throw positions are ${game.bestColumnsToThrow().contentToString()}")
            print("Your Turn: ")
            val input = scanner.nextInt()
            if (game.drop(input)) break
            println()
        }
    }

    println(game)
    if (game.victory)
        print(game.currentPlayer.toString() + " has won the game")
    else
        print("No one has won the game")
}

fun playGameVsAI(ai: AI) {
    val scanner = Scanner(System.`in`)
    val game = ConnectGame()

    while (game.running) {
        if (game.currentPlayer == FieldStatus.RED)
            while (true) {
                println()
                println(game)
                println("You are color ${game.firstPlayer}")
                print("Your Turn: ")
                val input = scanner.nextInt()
                if (game.drop(input)) break
                println()
            }
        else {
            runAIGameStep(ai, game)
        }
    }

    println(game)
    if (game.victory)
        print(game.currentPlayer.toString() + " has won the game")
    else
        print("No one has won the game")
}

@OptIn(ExperimentalAtomicApi::class)
fun trainAI(ai: AI) {
//    val iterations = 262144
    val iterations = 512
    var it = 0
//    val iterations = 64
    while (true) {
        val progress = it.toDouble() / iterations * 100
//        println("avg: ${ai.avgOfLayer(3)}; ${ai.avgOfConnections(3)}")

        var errors = 0
        repeat(128) {
            val game = ConnectGame()
            while (game.running) {
                val bestThrows = game.bestColumnsToThrow()
                if (!ai.train(getInputNeurons(game), bestThrows)) errors++
                runAIGameStep(ai, game)
            }
        }

        print("Iterations: $it; errors: $errors\r".format(progress))
        if (it > 100 && errors < 500) break
        it++
    }
}

fun runAIGameStep(ai: AI, game: ConnectGame) {
    val action = ai.compute(getInputNeurons(game))
    val actionMap = mutableMapOf<Int, Double>()
    action.forEachIndexed { i, v -> actionMap[i] = v }

    while (true) {
        if (actionMap.isEmpty()) {
            print(game)
            throw Exception("Stuff, that should never be possible, happened")
        }
        val maxIndex = actionMap.keys.maxBy { actionMap[it]!! }
        if (game.drop(maxIndex)) break
        actionMap.remove(maxIndex)
    }
}

fun playAiGame(ai: AI) {
    val game = ConnectGame()
    println()
    while (game.running) {
        println(game.bestColumnsToThrow().contentToString())
        println(game)
        println()
        runAIGameStep(ai, game)
    }
    println(game)
}

fun getInputNeurons(game: ConnectGame): Array<Double> {
    val inputNeurons = mutableListOf<Double>()
    game.gameField.forEach {
        it.forEach { fieldStatus -> inputNeurons.add(fieldStatus.intValue().toDouble()) }
    }
    inputNeurons.add(game.currentPlayer.intValue().toDouble())
    return inputNeurons.toTypedArray()
}
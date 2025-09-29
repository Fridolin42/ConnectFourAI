package de.fridolin1

import de.fridolin1.ai.AI
import de.fridolin1.connectGame.ConnectGame
import de.fridolin1.connectGame.FieldStatus
import java.util.*
import kotlin.concurrent.atomics.ExperimentalAtomicApi

fun main() {
    val ai = AI()
    ai.saveAI("connect4AiV1BeforeTraining")
    trainAI(ai)
    ai.saveAI("connect4AiV1AfterTraining")
    playAiGame(ai)
//    playGameVsAI(ai)
//    playHumanGame(true)
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
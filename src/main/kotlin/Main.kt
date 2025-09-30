package de.fridolin1

import de.fridolin1.ai.ANN
import de.fridolin1.ai.ReinforcedLearning
import de.fridolin1.connectGame.ConnectGame
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow

fun main() {
    val ann = ANN(6 * 7 + 1, 43, 215, 215, 215, 215, 7)

    val executor = Executors.newWorkStealingPool()
    val iterations = 2.0.pow(16)
    repeat(iterations.toInt()) {
        val p = (it / iterations) * 100
        print("\rProgress: %.4f%%".format(p))

        executor.submit {
            val rl1 = ReinforcedLearning(ann, 0.9)
            val rl2 = ReinforcedLearning(ann, 0.9)
            repeat(128) {
                val game = ConnectGame()
                while (game.running) {

                    if (game.currentPlayer == game.firstPlayer) {
                        var action = rl1.step(getInputNeurons(game))
                        if (!game.drop(action)) {
                            action = game.randomDrop()
                            rl1.overrideLastAction(action)
                        }
                    } else {
                        var action = rl2.step(getInputNeurons(game))
                        if (!game.drop(action)) {
                            action = game.randomDrop()
                            rl2.overrideLastAction(action)
                        }
                    }

                }
                if (game.victory) {
                    if (game.currentPlayer == game.firstPlayer) {
                        rl1.reward(1.0)
                        rl2.reward(-1.0)
                    } else {
                        rl1.reward(-1.0)
                        rl2.reward(1.0)
                    }
                }
                rl1.clearBuffer()
                rl2.clearBuffer()
            }
        }
    }
    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.SECONDS)
    println("\nDone")

    save(ann, "fourWinnsAiV1.json")

    println("Ai saved")

    playAgainstAI(ann)
}

fun playAgainstAI(ann: ANN) {
    val game = ConnectGame()
    val scanner = Scanner(System.`in`)
    while (game.running) {
        if (game.currentPlayer == game.firstPlayer) {
            while (true) {
                println()
                println("------------------------")
                print(game)
                print("Bitte Zahl eingeben:")
                val number = scanner.nextInt()
                if (game.drop(number)) break
            }
        } else {
            val output = ann.compute(getInputNeurons(game))
            val actionMap = HashMap<Int, Double>()
            output.indices.forEach { actionMap[it] = output[it] }
            while (true) {
                val action = actionMap.keys.maxBy { actionMap[it]!! }
                if (game.drop(action)) break
            }
        }
    }
}

fun getInputNeurons(game: ConnectGame): Array<Double> {
    val inputNeurons = mutableListOf<Double>()
    game.gameField.forEach {
        it.forEach { fieldStatus -> inputNeurons.add(fieldStatus.intValue().toDouble()) }
    }
    inputNeurons.add(game.currentPlayer.intValue().toDouble())
    return inputNeurons.toTypedArray()
}

fun save(ann: ANN, filename: String) {
    val content = Json.encodeToString(ann)
    val file = File("./ann/$filename")
    if (!file.parentFile.exists()) file.parentFile.mkdirs()
    val writer = file.bufferedWriter()
    writer.write(content)
    writer.close()
}
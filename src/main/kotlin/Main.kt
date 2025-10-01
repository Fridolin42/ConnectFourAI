package de.fridolin1

import de.fridolin1.ai.ANN
import de.fridolin1.ai.ReinforcedLearning
import de.fridolin1.aiApplications.humanTicTacToeGame
import de.fridolin1.aiApplications.trainTTTAi
import de.fridolin1.connectGame.ConnectGame
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
fun main() {

    trainTTTAi()
//    val ann = ANN(6 * 7 + 1, 43, 86, 129, 86, 7)


//    val iterations = 750.0
//    println("Iterations: ${iterations.toInt()}")
//
//    repeat(iterations.toInt()) {
//        val p = it / iterations * 100
//        print("\rProgress: %.4f%%".format(p))
//
//        val executor = Executors.newWorkStealingPool()
//        val annList = Collections.synchronizedList(mutableListOf<ANN>())
//
//        repeat(60) {
//            executor.submit {
//                val rl1 = ReinforcedLearning(ann.clone(), 0.9)
//                val rl2 = ReinforcedLearning(ann.clone(), 0.9)
//                trainAI(rl1, rl2)
//                annList.add(rl1.ann)
//                annList.add(rl2.ann)
//            }
//        }
//
//        executor.shutdown()
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
//
//        ann.setWeightsToAVG(annList)
//    }
//
//    println("\nDone")
//    save(ann, "fourWinnsAiV1.json")
//    println("Ai saved")
//
//    playAgainstAI(ann)
}

fun trainAI(rl1: ReinforcedLearning, rl2: ReinforcedLearning) {
    repeat(16) {
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
    }
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
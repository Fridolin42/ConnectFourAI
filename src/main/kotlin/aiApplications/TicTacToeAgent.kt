package de.fridolin1.aiApplications

import de.fridolin1.ai.ANN
import de.fridolin1.ai.ReinforcedLearning
import de.fridolin1.save
import de.fridolin1.ticTacToe.TicTacToe
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow

fun trainTTTAi() {
    val ann = ANN(20, 30, 24, 18, 9)

    val iterations = 2.0.pow(17)
    repeat(iterations.toInt()) {
        print("\rProgress: %.2f%%".format(it / iterations * 100))

        val avgAnnList = Collections.synchronizedList(mutableListOf<ANN>())
        val executor = Executors.newWorkStealingPool()
        repeat(100) {
            executor.submit {
                val rl1 = ReinforcedLearning(ann.clone(), 0.75)
                val rl2 = ReinforcedLearning(ann.clone(), 0.75)
                repeat(16) {
                    val game = TicTacToe()
                    while (game.running) {
                        if (game.currentPlayer == game.firstPlayer) {
                            val action = rl1.step(gameToInputVector(game))
                            val x = action % 3
                            val y = action / 3
                            if (!game.setField(x, y)) {
                                val randAction = game.randomAction()
                                game.setField(randAction.first, randAction.second)
                                rl1.overrideLastAction(3 * randAction.second + randAction.first)
                            }
                        } else {
                            val action = rl2.step(gameToInputVector(game))
                            val x = action % 3
                            val y = action / 3
                            if (!game.setField(x, y)) {
                                val randAction = game.randomAction()
                                game.setField(randAction.first, randAction.second)
                                rl2.overrideLastAction(3 * randAction.second + randAction.first)
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
                    } else if(game.isFull()) {
                        rl1.reward(0.1)
                        rl2.reward(0.1)
                    }
                }
                avgAnnList.add(rl1.ann)
                avgAnnList.add(rl2.ann)
            }
        }
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        ann.setWeightsToAVG(avgAnnList)
        avgAnnList.clear()
    }
    println("\nTraining Done")

    save(ann, "TicTacToeAIV1.json")
}

fun gameToInputVector(game: TicTacToe): Array<Double> {
    val inputVector = mutableListOf<Double>()

    for (row in game.gameField)
        for (it in row)
            inputVector.addAll(it.status)

    inputVector.addAll(game.currentPlayer.status)
    return inputVector.toTypedArray()
}

fun humanTicTacToeGame() {
    val game = TicTacToe()
    val scanner = Scanner(System.`in`)

    while (game.running) {
        while (true) {
            println()
            println("------------")
            println("Player: ${game.currentPlayer}")
            println(game)
            print("Row: ")
            val row = scanner.nextInt()
            print("Column: ")
            val column = scanner.nextInt()
            println()
            if (game.setField(row, column)) break
        }
    }

    println(game)
    println("Victory: ${game.victory}")
    println("Current Player: ${game.currentPlayer}")
}
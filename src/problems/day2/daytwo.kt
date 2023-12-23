package problems.day2

import java.io.File

const val gamesFile = "input/day2/games.txt"

data class Round(val red: Int, val green: Int, val blue: Int)

data class Game(val id: Int, val rounds: List<Round>) {
    fun allRedUnder(max: Int) = rounds.all { it.red <= max }
    fun allGreenUnder(max: Int) = rounds.all { it.green <= max }
    fun allBlueUnder(max: Int) = rounds.all { it.blue <= max }
    fun power() = maxRed() * maxGreen() * maxBlue()

    private fun maxRed() = rounds.maxOf { it.red }
    private fun maxGreen() = rounds.maxOf { it.green }
    private fun maxBlue() = rounds.maxOf { it.blue }
}

fun main() {
    part1()
    part2()
}

fun part1() {
    val idSum = File(gamesFile).bufferedReader().useLines { sumIDs(it) }
    println("ID sum is $idSum")
}

fun sumIDs(lines: Sequence<String>) =
    lines.map { it.toGame() }
        .filter { it.allRedUnder(12) and it.allGreenUnder(13) and it.allBlueUnder(14) }
        .map { it.id }
        .sum()

fun String.toGame(): Game =
    Game(
        this.substringBefore(":").toGameID(),
        this.substringAfter(":").toRounds(),
    )

fun String.toGameID(): Int = this.trim().removePrefix("Game ").toInt()

fun String.toRounds(): List<Round> = this.trim().split(";").map { it.toRound() }

fun String.toRound(): Round {
    var red = 0
    var green = 0
    var blue = 0

    this.split(",").map { it.trim() }.forEach {
        val amount = it.substringBefore(" ").toInt()
        when (it.substringAfter(" ")) {
            "red" -> red = amount
            "blue" -> blue = amount
            "green" -> green = amount
        }
    }

    return Round(red, green, blue)
}

fun part2() {
    val powerSum = File(gamesFile).bufferedReader().useLines { sumPower(it) }
    println("Power sum is $powerSum")
}

fun sumPower(lines: Sequence<String>) = lines.map { it.toGame().power() }.sum()

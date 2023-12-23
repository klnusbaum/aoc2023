package problems.day4.part1

import java.io.File
import kotlin.math.pow

//const val testFile = "input/day4/test.txt"
const val cardsFile = "input/day4/cards.txt"

fun main() {
    val cardValueSum = File(cardsFile).bufferedReader().useLines { sumCardValues(it) }
    println("Card Values Sum: $cardValueSum")
}

private fun sumCardValues(lines: Sequence<String>) = lines.map { it.toCard() }.sumOf { it.value() }

private fun String.toCard() = Card(
    id = this.toCardID(),
    winningNumbers = this.toWinningNumbers(),
    possessedNumbers = this.toPossessedNumbers()
)

private fun String.toCardID(): Int = this.substringBefore(":").substringAfter(" ").trim().toInt()

private fun String.toWinningNumbers() =
    this.substringAfter(":").substringBefore("|").trim().split(" ").filter { it != "" }.map { it.toInt() }
        .toSet()

private fun String.toPossessedNumbers(): Set<Int> =
    this.substringAfter("|").trim().split(" ").filter { it != "" }.map { it.trim().toInt() }.toSet()

private data class Card(val id: Int, val winningNumbers: Set<Int>, val possessedNumbers: Set<Int>) {
    fun value(): Int = 2.0.pow(numMatching() - 1).toInt()
    fun numMatching() = winningNumbers.intersect(possessedNumbers).count()
}

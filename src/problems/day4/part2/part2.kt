package problems.day4.part2

import java.io.File
import java.util.*

//const val testFile = "input/day4/test.txt"
const val cardsFile = "input/day4/cards.txt"

fun main() {
    val totalCardsWon = File(cardsFile).bufferedReader().useLines { totalCards(it) }
    println("Total Cards: $totalCardsWon")
}

private fun totalCards(lines: Sequence<String>): Int {
    val cards = lines.map { it.toCard() }.toList()
    val cardTable = cards.map { it.id to it }.toMap()
    val toProcess: Queue<Card> = LinkedList()
    toProcess.addAll(cards)

    var totalCards = 0
    while (toProcess.isNotEmpty()) {
        totalCards++
        val card = toProcess.remove()
        for (i in 1..card.numWinners) {
            toProcess.add(cardTable[card.id + i])
        }
    }

    return totalCards
}

private fun String.toCard() = Card(
    id = this.toCardID(),
    numWinners = this.toWinningNumbers().intersect(this.toPossessedNumbers()).count()
)

private fun String.toCardID(): Int = this.substringBefore(":").substringAfter(" ").trim().toInt()

private fun String.toWinningNumbers() =
    this.substringAfter(":").substringBefore("|").trim().split(" ").filter { it != "" }.map { it.toInt() }
        .toSet()

private fun String.toPossessedNumbers(): Set<Int> =
    this.substringAfter("|").trim().split(" ").filter { it != "" }.map { it.trim().toInt() }.toSet()

private data class Card(val id: Int, val numWinners: Int)

package problems.day7.part2

import java.io.File

private const val inputFile = "input/day7/input.txt"
//private const val testFile = "input/day7/test.txt"

fun main() {
    val scoreSum = File(inputFile).bufferedReader().useLines { sumScores(it) }
    println("Sum of scores is $scoreSum")
}

private fun sumScores(lines: Sequence<String>): Int {
    return lines.map { it.toHand() }
        .sorted()
        .foldIndexed(0) { index, acc, hand -> acc + ((index + 1) * hand.bid) }
}

private data class Hand(val cards: List<Card>, val bid: Int, val handType: HandType) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        if (this.handType == other.handType) {
            return this.cards.compareTo(other.cards)
        }

        return this.handType.compareTo(other.handType)
    }
}

private fun List<Card>.compareTo(other: List<Card>): Int {
    for (pair in this.asSequence().zip(other.asSequence())) {
        if (pair.first != pair.second) {
            return pair.first.compareTo(pair.second)
        }
    }

    return 0
}

private fun String.toHand(): Hand {
    val cards = this.substringBefore(" ").map { it.toCard() }
    val bid = this.substringAfter(" ").toInt()
    val handType = cards.toHandType()
    return Hand(cards, bid, handType)
}

private enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
}

private fun List<Card>.toHandType(): HandType {
    val typeHistogram = mutableMapOf<Card, UInt>()
    for (card in this) {
        typeHistogram[card] = typeHistogram.getOrDefault(card, 0u) + 1u
    }
    return strongestHandType(typeHistogram)
}

private fun strongestHandType(typeHistogram: Map<Card, UInt>): HandType {
    if (typeHistogram.getOrDefault(Card.JOKER, 0u) == 0u) {
        return simpleHandType(typeHistogram)
    }

    val removedJoker = typeHistogram.toMutableMap()
    removedJoker[Card.JOKER] = removedJoker[Card.JOKER]!! - 1u

    return Card.entries.filter { it != Card.JOKER }
        .maxOf {
            val possibleHisto = removedJoker.toMutableMap()
            possibleHisto[it] = possibleHisto.getOrDefault(it, 0u) + 1u
            strongestHandType(possibleHisto)
        }
}

private fun simpleHandType(typeHistogram: Map<Card, UInt>): HandType = when {
    typeHistogram.any { it.value == 5u } -> HandType.FIVE_OF_A_KIND
    typeHistogram.any { it.value == 4u } -> HandType.FOUR_OF_A_KIND
    typeHistogram.any { it.value == 3u } and typeHistogram.any { it.value == 2u } -> HandType.FULL_HOUSE
    typeHistogram.any { it.value == 3u } -> HandType.THREE_OF_A_KIND
    typeHistogram.filter { it.value == 2u }.count() == 2 -> HandType.TWO_PAIR
    typeHistogram.any { it.value == 2u } -> HandType.ONE_PAIR
    else -> HandType.HIGH_CARD
}

private enum class Card {
    JOKER,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    QUEEN,
    KING,
    ACE,
}

private fun Char.toCard() = when (this) {
    'J' -> Card.JOKER
    '2' -> Card.TWO
    '3' -> Card.THREE
    '4' -> Card.FOUR
    '5' -> Card.FIVE
    '6' -> Card.SIX
    '7' -> Card.SEVEN
    '8' -> Card.EIGHT
    '9' -> Card.NINE
    'T' -> Card.TEN
    'Q' -> Card.QUEEN
    'K' -> Card.KING
    'A' -> Card.ACE
    else -> throw IllegalArgumentException("Char $this does not have a corresponding card")
}

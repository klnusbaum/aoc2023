package problems.daythree.part2

import java.io.File

//const val testFile = "input/day3/test.txt"
const val part_numbers = "input/day3/part_numbers.txt"

fun main() {
//    val testGearRatioSum = File(testFile).bufferedReader().useLines { sumGearRatios(it) }
//    println("Test Gear ratio sum: $testGearRatioSum")
    val gearRatioSum = File(part_numbers).bufferedReader().useLines { sumGearRatios(it) }
    println("Part Number Sum: $gearRatioSum")
}

private fun sumGearRatios(lines: Sequence<String>): Int {
    val possibleGears = mutableMapOf<Int, Map<Int, Int>>()
    val stars = mutableMapOf<Int, Set<Int>>()
    lines.forEachIndexed {row, line ->
        val res = parseLine(line)
        possibleGears[row] = res.possibleGears
        stars[row] = res.stars
    }

    return stars.flatMap { it.value.toRowCols(it.key) }
        .mapNotNull { it.gearRatio(possibleGears) }
        .sum()
}

private fun parseLine(line: String) = LineResult(
    accumulateNumbers(line),
    recordStarts(line),
)

private data class LineResult(val possibleGears: Map<Int, Int>, val stars: Set<Int>)

private fun accumulateNumbers(line: String): Map<Int, Int> {
    val accumulator = PossibleGearAccumulator()
    line.forEach { accumulator.nextChar(it) }
    return accumulator.end()
}

private fun recordStarts(line: String): Set<Int> {
    val stars = mutableSetOf<Int>()
    line.forEachIndexed { index, c ->  if (c == '*') stars.add(index)}
    return stars
}

private fun Pair<Int, Int>.gearRatio(possibleGears: Map<Int, Map<Int, Int>>) : Int? {
    val adjacentNumbers = mutableSetOf<Int>()
    for (i in this.first-1..this.first+1) {
        for(j in this.second-1..this.second+1) {
            val number = possibleGears[i]?.get(j)
            if (number != null) {
                adjacentNumbers.add(number)
            }
        }
    }

    if (adjacentNumbers.count() != 2) {
        return null
    }

    return adjacentNumbers.fold(1) {acc, next -> acc * next}
}

private fun Set<Int>.toRowCols(row: Int): List<Pair<Int, Int>> = this.map {row to it}

class PossibleGearAccumulator() {
    private val possibleGears = mutableMapOf<Int,Int>()
    private val currentNumber = StringBuilder()
    private var currentCol = 0
    private var currentStartCol = 0
    private var currentEndCol = 0
    private var currentState = State.OUT_NUMBER

    private enum class State {
        IN_NUMBER, OUT_NUMBER
    }

    fun nextChar(character: Char) {
        when {
            (character in '0'..'9') and (currentState == State.OUT_NUMBER) -> {
                currentState = State.IN_NUMBER
                currentNumber.append(character)
                currentStartCol = currentCol
                currentEndCol = currentCol
            }

            (character in '0'..'9') and (currentState == State.IN_NUMBER) -> {
                currentNumber.append(character)
                currentEndCol = currentCol
            }

            (character !in '0'..'9') and (currentState == State.IN_NUMBER) -> {
                currentState = State.OUT_NUMBER
                recordPossiblePN()
                currentNumber.clear()
            }
        }
        currentCol++
    }

    private fun recordPossiblePN() {
        val number = currentNumber.toString().toInt()
        for (i in currentStartCol..currentEndCol) {
           possibleGears[i] = number
        }
    }

    fun end(): Map<Int, Int> {
        if (currentState == State.IN_NUMBER) {
            recordPossiblePN()
        }
        return possibleGears
    }
}
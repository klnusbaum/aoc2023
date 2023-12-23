package problems.day3.part1

import java.io.File

//const val testFile = "input/day3/test.txt"
const val part_numbers = "input/day3/part_numbers.txt"

fun main() {
    val partNumberSum = File(part_numbers).bufferedReader().useLines { sumPartNumbers(it) }
    println("Part Number Sum: $partNumberSum")
}

fun sumPartNumbers(lines: Sequence<String>): Int {
    val possiblePNs: MutableList<PossiblePN> = mutableListOf()
    val symbols: MutableMap<Int, Set<Int>> = mutableMapOf()
    lines.forEachIndexed {row, line ->
        val res = parseLine(line, row)
        possiblePNs.addAll(res.possiblePNs)
        symbols[row] = res.symbols
    }

    return possiblePNs.filter { it.adjacentToSymbol(symbols) }.sumOf { it.value }
}

fun parseLine(line: String, row: Int) = LineResult(
    accumulateNumbers(line, row),
    recordSymbols(line),
)

fun accumulateNumbers(line: String, row: Int): List<PossiblePN> {
    val accumulator = PossiblePNAccumulator(row)
    line.forEach { accumulator.nextChar(it) }
    return accumulator.end()
}

fun recordSymbols(line: String): Set<Int> {
    val symbols = mutableSetOf<Int>()
    line.forEachIndexed { index, c ->  if (c.isSymbol()) symbols.add(index)}
    return symbols
}

private fun Char.isSymbol() = (this !in '0'..'9') and (this != '.')

data class PossiblePN(val value: Int, val row: Int, val startCol: Int, val endCol: Int) {
    fun adjacentToSymbol(symbols: Map<Int, Set<Int>>): Boolean {
        return aboveRowContainsSymbol(symbols) or
                rowContainsSymbol(symbols) or
                belowRowContainsSymbols(symbols)
    }

    private fun aboveRowContainsSymbol(symbols: Map<Int, Set<Int>>) = checkRowSegment(symbols, row - 1)

    private fun rowContainsSymbol(symbols: Map<Int, Set<Int>>): Boolean {
        val targetRow = symbols[row] ?: return false
        return targetRow.contains(startCol - 1) or targetRow.contains(endCol + 1)
    }

    private fun belowRowContainsSymbols(symbols: Map<Int, Set<Int>>) = checkRowSegment(symbols, row + 1)

    private fun checkRowSegment(symbols: Map<Int, Set<Int>>, row: Int): Boolean {
        val targetRow = symbols[row] ?: return false
        for (col in startCol - 1..endCol + 1) {
            if (targetRow.contains(col)) return true
        }
        return false
    }
}

data class LineResult(val possiblePNs: List<PossiblePN>, val symbols: Set<Int>)

class PossiblePNAccumulator(private val row: Int) {
    private val possiblePNs = mutableListOf<PossiblePN>()
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
        possiblePNs.add(
            PossiblePN(
                value = currentNumber.toString().toInt(),
                row = row,
                startCol = currentStartCol,
                endCol = currentEndCol,
            )
        )
    }

    fun end(): List<PossiblePN> {
        if (currentState == State.IN_NUMBER) {
            recordPossiblePN()
        }
        return possiblePNs
    }
}
package problems.day9.part1

import java.io.File

//private const val testFile = "input/day9/test1.txt"
private const val inputFile = "input/day9/input.txt"

fun main() {
    val sumOfNextValues = File(inputFile).bufferedReader().useLines { sumNextValues(it) }
    println("Sum of next values: $sumOfNextValues")
}

private fun sumNextValues(lines: Sequence<String>) =
    lines.map { it.split(" ").map { num -> num.toInt() } }
        .map { nextValue(it) }
        .sum()

private fun nextValue(numbers: List<Int>): Int {
    if (numbers.all {it == 0}) {
        return 0
    }

    val diffs = differences(numbers)
    return numbers.last() + nextValue(diffs)
}

private fun differences(numbers: List<Int>) = numbers.windowed(2).map { it[1] - it[0]}
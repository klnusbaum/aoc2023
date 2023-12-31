package problems.day1

import java.io.File

private const val calibrationValues = "input/day1/calibration_values.txt"

fun main() {
    part1()
    part2()
}
private fun part1() {
    val sum = File(calibrationValues)
        .bufferedReader()
        .useLines { sumLines(it) }

    println("Sum of values is $sum")
}

private fun sumLines(lines: Sequence<String>) = lines.map { parseLine(it) }.sum()

private fun parseLine(input: String): Int {
    val numbers = input.filter { c -> c in '0'..'9' }
    return "${numbers.first()}${numbers.last()}".toInt()
}

private fun part2() {
    val sum = File(calibrationValues)
        .bufferedReader()
        .useLines { sumLinesV2(it) }

    println("Sum of values is $sum")
}

private fun sumLinesV2(lines: Sequence<String>) = lines.map { parseLineV2(it) }.sum()

private fun parseLineV2(input: String): Int {
    val first = input.findAnyOf(numTargets)?.second?.toNumString()
    val last = input.findLastAnyOf(numTargets)?.second?.toNumString()

    return "$first$last".toInt()
}

private val numTargets = listOf(
    "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
)

private fun String.toNumString() = when (this) {
    "one" -> "1"
    "two" -> "2"
    "three" -> "3"
    "four" -> "4"
    "five" -> "5"
    "six" -> "6"
    "seven" -> "7"
    "eight" -> "8"
    "nine" -> "9"
    else -> this
}
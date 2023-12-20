package problems.dayone

import java.io.File

fun part1() {
    val calibrationValues = File("input/day1/calibration_values.txt")
    val sum = calibrationValues.useLines { sumLines(it) }

    println("Sum of values is $sum")
}

fun sumLines(lines: Sequence<String>) = lines.map { parseLine(it) }.sum()

fun parseLine(input: String): Int {
    val nums = input.filter { c -> c in '0'..'9' }
    return "${nums.first()}${nums.last()}".toInt()
}

fun part2() {
    val calibrationValues = File("input/day1/calibration_values.txt")
    val sum = calibrationValues.useLines { sumLinesV2(it) }

    println("Sum of values is $sum")
}

fun sumLinesV2(lines: Sequence<String>) = lines.map{ parseLineV2(it) }.sum()

fun parseLineV2(input: String): Int {
    val first = input.findAnyOf(numTargets)?.second?.toNumString()
    val last = input.findLastAnyOf(numTargets)?.second?.toNumString()

    return "$first$last".toInt()
}

val numTargets = listOf(
    "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
)

fun String.toNumString() = when(this) {
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
package problems.dayone

import java.io.File

fun parseLine(input: String): Int {
    val nums = input.filter { c -> c in '0'..'9' }
    return "${nums.first()}${nums.last()}".toInt()
}

fun sumLines(lines: Sequence<String>) = lines.map { line -> parseLine(line) }.sum()

fun part1() {
    val calibrationValues = File("input/day1/calibration_values.txt")
    val sum = calibrationValues.useLines { lines -> sumLines(lines) }

    println("Sum of values is $sum")
}
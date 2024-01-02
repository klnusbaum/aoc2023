package problems.day9.part2


import java.io.File

//private const val testFile = "input/day9/test1.txt"
private const val inputFile = "input/day9/input.txt"

fun main() {
    val sumOfBackValues = File(inputFile).bufferedReader().useLines { sumBackValues(it) }
    println("Sum of back values: $sumOfBackValues")
}

private fun sumBackValues(lines: Sequence<String>) =
    lines.map { it.split(" ").map { num -> num.toInt() } }
        .map { backValue(it) }
        .sum()

private fun backValue(numbers: List<Int>): Int {
    if (numbers.all {it == 0}) {
        return 0
    }

    val diffs = differences(numbers)
    return numbers.first() - backValue(diffs)
}

private fun differences(numbers: List<Int>) = numbers.windowed(2).map { it[1] - it[0]}

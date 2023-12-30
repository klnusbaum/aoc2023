package problems.day6.part1

import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

const val inputFile = "input/day6/input.txt"
//const val testFile = "input/day6/test.txt"

fun main() {
    val productOfNumBeats = File(inputFile).bufferedReader().useLines { multiplyNumBeats(it) }
    println("Product of number of ways to beat all races: $productOfNumBeats")
}

private fun multiplyNumBeats(lines: Sequence<String>): Int {
    val iterator = lines.iterator()
    val times = iterator.next().toTimes()
    val maxes = iterator.next().toMaxes()
    val races = times.zip(maxes) { time, max -> Race(time, max) }
    return races.map { it.numBeats() }.fold(1) { acc, next -> next * acc }
}


private fun String.toTimes(): List<Int> {
    return this.substringAfter("Time: ")
        .trim()
        .split("\\s+".toRegex())
        .map { it.toInt() }
}

private fun String.toMaxes(): List<Int> {
    return this.substringAfter("Distance: ")
        .trim()
        .split("\\s+".toRegex())
        .map { it.toInt() }
}

private data class Race(val time: Int, val currentMax: Int) {
    fun numBeats(): Int {
        val a = -1
        val b = time
        val c = -currentMax

        val discriminant = sqrt(((b * b) - (4.0 * a * c)))
        val low = (-b + discriminant) / (2 * a)
        val high = (-b - discriminant) / (2 * a)
        val flooredHigh = floor(high)
        val ceiledLow = ceil(low)
        var numBeats = (flooredHigh - ceiledLow).toInt() + 1
        if (flooredHigh == high) {
            numBeats--
        }
        if (ceiledLow == low) {
            numBeats--
        }
        return numBeats
    }
}

package problems.day6.part2

import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

const val inputFile = "input/day6/input.txt"
//const val testFile = "input/day6/test.txt"

fun main() {
    val numberOfBeats = File(inputFile).bufferedReader().useLines { multiplyNumBeats(it) }
    println("The number of ways to be the current max is: $numberOfBeats")
}

private fun multiplyNumBeats(lines: Sequence<String>): Long {
    val iterator = lines.iterator()
    val time = iterator.next().toTime()
    val maxes = iterator.next().toMax()
    return Race(time, maxes).numBeats()
}


private fun String.toTime(): Long {
    return this.substringAfter("Time: ")
        .replace(" ","")
        .toLong()
}

private fun String.toMax(): Long {
    return this.substringAfter("Distance: ")
        .replace(" ","")
        .toLong()
}

private data class Race(val time: Long, val currentMax: Long) {
    fun numBeats(): Long {
        val a = -1
        val b = time
        val c = -currentMax

        val discriminant = sqrt(((b * b) - (4.0 * a * c)))
        val low = (-b + discriminant) / (2 * a)
        val high = (-b - discriminant) / (2 * a)
        val flooredHigh = floor(high)
        val ceiledLow = ceil(low)
        var numBeats = (flooredHigh - ceiledLow).toLong() + 1
        if (flooredHigh == high) {
            numBeats--
        }
        if (ceiledLow == low) {
            numBeats--
        }
        return numBeats
    }
}

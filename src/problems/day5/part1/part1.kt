package problems.day5.part1

import java.io.File
import java.util.*

//const val testFile = "input/day5/test.txt"
const val inputFile = "input/day5/input.txt"

fun main() {
    val lowestLocationNum = File(inputFile).bufferedReader().useLines { lowestLocationNumber(it) }
    println("Lowest Location Num: $lowestLocationNum")
}

private fun lowestLocationNumber(lines: Sequence<String>): Long {
    val almanac = lines.fold(AlmanacBuilder()) { builder, line -> builder.nextLine(line) }.build()
    return almanac.seeds.mapNotNull { almanac.seedLocation(it) }.min()
}

private class Almanac(val seeds: List<Long>, val maps: Map<String, OverrideMap>) {
    fun seedLocation(seed: Long): Long? {
        val soil = maps["seed-to-soil"]?.get(seed) ?: return null
        val fertilizer = maps["soil-to-fertilizer"]?.get(soil) ?: return null
        val water = maps["fertilizer-to-water"]?.get(fertilizer) ?: return null
        val light = maps["water-to-light"]?.get(water) ?: return null
        val temperature = maps["light-to-temperature"]?.get(light) ?: return null
        val humidity = maps["temperature-to-humidity"]?.get(temperature) ?: return null
        val location = maps["humidity-to-location"]?.get(humidity) ?: return null
        return location
    }
}


private class AlmanacBuilder {
    private var state = State.SEEDS
    private var currentMapName = ""
    private var currentMap = TreeMap<Long, Override>()
    private val maps = mutableMapOf<String, OverrideMap>()
    private val seeds = mutableListOf<Long>()

    fun nextLine(line: String): AlmanacBuilder {
        when (state) {
            State.SEEDS -> {
                seeds.addAll(line.toSeeds())
                state = State.SEEDS_BLANK
            }

            State.SEEDS_BLANK -> state = State.HEADER
            State.HEADER -> {
                currentMapName = line.substringBefore(" ")
                currentMap = TreeMap<Long, Override>()
                state = State.MAP
            }

            State.MAP -> {
                if (line != "") {
                    addOverride(line)
                } else {
                    recordMap()
                    state = State.HEADER
                }
            }
        }
        return this
    }

    fun addOverride(line: String) {
        val overrides = line.split(" ").map { it.toLong() }
        val srcStart = overrides[1]
        val srcEnd = overrides[1] + overrides[2] - 1
        val dstStart = overrides[0]
        currentMap[srcStart] = Override(srcStart, srcEnd, dstStart)
    }

    fun recordMap() {
        maps[currentMapName] = OverrideMap(currentMap)
    }

    fun build(): Almanac {
        if (state == State.MAP) {
            recordMap()
        }
        return Almanac(seeds, maps)
    }

    private enum class State {
        SEEDS,
        SEEDS_BLANK,
        HEADER,
        MAP,
    }
}

private fun String.toSeeds() = this.substringAfter(":").trim().split(" ").map { it.toLong() }

private class OverrideMap(val overrides: TreeMap<Long, Override>) {
    operator fun get(key: Long): Long {
        val possibleOverride = overrides.headMap(key, true).lastEntry()?.value ?: return key
        if (key <= possibleOverride.srcEnd) {
            return possibleOverride.dstStart + (key - possibleOverride.srcStart)
        }
        return key
    }
}

private data class Override(val srcStart: Long, val srcEnd: Long, val dstStart: Long)
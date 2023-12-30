package problems.day5.part2

import java.io.File
import java.util.*

//const val testFile = "input/day5/test.txt"
const val inputFile = "input/day5/input.txt"

fun main() {
    val lowestLocationNum = File(inputFile).bufferedReader().useLines { lowestLocationNumber(it) }
    println("Lowest Location Num: $lowestLocationNum")
}

private fun lowestLocationNumber(lines: Sequence<String>): Long? {
    val almanac = lines.fold(AlmanacBuilder()) { builder, line -> builder.nextLine(line) }.build()
    for (i in 0..<Long.MAX_VALUE) {
        if (almanac.locationHasSeed(i)) {
            return i
        }
    }
    return null
}

private class Almanac(val seeds: TreeMap<Long,Long>, val reverseMaps: Map<String, OverrideMap>) {
    fun locationHasSeed(location: Long): Boolean {
        val humidity = reverseMaps["humidity-to-location"]?.get(location) ?: return false
        val temperature = reverseMaps["temperature-to-humidity"]?.get(humidity) ?: return false
        val light = reverseMaps["light-to-temperature"]?.get(temperature) ?: return false
        val water = reverseMaps["water-to-light"]?.get(light) ?: return false
        val fertilizer = reverseMaps["fertilizer-to-water"]?.get(water) ?: return false
        val soil = reverseMaps["soil-to-fertilizer"]?.get(fertilizer) ?: return false
        val seed = reverseMaps["seed-to-soil"]?.get(soil) ?: return false
        val floorSeed = seeds.floorEntry(seed) ?: return false
        return floorSeed.key <= seed && seed <= floorSeed.value
    }
}


private class AlmanacBuilder {
    private var state = State.SEEDS
    private var currentMapName = ""
    private var currentMap = TreeMap<Long, Override>()
    private val maps = mutableMapOf<String, OverrideMap>()
    private val seeds = TreeMap<Long, Long>()

    fun nextLine(line: String): AlmanacBuilder {
        when (state) {
            State.SEEDS -> {
                seeds.putAll(line.toSeeds())
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
        val dstStart = overrides[0]
        val dstEnd = overrides[0] + overrides[2] - 1
        val srcStart = overrides[1]
        currentMap[dstStart] = Override(dstStart, dstEnd, srcStart)
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

private fun String.toSeeds(): TreeMap<Long, Long> {
    val seedRanges = TreeMap<Long,Long>()
    val pairs = this.substringAfter(":").trim().split(" ").iterator()
    while (pairs.hasNext()) {
        val start = pairs.next().toLong()
        val length = pairs.next().toLong()
        seedRanges[start] = start+length-1
    }
    return seedRanges
}

private class OverrideMap(val overrides: TreeMap<Long, Override>) {
    operator fun get(key: Long): Long {
        val possibleOverride = overrides.headMap(key, true).lastEntry()?.value ?: return key
        if (key <= possibleOverride.dstEnd) {
            return possibleOverride.srcStart + (key - possibleOverride.dstStart)
        }
        return key
    }
}

private data class Override(val dstStart: Long, val dstEnd: Long, val srcStart: Long)
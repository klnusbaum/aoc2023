package problems.day10.part1

import java.io.File

private const val testFile1 = "input/day10/test1.txt"
private const val inputFile = "input/day10/input.txt"

fun main() {
    val farthestTileDistance = File(inputFile).bufferedReader().useLines { farthestTileDistance(it) }
    println("The tile farthest from start is $farthestTileDistance tiles away")
}

fun farthestTileDistance(it: Sequence<String>): Int {
    val board = it.fold(BoardBuilder()) { builder, line -> builder.nextLine(line) }.build()
//    println("Board: ${board.tiles}")
//    println("start: ${board.startRow} ${board.startCol}")

    val totalDistance = board.totalPipeDistance()
    return totalDistance / 2
}

private class BoardBuilder {
    private val tiles = mutableMapOf<Location, Tile>()
    private var start: Location? = null
    private var rowIndex = 0

    fun nextLine(line: String): BoardBuilder {
        line.forEachIndexed { colIndex, c ->
            val tile = c.toTile()
            val location = Location(rowIndex, colIndex)
            if (tile is Tile.Start) {
                start = location
            }
            tiles[location] = tile
        }

        rowIndex++
        return this
    }

    fun build() = Board(
        tiles,
        start ?: throw IllegalArgumentException("No location found for start"),
    )
}

private class Board(val tiles: Map<Location, Tile>, val start: Location) {
    fun totalPipeDistance(): Int {
        var previousLocation = start
        var currentLocation = nextFromStart()
        var distance = 1
        while (currentLocation != start) {
            val nextLocation = nextLocation(previousLocation, currentLocation)
            previousLocation = currentLocation
            currentLocation = nextLocation
            distance++
        }
        return distance
    }

    private fun nextFromStart(): Location {
        val northernLocation = start.toNorth()
        if (tiles[northernLocation]?.opensOn(Opening.SOUTH) == true) {
            return northernLocation
        }

        val southernLocation = start.toSouth()
        if (tiles[southernLocation]?.opensOn(Opening.NORTH) == true) {
            return southernLocation
        }

        val westernLocation = start.toWest()
        if (tiles[westernLocation]?.opensOn(Opening.EAST) == true) {
            return westernLocation
        }

        val easternLocation = start.toEast()
        if (tiles[easternLocation]?.opensOn(Opening.WEST) == true) {
            return easternLocation
        }

        throw IllegalArgumentException("No tile accessible from start")
    }

    private fun nextLocation(previousLocation: Location, currentLocation: Location): Location {
        val currentTile = tiles[currentLocation] ?: throw IllegalArgumentException("No Tile at $currentLocation")

        if (currentTile.opensOn(Opening.NORTH)) {
            val northernLocation = currentLocation.toNorth()
            if (tiles[northernLocation]?.opensOn(Opening.SOUTH) == true && northernLocation != previousLocation) {
                return northernLocation
            }
        }

        if (currentTile.opensOn(Opening.SOUTH)) {
            val southernLocation = currentLocation.toSouth()
            if (tiles[southernLocation]?.opensOn(Opening.NORTH) == true && southernLocation != previousLocation) {
                return southernLocation
            }
        }

        if (currentTile.opensOn(Opening.WEST)) {
            val westernLocation = currentLocation.toWest()
            if (tiles[westernLocation]?.opensOn(Opening.EAST) == true && westernLocation != previousLocation) {
                return westernLocation
            }
        }

        if (currentTile.opensOn(Opening.EAST)) {
            val easternLocation = currentLocation.toEast()
            if (tiles[easternLocation]?.opensOn(Opening.WEST) == true && easternLocation != previousLocation) {
                return easternLocation
            }
        }

        throw IllegalArgumentException("No next tile from $currentLocation")
    }
}

private data class Location(val row: Int, val col: Int) {
    fun toNorth(): Location = Location(row - 1, col)
    fun toSouth(): Location = Location(row + 1, col)
    fun toWest(): Location = Location(row, col - 1)
    fun toEast(): Location = Location(row, col + 1)
}

private sealed class Tile {
    data class Pipe(val first: Opening, val second: Opening) : Tile()
    data object Ground : Tile()
    data object Start : Tile()

    fun opensOn(opening: Opening) = when (this) {
        is Pipe -> this.first == opening || this.second == opening
        is Start -> true
        else -> false
    }
}

private fun Char.toTile(): Tile = when (this) {
    '|' -> Tile.Pipe(Opening.NORTH, Opening.SOUTH)
    '-' -> Tile.Pipe(Opening.EAST, Opening.WEST)
    'L' -> Tile.Pipe(Opening.NORTH, Opening.EAST)
    'J' -> Tile.Pipe(Opening.NORTH, Opening.WEST)
    '7' -> Tile.Pipe(Opening.SOUTH, Opening.WEST)
    'F' -> Tile.Pipe(Opening.SOUTH, Opening.EAST)
    'S' -> Tile.Start
    else -> Tile.Ground
}


private enum class Opening {
    NORTH,
    SOUTH,
    EAST,
    WEST,
}
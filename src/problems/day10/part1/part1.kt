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
    private val tiles = mutableMapOf<Int, MutableMap<Int, Tile>>()
    private var startRow: Int? = null
    private var startCol: Int? = null
    private var rowIndex = 0

    fun nextLine(line: String): BoardBuilder {
        val row = mutableMapOf<Int, Tile>()
        line.forEachIndexed { colIndex, c ->
            val tile = c.toTile()
            if (tile is Tile.Start) {
                startRow = rowIndex
                startCol = colIndex
            }
            row[colIndex] = tile
        }

        tiles[rowIndex] = row
        rowIndex++
        return this
    }

    fun build() = Board(
        tiles,
        startRow ?: throw IllegalArgumentException("start x position not found"),
        startCol ?: throw IllegalArgumentException("start y position not found")
    )
}

private class Board(val tiles: Map<Int, Map<Int, Tile>>, val startRow: Int, val startCol: Int) {
    fun totalPipeDistance(): Int {
        var (previousRow, previousCol) = Pair(startRow, startCol)
        var (currentRow, currentCol) = nextFromStart(startRow, startCol)
        var distance = 1
        while (!isStart(currentRow, currentCol)) {
            val (nextRow, nextCol) = nextTile(previousRow, previousCol, currentRow, currentCol)
            previousRow = currentRow
            previousCol = currentCol
            currentRow = nextRow
            currentCol = nextCol
            distance++
        }
        return distance
    }

    private fun nextFromStart(currentRow: Int, currentCol: Int): Pair<Int, Int> {
        val northernTile = tiles[currentRow - 1]?.get(currentCol)
        if (northernTile?.opensOn(Opening.SOUTH) == true) {
            return Pair(currentRow - 1, currentCol)
        }

        val southernTile = tiles[currentRow + 1]?.get(currentCol)
        if (southernTile?.opensOn(Opening.NORTH) == true) {
            return Pair(currentRow + 1, currentCol)
        }
        val westernTile = tiles[currentRow]?.get(currentCol - 1)
        if (westernTile?.opensOn(Opening.EAST) == true) {
            return Pair(currentRow, currentCol - 1)
        }

        val easternTile = tiles[currentRow]?.get(currentCol + 1)
        if (easternTile?.opensOn(Opening.WEST) == true) {
            return Pair(currentRow, currentCol + 1)
        }

        throw IllegalArgumentException("No tile accessible from start")
    }

    private fun nextTile(previousRow: Int, previousCol: Int, currentRow: Int, currentCol: Int): Pair<Int, Int> {
        val currentTile =
            tiles[currentRow]?.get(currentCol) ?: throw IllegalArgumentException("No Tile at $currentRow $currentCol")
        if (currentTile.opensOn(Opening.NORTH) && !(currentRow - 1 == previousRow && currentCol == previousCol)) {
            if(tiles[currentRow-1]?.get(currentCol)?.opensOn(Opening.SOUTH) == true) {
                return Pair(currentRow-1, currentCol)
            }
        }
        if (currentTile.opensOn(Opening.SOUTH) && !(currentRow + 1 == previousRow && currentCol == previousCol)) {
            if(tiles[currentRow+1]?.get(currentCol)?.opensOn(Opening.NORTH) == true) {
                return Pair(currentRow+1, currentCol)
            }
        }
        if (currentTile.opensOn(Opening.WEST) && !(currentRow == previousRow && currentCol-1 == previousCol)) {
            if(tiles[currentRow]?.get(currentCol-1)?.opensOn(Opening.EAST) == true) {
                return Pair(currentRow, currentCol-1)
            }
        }
        if (currentTile.opensOn(Opening.EAST) && !(currentRow == previousRow && currentCol+1 == previousCol)) {
            if(tiles[currentRow]?.get(currentCol+1)?.opensOn(Opening.WEST) == true) {
                return Pair(currentRow, currentCol+1)
            }
        }

        throw IllegalArgumentException("No next tile from $currentRow $currentCol")
    }

    private fun isStart(row: Int, col: Int) = row == startRow && col == startCol

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
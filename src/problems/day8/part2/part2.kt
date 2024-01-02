package problems.day8.part2

import java.io.File

//private const val test3 = "input/day8/test3.txt"
private const val inputFile = "input/day8/input.txt"

fun main() {
    val stepsCount = File(inputFile).bufferedReader().useLines { numSteps(it) }
    println("Number of steps $stepsCount")
}

private fun numSteps(lines: Sequence<String>): Long {
    val (instructions, rules) = lines.fold(RuleSetBuilder()) { builder, line -> builder.nextLine(line) }.build()
    val startingLocations = rules.keys.filter { it.endsWith("A") }
    val cycleLengths = startingLocations.map { cycleLength(it, instructions.copy(), rules) }.map { it.toLong() }
    return leastCommonMultiple(cycleLengths)
}

private fun cycleLength(startPos: String, instructions: Instructions, rules: Map<String, Rule>): Int {
    var currentLocation = startPos
    while (!currentLocation.endsWith("Z")) {
        val nextDirection = instructions.next()
        val nextRule = rules[currentLocation] ?: throw IllegalArgumentException("No such location: $currentLocation")
        currentLocation =
            if (nextDirection == 'L') {
                nextRule.left
            } else {
                nextRule.right
            }
    }

    return instructions.directionCount
}

private fun leastCommonMultiple(numbers: List<Long>): Long =
    numbers.reduce { a, b -> leastCommonMultipleOfPair(a, b) }

private fun leastCommonMultipleOfPair(a: Long, b: Long): Long =
    (a * b) / greatestCommonDivisor(a, b)

private fun greatestCommonDivisor(a: Long, b: Long): Long = when {
    a == 0L -> b
    b == 0L -> a
    a > b -> greatestCommonDivisor(a % b, b)
    else -> greatestCommonDivisor(a, b % a)
}


private class Instructions(val directions: String) : Iterator<Char> {
    var directionCount = 0
    override fun hasNext() = true

    override fun next(): Char {
        val next = directions[directionCount % directions.count()]
        directionCount++
        return next
    }

    fun copy() = Instructions(this.directions)
}

private data class Rule(val left: String, val right: String)

private fun String.toRule(): Rule {
    val left = this.substringBefore(", ").trimStart { it == '(' }
    val right = this.substringAfter(", ").trimEnd { it == ')' }
    return Rule(left, right)
}

private data class ParseResult(val instructions: Instructions, val rules: Map<String, Rule>)

private class RuleSetBuilder {
    private var state = State.INSTRUCTIONS
    private var instructions: Instructions? = null
    private val rules = mutableMapOf<String, Rule>()

    private enum class State {
        INSTRUCTIONS,
        BLANK,
        RULES,
    }

    fun nextLine(line: String): RuleSetBuilder {
        when (state) {
            State.INSTRUCTIONS -> {
                instructions = Instructions(line)
                state = State.BLANK
            }

            State.BLANK -> {
                state = State.RULES
            }

            State.RULES -> {
                rules[line.substringBefore(" =")] = line.substringAfter("= ").toRule()
            }

        }
        return this
    }

    fun build() = ParseResult(
        instructions ?: throw IllegalArgumentException("missing instructions"),
        rules
    )
}

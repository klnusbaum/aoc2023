package problems.day8.part1

import java.io.File

//private const val test1file = "input/day8/test1.txt"
//private const val test2file = "input/day8/test2.txt"
private const val inputFile = "input/day8/input.txt"

fun main() {
    val stepsCount = File(inputFile).bufferedReader().useLines { numSteps(it) }
    println("Number of steps $stepsCount")
}

private fun numSteps(lines: Sequence<String>): Int {
    val (instructions, rules) = lines.fold(RuleSetBuilder()) { builder, line -> builder.nextLine(line) }.build()
    var currentLocation = "AAA"
    while (currentLocation != "ZZZ") {
        val nextDirection = instructions.next()
        val nextRule = rules[currentLocation] ?: throw IllegalArgumentException("No such location: $currentLocation")
        currentLocation = if (nextDirection == 'L') {
            nextRule.left
        } else {
            nextRule.right
        }
    }

    return instructions.directionCount
}


private class Instructions(val directions: String) : Iterator<Char> {
    var directionCount = 0
    override fun hasNext() = true

    override fun next(): Char {
        val next = directions[directionCount % directions.count()]
        directionCount++
        return next
    }

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
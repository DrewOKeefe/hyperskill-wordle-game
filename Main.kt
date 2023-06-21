package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

var start = 0L
var tries = 0
var display = """

""".trimIndent()
var incorrectChars = ""

fun argCheck(args: Array<String>) {
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        exitProcess(0)
    }
    if (!File(args[0]).exists()) {
        println("Error: The words file ${args[0]} doesn't exist.")
        exitProcess(0)
    }
    if (!File(args[1]).exists()) {
        println("Error: The candidate words file ${args[1]} doesn't exist.")
        exitProcess(0)
    }
    validWords(File(args[0]))
    validWords(File(args[1]))
    candidateCheck(File(args[0]), File(args[1]))
}

fun validWords(file: File) {
    val lines = file.readLines()
    var count = 0
    for (line in lines) {
        if (line.length != 5
            || line.lowercase().contains("[^a-zA-Z]".toRegex())
            || line.lowercase().contains("""(.).*\1+""".toRegex()))
            count++ else continue
    }
    if (count > 0) {
        println( "Error: $count invalid words were found in the $file file.")
        exitProcess(0)
    }
}

fun candidateCheck(file1: File, file2: File) {
    val lines1 = file1.readText().lowercase()
    val lines2 = file2.readLines()
    var count = 0
    lines2.forEach { if (!lines1.contains(it.lowercase())) count++ }
    if (count > 0) {
        println("Error: $count candidate words are not included in the $file1 file.")
        exitProcess(0)
    }
}

fun validInput(word: String, input:String) {
    var output = ""
    if (input == word) {
        if (tries == 0) {
            for (char in input) print("\u001B[48:5:10m${char.uppercase()}\u001B[0m")
            println("\n\nCorrect!\nAmazing luck! The solution was found at once.")
            exitProcess(0)
        } else {
            tries++
            for (char in input) display += ("\u001B[48:5:10m${char.uppercase()}\u001B[0m")
            println(display)
            val end = System.currentTimeMillis()
            println("\nCorrect!\nThe solution was found after $tries tries in ${end - start} seconds.")
            exitProcess(0)
        }
    } else {
        for (char in input) {
            if (word.contains(char)) {
                output += (if (input.indexOf(char) == word.indexOf(char)) "\u001B[48:5:10m${char.uppercase()}\u001B[0m"
                else "\u001B[48:5:11m${char.uppercase()}\u001B[0m")
            } else {
                output += "\u001B[48:5:7m${char.uppercase()}\u001B[0m"
                incorrectChars += char.uppercase()
            }
        }
        display += (output + "\n")
        println(display)
        println("\u001B[48:5:14m${incorrectChars.split("").toSet().sorted().joinToString("")}\u001B[0m")
        tries++
    }
}

fun main(args: Array<String>) {
    argCheck(args)
    println("Words Virtuoso")
    val word = File(args[1]).readLines()[Random.nextInt(File(args[1]).readLines().size)]
    while (true) {
        println("\nInput a 5-letter word:")
        val input = readln()
        when {
            input == "exit" -> println("\nThe game is over.").also { exitProcess(0) }
            input.length != 5 -> println("The input isn't a 5-letter word.")
            input.contains("[^a-zA-Z]".toRegex())-> println("One or more letters of the input aren't valid.")
            input.contains("""(.).*\1+""".toRegex()) -> println("The input has duplicate letters.")
            !File(args[0]).readLines().contains(input) -> println("The input word isn't included in my words list.")
            else -> {
                start = System.currentTimeMillis()
                validInput(word, input)
            }
        }
    }
}
package ru.spbau.mit.bachelors2015.veselov

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.BufferedTokenStream
import org.apache.commons.io.FileUtils
import ru.spbau.mit.bachelors2015.veselov.ast.AstFile
import ru.spbau.mit.bachelors2015.veselov.parser.LLexer
import ru.spbau.mit.bachelors2015.veselov.parser.LParser
import java.io.File
import java.nio.charset.Charset

class FailedToParseException : Exception()

fun buildAst(sourceCode: String): AstFile {
    val lLexer = LLexer(CharStreams.fromString(sourceCode))
    val lParser = LParser(BufferedTokenStream(lLexer))

    if (lParser.numberOfSyntaxErrors > 0) {
        throw FailedToParseException()
    }

    return AstFile.buildFromRuleContext(lParser.file())
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Invalid number of arguments. Should be 1")
        return
    }

    val charset: Charset? = null
    val sourceCode = FileUtils.readFileToString(File(args[0]), charset)

    try {
        buildAst(sourceCode).traverse(PrettyPrinter(System.out))
    } catch (e: FailedToParseException) {
        // all messages were written by antlr
    }
}

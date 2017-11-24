package ru.spbau.mit.bachelors2015.veselov

import ru.spbau.mit.bachelors2015.veselov.ast.AstNode
import ru.spbau.mit.bachelors2015.veselov.ast.Traverser
import java.io.PrintStream

class PrettyPrinter(private val stream: PrintStream) : Traverser {
    private var indentLevel: Int = 0

    override fun stepIn(node: AstNode) {
        stream.println(
            defaultIndent.repeat(indentLevel) +
                    node + "[" + node.line + ":" + node.column + "]")
        indentLevel++
    }

    override fun stepOut(node: AstNode) {
        indentLevel--
    }

    private companion object {
        val defaultIndent = "  "
    }
}

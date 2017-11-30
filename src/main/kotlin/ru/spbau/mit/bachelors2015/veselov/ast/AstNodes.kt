package ru.spbau.mit.bachelors2015.veselov.ast

import com.google.common.collect.ImmutableList
import ru.spbau.mit.bachelors2015.veselov.parser.LParser
import java.util.stream.Collectors

interface Traverser {
    fun stepIn(node: AstNode)
    fun stepOut(node: AstNode)
}

abstract class AstNode(val line: Int, val column: Int) {
    abstract fun traverse(traverser: Traverser)
}

class AstFile(
    val functions: ImmutableList<AstFunctionDefinition>,
    val body: AstBlock,
    line: Int,
    column: Int
) : AstNode(line, column) {
    override fun toString(): String {
        return "File"
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)

        for (function in functions) {
            function.traverse(traverser)
        }

        body.traverse(traverser)
        traverser.stepOut(this)
    }

    companion object {
        fun buildFromRuleContext(rule: LParser.FileContext): AstFile {
            return AstFile(
                ImmutableList.copyOf(
                    rule.functions.map { AstFunctionDefinition.buildFromRuleContext(it) }
                ),
                AstBlock.buildFromRuleContext(rule.block()),
                rule.start.line,
                rule.start.charPositionInLine
            )
        }
    }
}

class AstBlock(
    private val statements: ImmutableList<AstStatement>,
    line: Int,
    column: Int
) : AstNode(line, column) {
    override fun toString(): String {
        return "Block"
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)

        for (statement in statements) {
            statement.traverse(traverser)
        }

        traverser.stepOut(this)
    }

    companion object {
        fun buildFromRuleContext(rule: LParser.BlockContext): AstBlock {
            return AstBlock(
                ImmutableList.copyOf(
                    rule.statements.map { AstStatement.buildFromRuleContext(it) }
                ),
                rule.start.line,
                rule.start.charPositionInLine
            )
        }
    }
}

class AstFunctionDefinition(
        private val name: String,
        private val parameterNames: ImmutableList<String>,
        private val body: AstBlock,
        line: Int,
        column: Int
) : AstNode(line, column) {
    override fun toString(): String {
        return "FunctionDefinition " + name + " " +
                parameterNames.stream().collect(Collectors.joining(" "))
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        body.traverse(traverser)
        traverser.stepOut(this)
    }

    companion object {
        fun buildFromRuleContext(rule: LParser.FunctionDefinitionContext): AstFunctionDefinition {
            return AstFunctionDefinition(
                rule.functionName.text,
                ImmutableList.copyOf(
                        rule.parameterNames.map { it.text }
                ),
                AstBlock.buildFromRuleContext(rule.block()),
                rule.start.line,
                rule.start.charPositionInLine
            )
        }
    }
}

abstract class AstStatement(line: Int, column: Int) : AstNode(line, column) {
    companion object {
        fun buildFromRuleContext(rule: LParser.StatementContext) : AstStatement {
            return rule.accept(StatementContextVisitor)
        }
    }
}

class AstVariableDefinition(
    private val name: String,
    private val initializingExpression: AstExpression?,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "VariableDefinition " + name
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        initializingExpression?.traverse(traverser)
        traverser.stepOut(this)
    }
}

class AstWriteStatement(
    private val expression: AstExpression,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "WriteStatement"
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        expression.traverse(traverser)
        traverser.stepOut(this)
    }
}

abstract class AstExpression(line: Int, column: Int) : AstStatement(line, column) {
    companion object {
        fun buildFromRuleContext(rule: LParser.ExpressionContext) : AstExpression {
            return rule.accept(ExpressionContextVisitor)
        }
    }
}

class AstWhile(
    private val condition: AstExpression,
    private val body: AstBlock,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "WhileStatement"
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        condition.traverse(traverser)
        body.traverse(traverser)
        traverser.stepOut(this)
    }
}

class AstIf(
    private val condition: AstExpression,
    private val thenBody: AstBlock,
    private val elseBody: AstBlock?,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "IfStatement"
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        condition.traverse(traverser)
        thenBody.traverse(traverser)
        elseBody?.traverse(traverser)
        traverser.stepOut(this)
    }
}

class AstAssignment(
    private val identifier: String,
    private val expression: AstExpression,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "AssignmentStatement " + identifier
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        expression.traverse(traverser)
        traverser.stepOut(this)
    }
}

class AstVariableAccess(
    private val identifier: String,
    line: Int,
    column: Int
) : AstExpression(line, column) {
    override fun toString(): String {
        return "VariableAccess " + identifier
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        traverser.stepOut(this)
    }
}

class AstFunctionCall(
    private val identifier: String,
    private val argumentIdentifiers: ImmutableList<String>,
    line: Int,
    column: Int
) : AstExpression(line, column) {
    override fun toString(): String {
        return "FunctionCall " + identifier + ": " +
                argumentIdentifiers.stream().collect(Collectors.joining(" "))
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        traverser.stepOut(this)
    }
}

class AstIntegerLiteral(
    private val value: Int,
    line: Int,
    column: Int
) : AstExpression(line, column) {
    override fun toString(): String {
        return "IntegerLiteral " + value
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        traverser.stepOut(this)
    }
}

class AstFloatLiteral(
    private val value: Float,
    line: Int,
    column: Int
) : AstExpression(line, column) {
    override fun toString(): String {
        return "FloatLiteral " + value
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        traverser.stepOut(this)
    }
}

class AstBinaryExpression(
    private val operationType: BinaryOperation,
    private val leftOperand: AstExpression,
    private val rightOperand: AstExpression,
    line: Int,
    column: Int
) : AstExpression(line, column) {
    override fun toString(): String {
        return "BinaryExpression " + operationType
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        leftOperand.traverse(traverser)
        rightOperand.traverse(traverser)
        traverser.stepOut(this)
    }
}

class AstReadStatement(
    private val target: String,
    line: Int,
    column: Int
) : AstStatement(line, column) {
    override fun toString(): String {
        return "ReadStatement " + target
    }

    override fun traverse(traverser: Traverser) {
        traverser.stepIn(this)
        traverser.stepOut(this)
    }
}

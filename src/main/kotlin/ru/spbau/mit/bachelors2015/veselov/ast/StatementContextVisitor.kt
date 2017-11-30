package ru.spbau.mit.bachelors2015.veselov.ast

import com.google.common.collect.ImmutableList
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.bachelors2015.veselov.parser.LParser
import ru.spbau.mit.bachelors2015.veselov.parser.LVisitor

class NotAStatementError : Error()

object StatementContextVisitor : LVisitor<AstStatement> {
    override fun visitFunctionDefinition(ctx: LParser.FunctionDefinitionContext?): AstStatement {
        throw NotAStatementError()
    }

    override fun visitWriteStatement(ctx: LParser.WriteStatementContext): AstStatement {
        return AstWriteStatement(
            AstExpression.buildFromRuleContext(ctx.expression()),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitVariableDefinitionStatement(
        ctx: LParser.VariableDefinitionStatementContext
    ): AstStatement {
        val initializingExpression =
            if (ctx.initialValueExpression != null) {
                AstExpression.buildFromRuleContext(ctx.initialValueExpression)
            } else {
                null
            }

        return AstVariableDefinition(
            ctx.variableName.text,
            initializingExpression,
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitWhileStatement(ctx: LParser.WhileStatementContext): AstStatement {
        return AstWhile(
            AstExpression.buildFromRuleContext(ctx.condition),
            AstBlock.buildFromRuleContext(ctx.body),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitIfStatement(ctx: LParser.IfStatementContext): AstStatement {
        val elseBody =
            if (ctx.elseBody != null) AstBlock.buildFromRuleContext(ctx.elseBody)
            else null

        return AstIf(
            AstExpression.buildFromRuleContext(ctx.condition),
            AstBlock.buildFromRuleContext(ctx.thenBody),
            elseBody,
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitAssignmentStatement(ctx: LParser.AssignmentStatementContext): AstStatement {
        return AstAssignment(
            ctx.IDENTIFIER().text,
            AstExpression.buildFromRuleContext(ctx.expression()),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitFile(ctx: LParser.FileContext): AstStatement {
        throw NotAStatementError()
    }

    override fun visitTerminal(node: TerminalNode): AstStatement {
        throw NotAStatementError()
    }

    override fun visitBlock(ctx: LParser.BlockContext): AstStatement {
        throw NotAStatementError()
    }

    override fun visitChildren(node: RuleNode): AstStatement {
        throw NotAStatementError()
    }

    override fun visitBinaryExpression(ctx: LParser.BinaryExpressionContext): AstStatement {
        throw NotAStatementError()
    }

    override fun visitVariableAccessExpression(
        ctx: LParser.VariableAccessExpressionContext
    ): AstStatement {
        throw NotAStatementError()
    }

    override fun visitFunctionCallStatement(
        ctx: LParser.FunctionCallStatementContext?
    ): AstExpression {
        return AstFunctionCall(
                ctx!!.functionName.text,
                ImmutableList.copyOf(ctx.arguments.map { it.text }),
                ctx.start.line,
                ctx.start.charPositionInLine
        )
    }

    override fun visitIntegerLiteralExpression(
        ctx: LParser.IntegerLiteralExpressionContext
    ): AstStatement {
        throw NotAStatementError()
    }

    override fun visitFloatLiteralExpression(
        ctx: LParser.FloatLiteralExpressionContext
    ): AstStatement {
        throw NotAStatementError()
    }

    override fun visitReadStatement(ctx: LParser.ReadStatementContext?): AstStatement {
        return AstReadStatement(ctx!!.start.line, ctx.start.charPositionInLine)
    }

    override fun visitErrorNode(node: ErrorNode): AstStatement {
        throw NotAStatementError()
    }

    override fun visit(tree: ParseTree): AstStatement {
        throw NotAStatementError()
    }

    override fun visitExpressionInParentheses(
        ctx: LParser.ExpressionInParenthesesContext
    ): AstStatement {
        throw NotAStatementError()
    }
}

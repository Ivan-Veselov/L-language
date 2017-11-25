package ru.spbau.mit.bachelors2015.veselov.ast

import com.google.common.collect.ImmutableList
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.bachelors2015.veselov.parser.LParser
import ru.spbau.mit.bachelors2015.veselov.parser.LVisitor

class NotAnExpressionError : Error()

class UnknownOperationLiteral : Error()

object ExpressionContextVisitor : LVisitor<AstExpression> {
    override fun visitIntegerLiteralExpression(
        ctx: LParser.IntegerLiteralExpressionContext?
    ): AstExpression {
        return AstIntegerLiteral(
            java.lang.Integer.parseInt(ctx!!.DECIMAL_INTEGER_LITERAL().text),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitFloatLiteralExpression(
        ctx: LParser.FloatLiteralExpressionContext?
    ): AstExpression {
        return AstFloatLiteral(
            java.lang.Float.parseFloat(ctx!!.DECIMAL_FLOATING_POINT_LITERAL().text),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitVariableAccessExpression(
        ctx: LParser.VariableAccessExpressionContext?
    ): AstExpression {
        return AstVariableAccess(
            ctx!!.IDENTIFIER().text,
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitFunctionCallExpression(
        ctx: LParser.FunctionCallExpressionContext?
    ): AstExpression {
        return AstFunctionCall(
            ctx!!.IDENTIFIER().text,
            ImmutableList.copyOf(ctx.arguments.map { AstExpression.buildFromRuleContext(it) }),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitExpressionInParentheses(
        ctx: LParser.ExpressionInParenthesesContext?
    ): AstExpression {
        return ctx!!.expression().accept(this)
    }

    override fun visitBinaryExpression(ctx: LParser.BinaryExpressionContext?): AstExpression {
        return AstBinaryExpression(
            BinaryOperation.fromString(
                ctx!!.operation.text
            ) ?: throw UnknownOperationLiteral(),
            AstExpression.buildFromRuleContext(ctx.leftOperand),
            AstExpression.buildFromRuleContext(ctx.rightOperand),
            ctx.start.line,
            ctx.start.charPositionInLine
        )
    }

    override fun visitReadExpression(ctx: LParser.ReadExpressionContext?): AstExpression {
        return AstReadExpression(ctx!!.start.line, ctx.start.charPositionInLine)
    }

    override fun visitVariableDefinitionStatement(
        ctx: LParser.VariableDefinitionStatementContext?
    ): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitTerminal(node: TerminalNode?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitChildren(node: RuleNode?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitFile(ctx: LParser.FileContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitBlock(ctx: LParser.BlockContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitFunctionDefinitionStatement(
        ctx: LParser.FunctionDefinitionStatementContext?
    ): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitExpressionStatement(
        ctx: LParser.ExpressionStatementContext?
    ): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitWhileStatement(ctx: LParser.WhileStatementContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitIfStatement(ctx: LParser.IfStatementContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitAssignmentStatement(ctx: LParser.AssignmentStatementContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitReturnStatement(ctx: LParser.ReturnStatementContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitWriteStatement(ctx: LParser.WriteStatementContext?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visit(tree: ParseTree?): AstExpression {
        throw NotAnExpressionError()
    }

    override fun visitErrorNode(node: ErrorNode?): AstExpression {
        throw NotAnExpressionError()
    }
}
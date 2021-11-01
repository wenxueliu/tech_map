package com.wexueliu.bizhidao.auth;

import org.springframework.expression.ParseException;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.*;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.util.Assert;

import java.util.*;

/**
* @author liuwenxue
* @date 2021-10-31
*/
public class LogicExpressionParser {

    private SpelParserConfiguration configuration;

    // For rules that build nodes, they are stacked here for return
    private final Deque<SpelNodeImpl> constructedNodes = new ArrayDeque<>();

    private String expressionString;

    private int tokenStreamLength;

    private int tokenStreamPointer;

    private List<Token> tokenStream = Collections.emptyList();

    LogicExpressionParser() {
    }

    protected SpelExpression doParseExpression(String expressionString)
            throws ParseException {
        try {
            this.expressionString = expressionString;
            SpelNodeImpl ast = buildAst(expressionString);
            Token t = peekToken();
            if (t != null) {
                throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, toString(nextToken()));
            }
            Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");
            return new SpelExpression(expressionString, ast, this.configuration);
        }
        catch (InternalParseException ex) {
            throw ex.getCause();
        }
    }

    public SpelNodeImpl buildAst(String expressionString) {
        return buildAst(lexer(expressionString));
    }

    public SpelNodeImpl buildAst(List<Token> tokens) {
        this.tokenStreamLength = lexer(expressionString).size();
        this.tokenStreamPointer = 0;
        this.constructedNodes.clear();
        SpelNodeImpl ast = eatExpression();
        Assert.state(ast != null, "No node");
        return ast;
    }

    public List<Token> lexer(String expressionString) {
        Tokenizer tokenizer = new Tokenizer(expressionString);
        this.tokenStream = tokenizer.process();
        return this.tokenStream;
    }


    private Token peekToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer);
    }

    private Token nextToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer++);
    }

    public String toString(Token t) {
        if (t == null) {
            return "";
        }
        if (t.getKind().hasPayload()) {
            return t.stringValue();
        }
        return t.kind.toString().toLowerCase();
    }

    //	expression
    //    : logicalOrExpression
    //      ( (ASSIGN^ logicalOrExpression)
    //	    | (DEFAULT^ logicalOrExpression)
    //	    | (QMARK^ expression COLON! expression)
    //      | (ELVIS^ expression))?;
    private SpelNodeImpl eatExpression() {
        return eatLogicalOrExpression();
    }

    private SpelNodeImpl eatLogicalOrExpression() {
        SpelNodeImpl expr = eatLogicalAndExpression();
        while (peekLogicToken(TokenKind.OR)) {
            Token t = takeToken();  //consume OR
            SpelNodeImpl rhExpr = eatLogicalAndExpression();
            checkOperands(t, expr, rhExpr);
            expr = new OpOr(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    private SpelNodeImpl eatLogicalAndExpression() {
        SpelNodeImpl expr = eatRelationalExpression();
        while (peekLogicToken(TokenKind.AND)) {
            Token t = takeToken();  // consume 'AND'
            SpelNodeImpl rhExpr = eatRelationalExpression();
            checkOperands(t, expr, rhExpr);
            expr = new OpAnd(t.startPos, t.endPos, expr, rhExpr);
        }
        return expr;
    }

    private boolean peekLogicToken(TokenKind kind) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return (t.kind == kind);
    }

    private SpelNodeImpl eatRelationalExpression() {
        if (peekToken(TokenKind.NOT)) {
            Token t = takeToken();
            SpelNodeImpl expr = eatRelationalExpression();
            Assert.state(expr != null, "No node");
            if (t.kind == TokenKind.NOT) {
                return new OperatorNot(t.startPos, t.endPos, expr);
            }
            return new OpNot(t.startPos, t.endPos, expr);
        }
        return eatStartNode();
    }

    private SpelNodeImpl eatStartNode() {
        if (maybeEatLiteral()) {
            return pop();
        }
        else if (maybeEatParenExpression()) {
            return pop();
        }
        else {
            return null;
        }
    }

    // 遇到括号，取括号中间的表达式
    private boolean maybeEatParenExpression() {
        if (peekToken(TokenKind.LPAREN)) {
            nextToken();
            SpelNodeImpl expr = eatExpression();
            Assert.state(expr != null, "No node");
            eatToken(TokenKind.RPAREN);
            push(expr);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean peekToken(TokenKind desiredTokenKind) {
        return peekToken(desiredTokenKind, false);
    }

    private boolean peekToken(TokenKind desiredTokenKind, boolean consumeIfMatched) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == desiredTokenKind) {
            if (consumeIfMatched) {
                this.tokenStreamPointer++;
            }
            return true;
        }
        return false;
    }


    private Token eatToken(TokenKind expectedKind) {
        Token t = nextToken();
        if (t == null) {
            int pos = this.expressionString.length();
            throw internalException(pos, SpelMessage.OOD);
        }
        if (t.kind != expectedKind) {
            throw internalException(t.startPos, SpelMessage.NOT_EXPECTED_TOKEN,
                    expectedKind.toString().toLowerCase(), t.getKind().toString().toLowerCase());
        }
        return t;
    }

    //	literal
    //  : STRING_LITERAL
    //	| NULL_LITERAL
    // 开始字符
    private boolean maybeEatLiteral() {
        Token t = peekToken();
        if (t == null) {
            return false;
        } else if (t.kind == TokenKind.IDENTIFIER) {
            push(new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
        } else {
            return false;
        }
        nextToken();
        return true;
    }

    private boolean peekIdentifierToken(String identifierString) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return (t.kind == TokenKind.IDENTIFIER && identifierString.equalsIgnoreCase(t.stringValue()));
    }

    private Token takeToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            throw new IllegalStateException("No token");
        }
        return this.tokenStream.get(this.tokenStreamPointer++);
    }

    private void push(SpelNodeImpl newNode) {
        this.constructedNodes.push(newNode);
    }

    private SpelNodeImpl pop() {
        return this.constructedNodes.pop();
    }

    private InternalParseException internalException(int startPos, SpelMessage message, Object... inserts) {
        return new InternalParseException(new SpelParseException(this.expressionString, startPos, message, inserts));
    }

    private void checkOperands(Token token, SpelNodeImpl left, SpelNodeImpl right) {
        checkLeftOperand(token, left);
        checkRightOperand(token, right);
    }

    private void checkLeftOperand(Token token, SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.LEFT_OPERAND_PROBLEM);
        }
    }

    private void checkRightOperand(Token token, SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.RIGHT_OPERAND_PROBLEM);
        }
    }

    static void test1() {
        String expressionStr = "role:develop AND group:dev OR group:test AND role:system";
        LogicExpressionParser logicExpressionParser = new LogicExpressionParser();
        SpelExpression expression = logicExpressionParser.doParseExpression(expressionStr);
        System.out.println(expression);
    }

    static void test2() {
        String expressionStr = "role:develop AND ( group:dev OR group:test ) AND (NOT role:system)";
        LogicExpressionParser logicExpressionParser = new LogicExpressionParser();
        SpelExpression expression = logicExpressionParser.doParseExpression(expressionStr);
        System.out.println(expression.getAST().toStringAST());
    }

    static void test3() {
        String expressionStr = "role:develop OR ( group:dev OR group:test ) AND (NOT role:system)";
        LogicExpressionParser logicExpressionParser = new LogicExpressionParser();
        SpelExpression expression = logicExpressionParser.doParseExpression(expressionStr);
        System.out.println(expression.getAST().toStringAST());
    }

    public static void main(String[] args) {
        test3();
    }
}

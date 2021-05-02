package com.humanova;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    ArrayList<Token> tokens;
    Token currentToken;
    Token nextToken;
    int tokenIdx;

    static final HashMap<TokenType, BinaryOpType> AssignOpMap = new HashMap<TokenType, BinaryOpType>() {{
        put(TokenType.ADDEQ, BinaryOpType.ADD);
        put(TokenType.SUBEQ, BinaryOpType.SUB);
        put(TokenType.MULEQ, BinaryOpType.MUL);
        put(TokenType.DIVEQ, BinaryOpType.DIV);
        put(TokenType.OREQ,  BinaryOpType.OR);
        put(TokenType.ANDEQ, BinaryOpType.AND);
        put(TokenType.XOREQ, BinaryOpType.XOR);
        put(TokenType.MODEQ, BinaryOpType.MOD);
    }};

    static final HashMap<TokenType, BinaryOpType> TermOpMap = new HashMap<TokenType, BinaryOpType>() {{
        put(TokenType.PLUS,  BinaryOpType.ADD);
        put(TokenType.MINUS, BinaryOpType.SUB);
        put(TokenType.AND,   BinaryOpType.AND);
        put(TokenType.OR,    BinaryOpType.OR);
        put(TokenType.XOR,   BinaryOpType.XOR);
        put(TokenType.LOGICALAND, BinaryOpType.LOGICALAND);
        put(TokenType.LOGICALOR,  BinaryOpType.LOGICALOR);
        put(TokenType.MOD, BinaryOpType.MOD);
    }};

    static final HashMap<TokenType, BinaryOpType> FactorOpMap = new HashMap<TokenType, BinaryOpType>() {{
        put(TokenType.MUL, BinaryOpType.MUL);
        put(TokenType.DIV, BinaryOpType.DIV);
    }};

    public Parser() { }

    private void advance() {
        if (tokenIdx < tokens.size()-1) {
            tokenIdx++;
            currentToken = tokens.get(tokenIdx);
            if (tokenIdx < tokens.size()-2) {
                nextToken = tokens.get(tokenIdx+1);
            } else nextToken = null;
        } else {
            currentToken = null;
            nextToken = null;
        }
    }

    public AST.Node parse(ArrayList<Token> tokens) {
        this.tokens = tokens;
        tokenIdx = -1;
        advance();

        if (currentToken == null) {
            return null;
        }
        if (currentToken.type == TokenType.ID
                && (nextToken != null)
                && (AssignOpMap.containsKey(nextToken.type)
                || nextToken.type == TokenType.EQ)) {
            return parseStmt();
        }
        else {
            return parseExpr();
        }
    }

    private AST.Stmt parseStmt() {
        AST.Stmt stmt = null;

        // assign stmt
        if (currentToken.type == TokenType.ID) {
            stmt = parseAssignStmt();
        } else {
            raiseParserException();
        }

        return stmt;
    }

    // grammar = assign_stmt : id (+-*/%&^|)= expr
    private AST.AssignStmt parseAssignStmt() {
        AST.AssignStmt assignStmt = null;
        AST.IdNode id = parseId();

        if (currentToken.type == TokenType.EQ) {
            advance();
            assignStmt = new AST.AssignStmt(id, parseExpr());
        }
        else if (AssignOpMap.containsKey(currentToken.type)) {
            BinaryOpType op = AssignOpMap.get(currentToken.type);
            advance();
            assignStmt = new AST.AssignStmt(id, parseExpr(), op);
        }
        else {
            raiseParserException();
        }

        return assignStmt;
    }

    // grammar = expr : term ((+|-) term)+
    private AST.Expr parseExpr() {
        AST.Expr expr = null;
        AST.Expr term = parseTerm();
        expr = term;

        while (currentToken != null && TermOpMap.containsKey(currentToken.type)) {
            BinaryOpType op = TermOpMap.get(currentToken.type);
            advance();
            expr = new AST.BinaryOp(op, term, parseTerm());
        }

        return expr;
    }

    // grammar = term : factor ((*|/) factor)+
    private AST.Expr parseTerm() {
        AST.Expr term = null;
        AST.Expr leftFactor = parseFactor();
        term = leftFactor;

        while (currentToken != null && FactorOpMap.containsKey(currentToken.type)) {
            BinaryOpType op = FactorOpMap.get(currentToken.type);
            advance();
            term = new AST.BinaryOp(op, leftFactor, parseTerm());
        }

        return term;
    }

    // grammar = factor : num | +num | -num | "(" expr ")" | id
    private AST.Expr parseFactor() {
        Token currTok = currentToken;
        AST.Expr factor = null;

        if (currTok.type == TokenType.LPAREN) {
            advance();
            factor = parseExpr();

            if (currentToken.type != TokenType.RPAREN) {
                raiseParserException();
            }
            advance();
        }
        else if (currTok.type == TokenType.NUM) {
            advance();
            factor = new AST.Num(Double.parseDouble(currTok.value));
        }
        else if (currTok.type == TokenType.PLUS) {
            advance();
            factor = new AST.UnaryOp(BinaryOpType.ADD, parseFactor());
        }
        else if (currTok.type == TokenType.MINUS) {
            advance();
            factor = new AST.UnaryOp(BinaryOpType.SUB, parseFactor());
        }
        else if (currTok.type == TokenType.ID) {
            advance();
            factor = new AST.IdNode(currTok.value);
        }
        else {
            raiseParserException();
        }

        return factor;
    }

    private AST.IdNode parseId() {
        AST.IdNode idNode = null;

        if (currentToken.type == TokenType.ID) {
            idNode = new AST.IdNode(currentToken.value);
            advance();
        }

        return idNode;
    }

    public void raiseParserException() {
        throw new RuntimeException(String.format("[Parser] Invalid syntax, current token : '%s'", currentToken.toString()));
    }
}

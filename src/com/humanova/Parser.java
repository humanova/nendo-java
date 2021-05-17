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
        put(TokenType.MODEQ, BinaryOpType.MOD);
    }};

    static final HashMap<TokenType, BinaryOpType> TermOpMap = new HashMap<TokenType, BinaryOpType>() {{
        put(TokenType.PLUS,  BinaryOpType.ADD);
        put(TokenType.MINUS, BinaryOpType.SUB);
    }};

    static final HashMap<TokenType, BinaryOpType> FactorOpMap = new HashMap<TokenType, BinaryOpType>() {{
        put(TokenType.MUL, BinaryOpType.MUL);
        put(TokenType.DIV, BinaryOpType.DIV);
        put(TokenType.MOD, BinaryOpType.MOD);
    }};

    private boolean containsAssignOp() {
        for (Token t : this.tokens) {
            if (t.type == TokenType.EQ || AssignOpMap.containsKey(t.type))
                return true;
        }
        return false;
    }

    private boolean containsLoopOp() {
        for (Token t : this.tokens) {
            if (t.type == TokenType.LOOP)
                return true;
        }
        return false;
    }

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
        if (containsAssignOp() || containsLoopOp()) {
            return parseStmt();
        }
        else {
            return parseExpr();
        }
    }

    private AST.Stmt parseStmt() {
        AST.Stmt stmt = null;

        // assign stmt
        if (currentToken.type == TokenType.ID
                && (nextToken.type == TokenType.EQ || AssignOpMap.containsKey(nextToken.type))) {
            stmt = parseAssignStmt();
        }
        else if (currentToken.type == TokenType.ID
                && nextToken.type == TokenType.LPAREN){
            stmt = parseFuncDeclStmt();
        }
        else if (currentToken.type == TokenType.LOOP) {
            stmt = parseLoopStmt();
        }
        else {
            raiseParserException("could not parse statement");
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
            raiseParserException("could not parse assign statement (expected '=' or an operator)");
        }

        return assignStmt;
    }

    private AST.FuncDeclStmt parseFuncDeclStmt() {
        AST.FuncDeclStmt funcDeclStmt = null;
        AST.IdNode funcId = parseId();
        AST.Expr funcExpr = null;
        ArrayList<AST.IdNode> params = new ArrayList<AST.IdNode>();

        if (currentToken.type == TokenType.LPAREN) {
            params = parseFuncParams();

            if (currentToken.type == TokenType.EQ) {
                advance();
                funcExpr = parseExpr();
                funcDeclStmt = new AST.FuncDeclStmt(funcId, funcExpr, params);
            } else {
                raiseParserException("could not parse function declaration (expected '=')");
            }
        } else {
            raiseParserException("could not parse function declaration (expected '('");
        }

        return funcDeclStmt;
    }

    // loop   expr    :    stmt, stmt,...
    //   iteration count     body
    private AST.LoopStmt parseLoopStmt() {
        AST.LoopStmt loopStmt = null;
        AST.Expr iterationExpr;
        ArrayList<AST.AssignStmt> body = new ArrayList<AST.AssignStmt>();

        advance(); // skip 'loop'

        iterationExpr = parseExpr();
        advance(); // skip ":"

        while (currentToken != null) {
            body.add(parseAssignStmt());

            if (currentToken != null && currentToken.type != TokenType.COMMA) {
                raiseParserException("could not parse loop statement body (expected ',' or EOL)");
            }

            if (currentToken != null)
                advance(); // skip ','
        }
        loopStmt = new AST.LoopStmt(iterationExpr, body);

        return loopStmt;
    }

    // grammar = expr : term ((+|-) term)+
    private AST.Expr parseExpr() {
        AST.Expr expr = parseTerm(); // get left expr

        while (currentToken != null && TermOpMap.containsKey(currentToken.type)) {
            BinaryOpType op = TermOpMap.get(currentToken.type);
            advance();
            expr = new AST.BinaryOp(op, expr, parseTerm());
        }

        return expr;
    }

    // grammar = term : factor ((*|/) factor)+
    private AST.Expr parseTerm() {
        AST.Expr term = parseFactor(); // get left term

        while (currentToken != null && FactorOpMap.containsKey(currentToken.type)) {
            BinaryOpType op = FactorOpMap.get(currentToken.type);
            advance();
            term = new AST.BinaryOp(op, term, parseTerm());
        }

        return term;
    }

    // grammar = factor : num | +num | -num | "(" expr ")" | id | func_id(arg_id,...)
    private AST.Expr parseFactor() {
        Token currTok = currentToken;
        AST.Expr factor = null;

        if (currTok.type == TokenType.LPAREN) {
            advance();
            factor = parseExpr();

            if (currentToken.type != TokenType.RPAREN) {
                raiseParserException("could not parse the expression inside parenthesis (expected ')')");
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
        else if (currTok.type == TokenType.ID
                && nextToken != null
                && nextToken.type == TokenType.LPAREN) {
            AST.IdNode funcId = parseId();
            factor = new AST.FuncCall(funcId, parseFuncArgs());
        }
        else if (currTok.type == TokenType.ID) {
            advance();
            factor = new AST.IdNode(currTok.value);
        }
        else {
            raiseParserException("could not parse factor");
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

    private ArrayList<AST.Node> parseFuncArgs() {
        if (currentToken.type == TokenType.LPAREN)
            advance(); // skip '('
        else raiseParserException("could not parse function arguments (expected '(')");

        ArrayList<AST.Node> args = new ArrayList<AST.Node>();
        while (currentToken.type != TokenType.RPAREN) {
            if (currentToken.type == TokenType.ID
                    && nextToken != null
                    && nextToken.type != TokenType.LPAREN
                    && nextToken.type == TokenType.COMMA)
                args.add(parseId());
            else
                args.add(parseExpr());

            // if the current token isn't ',' or ')'
            if (currentToken.type != TokenType.COMMA && currentToken.type != TokenType.RPAREN) {
                raiseParserException("could not parse function arguments (expected ',' or ')')");
            }
            if (currentToken.type != TokenType.RPAREN)
                advance(); // skip ','
        }
        advance(); // skip ')'

        return args;
    }

    private ArrayList<AST.IdNode> parseFuncParams() {
        if (currentToken.type == TokenType.LPAREN)
            advance(); // skip '('
        else raiseParserException("could not parse function parameters (expected '(')");

        ArrayList<AST.IdNode> params = new ArrayList<AST.IdNode>();
        while (currentToken.type != TokenType.RPAREN) {
            if (currentToken.type == TokenType.ID)
                params.add(parseId());
            else raiseParserException("function parameters must be an identifier (id)");

            // if the current token isn't ',' or ')'
            if (currentToken.type != TokenType.COMMA && currentToken.type != TokenType.RPAREN) {
                raiseParserException("could not parse function parameters (expected ',' or ')')");
            }
            if (currentToken.type != TokenType.RPAREN)
                advance(); // skip ','
        }
        advance(); // skip ')'

        return params;
    }

    public void raiseParserException(String err) {
        throw new RuntimeException(String.format("[Parser] Invalid syntax : %s\n\tcurrent token : '%s'", err, currentToken.toString()));
    }
}

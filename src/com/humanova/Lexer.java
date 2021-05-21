package com.humanova;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private String text;
    private int textIdx;
    private char currentChar;

    // --  Define characters which will get tokenized
    private String WHITESPACE = " \n\t";
    private String DIGITS = "0123456789";
    private String IDS = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String OPS = "+-*/%";
    private char COMMENT = '#';
    private char DECIMAL_POINT = '.';

    // --  Define "str -> token" map
    private final HashMap<String, TokenType> TokenMap = new HashMap<String, TokenType>() {{
        put("+",  TokenType.PLUS);
        put("-",  TokenType.MINUS);
        put("*",  TokenType.MUL);
        put("/",  TokenType.DIV);
        put("%",  TokenType.MOD);
        put("+=", TokenType.ADDEQ);
        put("-=", TokenType.SUBEQ);
        put("*=", TokenType.MULEQ);
        put("/=", TokenType.DIVEQ);
        put("%=", TokenType.MODEQ);
    }};

    // ----

    public Lexer() { }

    private void advance() {
        if (textIdx < text.length()-1) {
            textIdx++;
            currentChar = text.charAt(textIdx);
        } else {
            currentChar = '\0';
        }
    }

    public ArrayList<Token> generateTokens(String text) {
        this.text = text;
        ArrayList<Token> tokens = new ArrayList<Token>();
        textIdx = -1;

        advance();

        while (currentChar != '\0') {
            if (WHITESPACE.indexOf(currentChar) != -1) {
                advance();
            }
            else if (DIGITS.indexOf(currentChar) != -1) {
                tokens.add(generateNumber());
            }
            else if (currentChar == DECIMAL_POINT) {
                tokens.add(generateNumber());
            }
            else if (IDS.indexOf(currentChar) != -1) {
                tokens.add(generateId());
            }
            else if (OPS.indexOf(currentChar) != -1) {
                tokens.add(generateOp());
            }
            else if (currentChar == '(') {
                tokens.add(new Token(TokenType.LPAREN));
                advance();
            }
            else if (currentChar == ')') {
                tokens.add(new Token(TokenType.RPAREN));
                advance();
            }
            else if (currentChar == '=') {
                tokens.add(new Token(TokenType.EQ));
                advance();
            }
            else if (currentChar == ',') {
                tokens.add(new Token(TokenType.COMMA));
                advance();
            }
            else if (currentChar == ':') {
                tokens.add(new Token(TokenType.COLON));
                advance();
            }
            else if (currentChar == COMMENT) {
                while (!(currentChar == '\n' || currentChar == '\0'))
                    advance();
            }
            else {
                throw new RuntimeException(String.format("Illegal Character : %s", currentChar));
            }
        }
        return tokens;
    }

    private Token generateNumber() {
        boolean decimalPointUsed = currentChar == DECIMAL_POINT;
        StringBuilder numberStr = new StringBuilder("" + currentChar);
        advance();

        // must be a char
        while (currentChar != '\0'
                && (DIGITS.indexOf(currentChar) != -1 || (currentChar == DECIMAL_POINT && !decimalPointUsed))) {
            numberStr.append(currentChar);
            if (currentChar == DECIMAL_POINT)
                decimalPointUsed = true;
            advance();
        }

        return new Token(TokenType.NUM, numberStr.toString());
    }

    private Token generateOp() {
        StringBuilder opStr = new StringBuilder("" + currentChar);
        advance();

        if (currentChar == '=') {
            opStr.append("=");
            advance();
        }

        return new Token(TokenMap.get(opStr.toString()));
    }

    private Token generateId() {
        StringBuilder idStr = new StringBuilder("" + currentChar);
        advance();

        // must be a number or char
        while (currentChar != '\0' && (IDS.indexOf(currentChar) != -1 || DIGITS.indexOf(currentChar) != -1)) {
            idStr.append(currentChar);
            advance();
        }

        if (idStr.toString().equals("loop")) {
            return new Token(TokenType.LOOP);
        }

        return new Token(TokenType.ID, idStr.toString());
    }
}

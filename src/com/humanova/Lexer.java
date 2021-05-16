package com.humanova;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    String text;
    int textIdx;
    char currentChar;
    ArrayList<Token> tokens;

    // --  Define characters which will get tokenized
    String WHITESPACE = " \n\t";
    String DIGITS = "0123456789";
    String IDS = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String OPS = "+-*/^|&%";
    char COMMENT = '#';
    char DECIMAL_POINT = '.';

    // --  Define "str -> token" map
    private final HashMap<String, TokenType> TokenMap = new HashMap<String, TokenType>() {{
        put("+",  TokenType.PLUS);
        put("-",  TokenType.MINUS);
        put("*",  TokenType.MUL);
        put("/",  TokenType.DIV);
        put("||", TokenType.LOGICALOR);
        put("&&", TokenType.LOGICALAND);
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
        textIdx = -1;
        tokens = new ArrayList<Token>();
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
            else if (currentChar == COMMENT) {
                while (currentChar != '\n')
                    advance();
            }
            else {
                throw new RuntimeException(String.format("Illegal Character : %s", currentChar));
            }
        }
        return tokens;
    }

    public Token generateNumber() {
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

    public Token generateOp() {
        StringBuilder opStr = new StringBuilder("" + currentChar);
        advance();

        if (currentChar == '=') {
            opStr.append("=");
            advance();
        }

        return new Token(TokenMap.get(opStr.toString()));
    }

    public Token generateId() {
        StringBuilder idStr = new StringBuilder("" + currentChar);
        advance();

        // must be a number or char
        while (currentChar != '\0' && (IDS.indexOf(currentChar) != -1 || DIGITS.indexOf(currentChar) != -1)) {
            idStr.append(currentChar);
            advance();
        }

        return new Token(TokenType.ID, idStr.toString());
    }
}

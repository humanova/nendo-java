package com.humanova;

enum TokenType {
    NUM,
    PLUS,
    MINUS,
    MUL,
    DIV,
    LOGICALOR,
    LOGICALAND,
    MOD,
    LPAREN,
    RPAREN,
    // SEMICOLON,
    ID,
    EQ,
    // RCURLY,
    // LCURLY,
    FUNC,
    RET,
    ADDEQ,
    SUBEQ,
    MULEQ,
    DIVEQ,
    MODEQ,
    COMMA
}

public class Token {
    TokenType type;
    String value = "";

    public Token(TokenType tk) {
        this.type = tk;
    }

    public Token(TokenType tk, String value) {
        this.type = tk;
        this.value = value;
    }

    public String toString() {
        return String.format("[%s : %s]", type.name(), value);
    }
}

package com.humanova;

enum TokenType {
    NUM,
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD,
    LPAREN,
    RPAREN,
    ID,
    EQ,
    ADDEQ,
    SUBEQ,
    MULEQ,
    DIVEQ,
    MODEQ,
    COMMA,
    COLON,
    LOOP
}

public class Token {
    public TokenType type;
    public String value = "";

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

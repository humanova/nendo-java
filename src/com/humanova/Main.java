package com.humanova;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String text = "nendo = 3 * 564";

	    Lexer lexer = new Lexer(text);
	    ArrayList<Token> tokens = lexer.generateTokens();

	    Parser parser = new Parser(tokens);

	    System.out.println("Text : " + text);
	    System.out.println("Tokens : " + tokens.toString());
    }
}

package com.humanova;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	static private void Test() {
		//String text1 = "nendo = 3 * (564 / -4)";
		String text1 = "f(a) + 4";
		String text2 = "2 * 2 / -2 - 3 + 1";

		Lexer lexer = new Lexer();
		ArrayList<Token> tokens1 = lexer.generateTokens(text1);
		ArrayList<Token> tokens2 = lexer.generateTokens(text2);

		Parser parser = new Parser();
		AST.Node ast1 = parser.parse(tokens1);
		AST.Node ast2 = parser.parse(tokens2);

		System.out.println("Text1 : " + text1);
		System.out.println("Tokens1 : " + tokens1.toString());
		System.out.println("AST1 : " + ast1.toString());

		System.out.println("Text2 : " + text2);
		System.out.println("Tokens2 : " + tokens2.toString());
		System.out.println("AST2 : " + ast2.toString());
	}

    public static void main(String[] args) {
		Test();

		Interpreter interpreter = new Interpreter();
		Scanner scn = new Scanner(System.in);
		String input = "";

		while (!input.equals("quit()")) {
			System.out.printf("> ");
			input = scn.nextLine();
			if (!input.equals(""))
				interpreter.interpret(input);
		}

    }
}

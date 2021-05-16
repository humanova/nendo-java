package com.humanova;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

	static private void Test() {
		String text1 = "nendo = 3 * (564 / -4)";
		String text2 = "func(a,b,c) = 0";

		Lexer lexer = new Lexer();
		ArrayList<Token> tokens1 = lexer.generateTokens(text1);
		ArrayList<Token> tokens2 = lexer.generateTokens(text2);

		Parser parser = new Parser();
		AST.Node ast1 = parser.parse(tokens1);
		AST.Node ast2 = parser.parse(tokens2);

		Interpreter interpreter = new Interpreter();

		System.out.println("Text1 : " + text1);
		System.out.println("Tokens1 : " + tokens1.toString());
		System.out.println("AST1 : " + ast1.toString());
		interpreter.interpret(ast1);

		System.out.println("Text2 : " + text2);
		System.out.println("Tokens2 : " + tokens2.toString());
		System.out.println("AST2 : " + ast2.toString());
		interpreter.interpret(ast2);
	}

    public static void main(String[] args) {
		//Test();
		Interpreter interpreter = new Interpreter();

		// commandline interpreter mode
		if (args.length == 0) {
			Scanner scn = new Scanner(System.in);
			String input = "";
			while (!input.equals("quit()")) {
				System.out.printf("> ");
				input = scn.nextLine();
				if (!input.equals(""))
					interpreter.interpret(input);
			}
		}
		else if (args.length == 1){
			List<String> lines = new ArrayList<>();

			try (BufferedReader br = Files.newBufferedReader(Paths.get(args[0]))) {
				lines = br.lines().collect(Collectors.toList());
			}
			catch (IOException e) {
				System.out.println("couldn't read the source file");
				//e.printStackTrace();
			}

			for (String ln : lines){
				if (!ln.replaceAll("\\s+","").equals(""))
					interpreter.interpret(ln);
			}

		}
		else {
			System.out.println("invalid argument count");
		}
    }
}

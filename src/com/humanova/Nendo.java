package com.humanova;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Nendo {

    public static void main(String[] args) {
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
				interpreter.interpret(ln);
			}

		}
		else {
			System.out.println("invalid argument count");
		}
    }
}

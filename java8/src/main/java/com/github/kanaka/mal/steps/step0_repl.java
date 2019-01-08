package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class step0_repl {
	
	static String read(String input) {
		return input;
	}
	
	static String eval(String input) {
		return input;
	}

	static String print(String input) {
		return input;
	}
	
	static String rep(String input) {
		return print(eval(read(input)));
	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				System.out.print("user> ");
				System.out.flush();
				String inputLine = in.readLine();
				if (inputLine == null) {
					System.out.println("\nBye!");
					break;
				}
				System.out.println(rep(inputLine));
				System.out.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}

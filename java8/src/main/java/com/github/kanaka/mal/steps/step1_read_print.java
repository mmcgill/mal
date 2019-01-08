package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.kanaka.mal.MalException;
import com.github.kanaka.mal.Reader;
import com.github.kanaka.mal.Value;

public class step1_read_print {
	
	static Value read(String input) {
		return new Reader(input).readForm();
	}
	
	static Value eval(Value input) {
		return input;
	}

	static String print(Value input) {
		return input.toString();
	}
	
	static String rep(String input) {
		try {
			return print(eval(read(input)));
		} catch (MalException ex) {
			return ex.getMessage();
		}
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

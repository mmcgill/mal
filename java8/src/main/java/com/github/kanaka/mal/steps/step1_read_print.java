package com.github.kanaka.mal.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.github.kanaka.mal.MalException;
import com.github.kanaka.mal.Reader;
import com.github.kanaka.mal.value.Value;

public class step1_read_print {
	
	static Value eval(Value input) {
		return input;
	}

	static String print(Value input) {
		return input.toString();
	}
	
	static List<String> rep(String inputLine) {
		List<String> results = new LinkedList<>();
		try {
			Reader reader = new Reader(inputLine);
			for (Value form = reader.readForm(); form != null; form = reader.readForm()) {
				try {
					results.add(print(eval(form)));
				} catch (MalException ex) {
					results.add(ex.getMessage());
				}
			}
		} catch (MalException ex) {
			results.add(ex.getMessage());
		}
		return results;
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
				for (String result : rep(inputLine)) {
					System.out.println(result);
				}
				System.out.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}

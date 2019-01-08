package com.github.kanaka.mal;

import static com.github.kanaka.mal.value.Value.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReaderTest {

	@Test
	public void testTokenizeSingleNumber() {
		assertArrayEquals(new String[] {"123"}, Reader.tokenize("123").toArray());
	}

	@Test
	public void testTokenizeList() {
		assertArrayEquals(
				new String[] {"(", "1", "2", "123", ")"},
				Reader.tokenize("(1 2 123)").toArray());
		assertArrayEquals(
				new String[] {"(", "1", "2", "123", ")"},
				Reader.tokenize("(1, 2, 123)").toArray());
	}
	
	@Test
	public void testReadIntForm() {
		assertEquals(integer(42), new Reader("42").readForm());

	}
	
	@Test
	public void testReadSymbolForm() {
		assertEquals(symbol("forty_two"), new Reader("forty_two").readForm());
	}
	
	@Test
	public void testReadListForm() {
		assertEquals(
				list(integer(1), symbol("two"), integer(3)),
				new Reader("(1 two 3)").readForm());

		assertEquals(list(), new Reader("()").readForm());

		assertEquals(list(list(), list()),
				new Reader("(()())").readForm());
	}
	
	@Test(expected=SyntaxException.class)
	public void testListWithMissingCloseParen() {
		new Reader("(1 2 3").readForm();
	}
}

package com.github.kanaka.mal;

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
		assertEquals(new IntValue(42), new Reader("42").readForm());

	}
	
	@Test
	public void testReadSymbolForm() {
		assertEquals(new SymbolValue("forty_two"), new Reader("forty_two").readForm());
	}
	
	@Test
	public void testReadListForm() {
		assertEquals(
				new ListValue(new IntValue(1), new SymbolValue("two"), new IntValue(3)),
				new Reader("(1 two 3)").readForm());

		assertEquals(new ListValue(), new Reader("()").readForm());

		assertEquals(new ListValue(new ListValue(), new ListValue()),
				new Reader("(()())").readForm());
	}
	
	@Test(expected=SyntaxException.class)
	public void testListWithMissingCloseParen() {
		new Reader("(1 2 3").readForm();
	}
}

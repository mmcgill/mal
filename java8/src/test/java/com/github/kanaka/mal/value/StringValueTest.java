package com.github.kanaka.mal.value;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.kanaka.mal.value.StringValue;

public class StringValueTest {

	@Test
	public void testEscapingAndUnescaping() {
		assertEquals("\n", StringValue.unescape("\n"));
		assertEquals("\n", StringValue.unescape("\\n"));
		assertEquals("\\n", StringValue.escape("\n"));

		assertEquals("\"", StringValue.unescape("\""));
		assertEquals("\"", StringValue.unescape("\\\""));
		assertEquals("\\\"", StringValue.escape("\""));
		
		assertEquals("\\", StringValue.unescape("\\"));
		assertEquals("\\", StringValue.unescape("\\\\"));
		assertEquals("\\\\", StringValue.escape("\\"));

		assertEquals("fo\"o\nbar\\", StringValue.unescape("fo\\\"o\\nbar\\\\"));

		assertEquals("1", StringValue.escape("1"));
	}

}

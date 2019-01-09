package com.github.kanaka.mal.value;

import static org.junit.Assert.*;

import org.junit.Test;

import static com.github.kanaka.mal.value.Value.*;

public class ListValueTest {
	
	@Test
	public void testCons() {
		assertArrayEquals(new Value[] {}, list().toArray());
		assertArrayEquals(new Value[] {integer(1)}, list(integer(1)).toArray());
		assertArrayEquals(new Value[] {integer(1), integer(2)}, list(integer(1), integer(2)).toArray());
	}

	@Test
	public void testEquality() {
		assertEquals(list(integer(1)), list(integer(1)));
		assertNotEquals(list(integer(1)), list(integer(2)));
		assertEquals(list(integer(1)), vector(integer(1)));
		assertNotEquals(list(integer(1)), vector(integer(2)));
	}

}

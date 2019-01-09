package com.github.kanaka.mal;

import static org.junit.Assert.*;

import org.junit.Test;

import static com.github.kanaka.mal.value.Value.*;

public class CoreTest {

	@Test
	public void testConcat() {
		assertEquals(
				list(integer(1), integer(2), integer(3), integer(4), integer(5)),
				Core.concat(list(integer(1), integer(2)), list(integer(3), integer(4)), list(integer(5))));
	}

}

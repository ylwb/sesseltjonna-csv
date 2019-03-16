package com.github.skjolber.stcsv.column.bi;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class LongCsvColumnValueConsumerTest {

	@Test
    public void testLongParsing1() throws Exception {
		String characters = "123456789012345678";
		
		for(int i = 0; i < characters.length() - 1; i++) {
			String str = characters.substring(i);

	        assertEquals(Long.parseLong(str), LongCsvColumnValueConsumer.parseLong(str.toCharArray(), 0, str.length()));
		}
		
    }

	@Test
    public void testLongParsing2() throws Exception {
		String characters = "123456789012345678";
		
		for(int i = 2; i < characters.length() - 1; i++) {
			String str = '-' + characters.substring(i);

	        assertEquals(Long.parseLong(str), LongCsvColumnValueConsumer.parseLong(str.toCharArray(), 0, str.length()));
		}
    }

}
package com.github.skjolber.stcsv.builder;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;

public class StringArrayCsvReaderBuilderTest {

	private String empty = "";
	private String singleLine = "a,b,c\n";
	private String[] emptyColumns = {",,\n", "a,b,\n", "a,,\n"};

	@Test
	public void testEmpty() throws Exception {
		new StringArrayCsvReaderBuilder().build(new StringReader(empty));
	}

	@Test
	public void testSingleLine() throws Exception {
		new StringArrayCsvReaderBuilder().build(new StringReader(singleLine));
	}

	@Test
	public void testEmptyFirstColumns() throws Exception {
		for(String empty : emptyColumns) {
			CsvReader<String[]> build = new StringArrayCsvReaderBuilder().build(new StringReader(empty + singleLine));
			build.next(); // skip first line

			String[] next = build.next();
			assertThat(next[0]).isEqualTo("a");
			assertThat(next[1]).isEqualTo("b");
			assertThat(next[2]).isEqualTo("c");
		}
	}
}
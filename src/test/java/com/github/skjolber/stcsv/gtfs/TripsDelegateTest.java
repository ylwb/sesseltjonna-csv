package com.github.skjolber.stcsv.gtfs;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.column.tri.StringCsvColumnValueTriConsumer;
import com.github.skjolber.stcsv.column.tri.TriConsumer;
import com.github.skjolber.stcsv.CsvMapper;
import com.github.skjolber.stcsv.CsvMapper2;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
/**
 * 
 * https://developers.google.com/transit/gtfs/reference/#tripstxt
 *
 */
public class TripsDelegateTest {

	private File file = new File("src/test/resources/gtfs/trips-plain-5000.txt");

	private CsvMapper2<Trip, Cache> plain;

	public static class Cache {
		Set<String> routes = new HashSet<>();
		
		public void add(String str) {
			routes.add(str);
		}
		
		public Set<String> getRoutes() {
			return routes;
		}
	}
	
	@BeforeEach
	public void init() throws Exception {
		
		plain = CsvMapper2.builder(Trip.class, Cache.class)
				.stringField("route_id")
					.consumer((a, b, c) -> {
						System.out.println("Consume for " + a + " " + b + " " + c);
						b.add(c);
					})
					.setter(Trip::setRouteId)
					.quoted()
					.optional()
				.stringField("service_id")
					.setter(Trip::setServiceId)
					.required()
				.stringField("trip_id")
					.setter(Trip::setTripId)
					.required()
				.stringField("trip_headsign")
					.setter(Trip::setTripHeadsign)
					.quoted()
					.optional()
					/*
				.integerField("direction_id")
					.setter(Trip::setDirectionId)
					.optional()
					*/
				.stringField("shape_id")
					.setter(Trip::setShapeId)
					.optional()
					/*
				.integerField("wheelchair_accessible")
					.setter(Trip::setWheelchairAccessible)
					.optional()
					*/
				.build();
	}

	static TriConsumer<Trip, Cache, String> c = new TriConsumer<Trip, Cache, String>() {

		@Override
		public void accept(Trip s, Cache t, String u) {
			System.out.println("ACCEPT");
		}
	};

	static Cache cache = new Cache();

	@Test
	public  void testStuff() {

		StringCsvColumnValueTriConsumer<Trip, Cache> d = new StringCsvColumnValueTriConsumer<>(c);
		
		System.out.println("BEFORE");
		
		Trip trip = new Trip();

		char[] part = "ABCDEF".toCharArray();
		
		d.consume(trip, cache, part, 0, 6);
		
		System.out.println("AFTER");
	}
	

	@Test
	public void compareToConventionalParserWithoutQuotes() throws Exception {

		Cache cache = new Cache();
		CsvParser referenceParser = referenceParser(file, StandardCharsets.UTF_8);
		CsvReader<Trip> factory = parser(file, StandardCharsets.UTF_8, cache);
		
		int count = 0;
		
		referenceParser.parseNext(); // first row
		do {
			Trip trip = factory.next();
			
			String[] row = referenceParser.parseNext();
			
			Trip referenceTrip;
			if(row != null) {
				referenceTrip = new Trip();
				referenceTrip.setRouteId(row[0]);
				referenceTrip.setTripId(row[1]);
				referenceTrip.setServiceId(row[2]);
				referenceTrip.setTripHeadsign(row[3]);
				if(row[4] != null && row[4].length() > 0) {
					referenceTrip.setDirectionId(Integer.parseInt(row[4]));
				}
				referenceTrip.setShapeId(row[5]);
				if(row[6] != null && row[6].length() > 0) {
					referenceTrip.setWheelchairAccessible(Integer.parseInt(row[6]));
				}
			} else {
				referenceTrip = null;
			}

			if(!Objects.equals(trip, referenceTrip)) {
				System.out.println("Line " + count);
				System.out.println(trip);
				System.out.println(referenceTrip);
			}
			assertThat(trip).isEqualTo(referenceTrip);

			if(trip == null) {
				break;
			}
			
			count++;
		} while(true);

		System.out.println("Parsed " + count + " lines");

	}

	public CsvReader<Trip> parser(File file, Charset charset, Cache cache) throws Exception {
		InputStream input = new FileInputStream(file);
		
		InputStreamReader reader1 = new InputStreamReader(input, charset);
		return plain.create(reader1, cache);
	}

	public static CsvParser referenceParser(File file, Charset charset) throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setSkipEmptyLines(false);
		settings.setColumnReorderingEnabled(false);
		
		//##CODE_START

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);
		
		InputStream input = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(input, charset);
		
		parser.beginParsing(reader);		
		
		return parser;
	}
}

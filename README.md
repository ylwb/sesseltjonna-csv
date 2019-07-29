[![Build Status](https://travis-ci.org/skjolber/sesseltjonna-csv.svg)](https://travis-ci.org/skjolber/sesseltjonna-csv)
[![codecov](https://codecov.io/gh/skjolber/sesseltjonna-csv/branch/master/graph/badge.svg)](https://codecov.io/gh/skjolber/sesseltjonna-csv)

# sesseltjonna-csv: High-performance CSV processing
**sesseltjonna-csv** is a high-performance CSV library with developer-friendly configuration options.

Projects using this library will benefit from:

 * dynamically generated CSV databinding (including parser) per file (at runtime, using [ASM])
 * per-field configuration options
 * builder with support for syntactic sugar and customization options
 * best in class performance according to the [benchmarks]. 

For databinding, a very specific parser is generated per unique CSV file header, which yields __extremely fast processing while allowing for per-field customizations__. 

The library also hosts 'traditional' CSV parsers (statically typed) for those wanting to work directly on String arrays. 

The primary use-case for this library is __large csv files__ with more than 1000 lines where the CSV file format is known and reasonable stable. 

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Obtain
The project is implemented in Java and built using [Maven]. The project is available on the central Maven repository.

Example dependency config:

```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>databinder</artifactId>
    <version>1.0.17</version>
</dependency>
```
or
```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>parser</artifactId>
    <version>1.0.17</version>
</dependency>
```

# Usage - databinding
Use the builder to configure your parser.

```java
CsvMapper<Trip> mapper = CsvMapper.builder(Trip.class)
        .stringField("route_id")
            .setter(Trip::setRouteId)
            .quoted()
            .optional()
        .stringField("service_id")
            .setter(Trip::setServiceId)
            .required()
        .build();
```

where each field must be either `required` or `optional`. Use of the `setter` is optional; a setter with the same name as the field name will be automatically selected in its abscense. 

Then create a `CsvReader` using


```java
Reader reader = ...; // your input

CsvReader<Trip> csvReader = mapper.create(reader);
```

and parse untill `null` using

```java
do {
    Trip trip = csvReader.next();
    if(trip == null) {
        break;
    }

   // your code here    
} while(true);
```

If you're into doing some custom logic before applying values, add your own `consumer`:

```java
CsvMapper<City> mapping = CsvMapper.builder(City.class)
    .longField("Population")
        .consumer((city, n) -> city.setPopulation(n * 1000))
        .optional()
    .build();
```

## Intermediate processor
The library supports an `intermediate processor` for handling complex references. In other words when a column value maps to a child or parent object, it can be resolved at parse or post-processing time. For example by resolving a `Country` when parsing a `City` using an instance of `MyCountryLookup` - first the mapper:

```java
CsvMapper<City> mapping = CsvMapper.builder(City.class, MyCountryLookup.class)
    .longField("Country")
        .consumer((city, lookup, country) -> city.setCountry(lookup.getCountry(country))
        .optional()
    .build();
```

Then supply an instance of of the `intermediate processor` when creating the `CsvRader`:

```java
MyCountryLookup lookup = ...;

CsvReader<City> csvReader = mapper.create(reader, lookup);
```

Using this feature can be essential when parsing multiple CSV files in parallel, or even fragments of the same file in parallel, with entities referencing each other, storing the values in intermediate processors and resolving references as a post-processing step. 

# Usage - traditional parser
Create a `CsvReader<String[]>` using

```java
Reader input = ...; // your input
CsvReader<String[]> csvReader = StringArrayCsvReader.builder().build(input);
        
String[] next;
do {
    next = csvReader.next();
    if(next == null) {
        break;
    }
    
   // your code here    
} while(true);
```
Note that the String-array itself is reused between lines. Note that the column indexes can be rearranged  by using the builder `withColumnMapping(..)` methods, which should be useful when doing your own (efficient) hand-coded databinding. 

# Performance
The dynamically generated instances are extremely fast (i.e. as good as a parser tailored very specifically to the file being parsed), but note that the assumption is that the number of different CSV files for a given application or format is limited, so that parsing effectively is performed by a JIT-compiled class and not by a newly generated class for each file.

To maximize performance (like response time) it is always necessary to pre-warm the JVM regardless of the underlying implementation.

JMH [benchmark results](https://github.com/skjolber/csv-benchmark#results). 

If the parser runs alone on a multicore system, the [ParallelReader](https://github.com/arnaudroger/SimpleFlatMapper/blob/master/sfm-util/src/main/java/org/simpleflatmapper/util/ParallelReader.java) from the [SimpleFlatMapper](https://simpleflatmapper.org/) might further improve performance by approximately 50%.

# Compatibility
The following rules / restrictions apply, mostly for keeping in sync with [RFC-4180]:

 * Quoted fields must be declared as quoted (in the builder) and can contain all characters. 
 * The first character of a quoted field must be a quote. If not, the value is treated as a plain field. 
 * Plain fields must not contain the separator or newline, otherwise can contain all characters.
 * All fields are either required or optional (no empty string is ever propagated to the target). Missing values result in CsvException.
 * All lines must contain the same number of columns
 * Corrupt files can result in CsvException
 * Newline and carriage return + newline line endings are supported (and auto-detected).
 * Columns which have no mapping are skipped (ignored).

Also note that

 * The default mode assumes the first line is the header. For fixed formats, a default parser can be created.
 * Maximum line length is per default 64K. 64K should be enough to hold a lot of lines, if not try increasing the buffer size to improve performance.

# Contact
If you have any questions, comments or feature requests, please open an issue.

Contributions are welcome, especially those with unit tests ;)

## License
[Apache 2.0]

# History

 - 1.0.17: Improve parse of quoted columns
 - 1.0.16: Improve ByteBuddy usage for setter detection.
 - 1.0.15: Improved test coverage, fix quoted first line for.
 - 1.0.14: Fix issue with skipping columns

[Apache 2.0]:           http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:        https://github.com/skjolber/sesseltjonna-csv/issues
[Maven]:                http://maven.apache.org/
[benchmarks]:           https://github.com/skjolber/csv-benchmark
[hytta.jpg]:            http://skjolber.github.io/img/hytta.jpg
[ASM]:                  https://asm.ow2.io/
[RFC-4180]:             https://tools.ietf.org/html/rfc4180

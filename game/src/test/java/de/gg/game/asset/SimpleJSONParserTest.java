package de.gg.game.asset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.google.gson.reflect.TypeToken;


public class SimpleJSONParserTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testClassParsing() {
		JsonTestObject o = new JsonTestObject();
		o.date = new Date(2018, 11, 30);
		o.i = 35;
		o.string = "xyz";
		o.string2 = "abc";

		SimpleJSONParser parser = new SimpleJSONParser();

		String jsonText = parser.parseToJson(o);

		assertEquals(
				"{\"string\":\"xyz\",\"date\":\"3918-12-30 00:00:00\",\"i\":35}",
				jsonText);
		assertEquals(o, parser.parseFromJson(jsonText, JsonTestObject.class));
	}

	@Test
	public void testTypeParsing() {
		ArrayList<String> l = new ArrayList<>();
		l.add("test");

		SimpleJSONParser parser = new SimpleJSONParser();

		String jsonText = parser.parseToJson(l);

		assertEquals("[\"test\"]", jsonText);
		assertEquals(l, parser.parseFromJson(jsonText,
				new TypeToken<ArrayList<String>>() {
				}.getType()));
	}

}

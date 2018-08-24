package de.gg.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.gg.util.json.ExcludeAnnotationExclusionStrategy.ExcludeFromJSON;

public class SimpleJSONParserTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		TestObject o = new TestObject();
		o.date = new Date(2018, 11, 30);
		o.i = 35;
		o.string = "xyz";
		o.string2 = "abc";

		SimpleJSONParser parser = new SimpleJSONParser();

		String jsonText = parser.parseToJson(o);

		assertEquals(
				"{\"string\":\"xyz\",\"date\":\"3918-12-30 00:00:00\",\"i\":35}",
				jsonText);
		assertEquals(o, parser.parseFromJson(jsonText, TestObject.class));
	}

	static class TestObject {

		public String string;
		public Date date;
		public int i;
		public static final int TEST = 123;
		@ExcludeFromJSON
		public String string2;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestObject other = (TestObject) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (i != other.i)
				return false;
			if (string == null) {
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}

	}

}

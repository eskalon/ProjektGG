package de.gg.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonIndexTest {

	@Test
	public void test() {
		List<TestObject> list = new ArrayList<>();

		TestObject o1 = new TestObject();
		o1.i = -1;
		list.add(o1);

		TestObject o2 = new TestObject();
		o2.i = -2;
		list.add(o2);

		TestObject o3 = new TestObject();
		o3.i = -3;
		list.add(o3);

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(TestObject.class,
						new JSONIndexSerializer<>(list))
				.registerTypeAdapter(TestObject.class,
						new JSONIndexDeserializer<>(list))
				.create();

		TestObjectWrapper w = new TestObjectWrapper();
		w.object = o3;

		String jsonText = gson.toJson(w);

		// Serialization
		assertEquals("{\"object\":2}", jsonText);
		// Deserialization
		assertEquals(w, gson.fromJson(jsonText, TestObjectWrapper.class));
	}

	static class TestObjectWrapper {

		public TestObject object;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestObjectWrapper other = (TestObjectWrapper) obj;
			if (object == null) {
				if (other.object != null)
					return false;
			} else if (!object.equals(other.object))
				return false;
			return true;
		}

	}

	static class TestObject {

		public int i;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestObject other = (TestObject) obj;
			if (i != other.i)
				return false;
			return true;
		}

	}

}

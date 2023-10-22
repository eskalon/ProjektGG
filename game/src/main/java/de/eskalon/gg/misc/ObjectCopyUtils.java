package de.eskalon.gg.misc;

import com.esotericsoftware.kryo.Kryo;

public class ObjectCopyUtils {

	private static ObjectCopyUtils instance;
	private Kryo kryo;

	private ObjectCopyUtils() {
		this.kryo = new Kryo();
		this.kryo.setRegistrationRequired(false);
	}

	public static ObjectCopyUtils instance() {
		if (instance == null)
			instance = new ObjectCopyUtils();
		return instance;
	}

	public <T> T copy(T obj) {
		return kryo.copy(obj);
	}

}

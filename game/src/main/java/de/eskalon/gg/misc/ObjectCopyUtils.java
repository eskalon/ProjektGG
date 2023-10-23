package de.eskalon.gg.misc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializerFactory.FieldSerializerFactory;
import com.esotericsoftware.kryo.serializers.FieldSerializer.FieldSerializerConfig;

public class ObjectCopyUtils {

	private static ObjectCopyUtils instance;
	private Kryo kryo;

	private ObjectCopyUtils() {
		this.kryo = new Kryo();
		this.kryo.setRegistrationRequired(false);
		FieldSerializerConfig cfg = new FieldSerializerConfig();
		cfg.setCopyTransient(false);
		this.kryo.setDefaultSerializer(new FieldSerializerFactory(cfg));
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

package de.gg.engine.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import de.gg.LibgdxUnitTest;

public class LangTest extends LibgdxUnitTest {

	@Test
	public void test() {
		// Mock bundle
		I18NBundle bundle = I18NBundle.createBundle(Gdx.files.internal("lang"));

		Lang.setBundle(bundle);

		// Test methods
		assertEquals("test_abc", Lang.get("unloc.test"));
		assertEquals("test2_abc", Lang.get("unloc.test2"));
		assertEquals("test_abc", Lang.get(new A()));
		assertEquals("TEST-123", Lang.get(new B()));
	}

	public class A implements Localizable {
		@Override
		public String getUnlocalizedName() {
			return "unloc.test";
		}
	}

	public class B implements Localized {
		@Override
		public String getLocalizedName() {
			return "TEST-123";
		}
	}

}

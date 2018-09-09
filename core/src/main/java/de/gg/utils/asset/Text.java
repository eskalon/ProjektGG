package de.gg.utils.asset;

import java.nio.charset.Charset;

import com.badlogic.gdx.files.FileHandle;

/**
 * A simple asset type for text files. Uses the UTF-8-encoding if supported by
 * the local JVM.
 *
 * @see TextLoader The respective asset loader.
 * @see <a href=
 *      "https://gamedev.stackexchange.com/a/101331">https://gamedev.stackexchange.com/a/101331</a>
 */
public class Text {

	private static final Charset CHARSET = Charset.isSupported("UTF-8")
			? Charset.forName("UTF-8")
			: Charset.defaultCharset();
	private String string;

	public Text(String string) {
		this.string = new String(string.getBytes(), CHARSET);
	}

	public Text(FileHandle file) {
		this(new String(file.readBytes(), CHARSET));
	}

	public String getString() {
		return this.string;
	}

}
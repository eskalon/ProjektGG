package de.gg.util.asset;

import java.nio.charset.Charset;

import com.badlogic.gdx.files.FileHandle;

/**
 * A simple asset type for text files.
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

	public Text() {
		this.string = new String("".getBytes());
	}

	public Text(byte[] data) {
		this.string = new String(data, CHARSET);
	}

	public Text(String string) {
		this.string = new String(string.getBytes(), CHARSET);
	}

	public Text(FileHandle file) {
		this.string = new String(file.readBytes(), CHARSET);
	}

	public Text(Text text) {
		this.string = new String(text.getString().getBytes(), CHARSET);
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	public void clear() {
		this.string = new String("".getBytes());
	}

}
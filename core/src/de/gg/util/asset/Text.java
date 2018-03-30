package de.gg.util.asset;

import com.badlogic.gdx.files.FileHandle;

/**
 * A simple asset type for text files.
 *
 * @see TextLoader The respective asset loader.
 * @see <a href=
 *      "https://gamedev.stackexchange.com/a/101331">https://gamedev.stackexchange.com/a/101331</a>
 */
public class Text {

    private String string;

    public Text() {
        this.string = new String("".getBytes());
    }

    public Text(byte[] data) {
        this.string = new String(data);
    }

    public Text(String string) {
        this.string = new String(string.getBytes());
    }

    public Text(FileHandle file) {
        this.string = new String(file.readBytes());
    }

    public Text(Text text) {
        this.string = new String(text.getString().getBytes());
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
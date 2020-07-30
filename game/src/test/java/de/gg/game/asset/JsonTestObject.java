package de.gg.game.asset;

import java.util.Date;

import de.gg.game.asset.ExcludeAnnotationExclusionStrategy.ExcludeFromJSON;

public class JsonTestObject {

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
		JsonTestObject other = (JsonTestObject) obj;
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

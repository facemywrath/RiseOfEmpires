package facemywrath.riseofempires.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlphaNumStr {


	public static boolean isAlphaNumeric(String str)
	{ 
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

}

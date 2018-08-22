package de.gg.util;

import java.io.IOException;
import java.util.Scanner;

public class MachineIdentificationUtils {

	private static final String HOSTNAME_COMMAND = "hostname";
	private static final String UNKNOWN_HOST = "Unknown Machine";

	private MachineIdentificationUtils() {
	}

	/**
	 * @return the (host)name of the machine.
	 */
	public static String getHostname() {
		try {
			return getHostnameCommandResult().trim().toLowerCase()
					.replace(".home", "");
		} catch (IOException e) {
			return UNKNOWN_HOST;
		}
	}

	private static String getHostnameCommandResult() throws IOException {
		try (Scanner s = new Scanner(
				Runtime.getRuntime().exec(HOSTNAME_COMMAND).getInputStream())
						.useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}

}
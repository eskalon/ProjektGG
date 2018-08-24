package de.gg.util;

import java.io.IOException;
import java.util.Scanner;

public class MachineIdentificationUtils {

	private static final String HOSTNAME_COMMAND = "hostname";
	private static final String UNKNOWN_HOST_PREFIX = "_u";

	private MachineIdentificationUtils() {
		// not used
	}

	/**
	 * @return the (host)name of the machine.
	 */
	public static String getHostname() {
		try {
			return getHostnameCommandResult().trim().toLowerCase()
					.replace(".home", "");
		} catch (IOException e) {
			return UNKNOWN_HOST_PREFIX
					+ String.valueOf(System.currentTimeMillis());
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

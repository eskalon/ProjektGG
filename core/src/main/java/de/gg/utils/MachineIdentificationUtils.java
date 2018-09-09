package de.gg.utils;

import java.io.IOException;
import java.util.Scanner;

public class MachineIdentificationUtils {

	private static final String HOSTNAME_COMMAND = "hostname";
	private static final String UNKNOWN_HOST_PREFIX = "_u";

	private MachineIdentificationUtils() {
		// not used
	}

	/**
	 * @return the (host)name of the machine. If the name cannot be found
	 *         <code>{@value #UNKNOWN_HOST_PREFIX}</code> is returned followed
	 *         by {@link System#currentTimeMillis()}.
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
		try (@SuppressWarnings("resource")
		Scanner s = new Scanner(
				Runtime.getRuntime().exec(HOSTNAME_COMMAND).getInputStream())
						.useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}

}

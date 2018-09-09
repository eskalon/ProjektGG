package de.gg.network;

import java.net.DatagramPacket;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientDiscoveryHandler;
import com.google.common.base.Preconditions;

import de.gg.network.messages.DiscoveryResponsePacket;

/**
 * This class takes care of discovering available game servers.
 */
public class ServerDiscoveryHandler {

	private final int TIMEOUT;

	public ServerDiscoveryHandler(int timeout) {
		this.TIMEOUT = timeout;
	}

	/**
	 * @param port
	 *            The port to listen on.
	 * @param listener
	 *            The listener that is informed when a server is found.
	 */
	public void discoverHosts(int port, HostDiscoveryListener listener) {
		Preconditions.checkArgument(port > 0, "the port must be valid");
		Preconditions.checkNotNull(listener, "the listener cannot be null");

		Client c = new Client();
		c.getKryo().register(DiscoveryResponsePacket.class);
		c.setDiscoveryHandler(new ClientDiscoveryHandler() {

			@Override
			public DatagramPacket onRequestNewDatagramPacket() {
				byte[] buffer = new byte[1024];
				return new DatagramPacket(buffer, buffer.length);
			}

			@Override
			public void onDiscoveredHost(DatagramPacket datagramPacket) {
				Object obj = c.getKryo().readClassAndObject(
						new Input(datagramPacket.getData()));
				if (obj instanceof DiscoveryResponsePacket)
					listener.onHostDiscovered(
							datagramPacket.getAddress().getHostAddress(),
							(DiscoveryResponsePacket) obj);
			}

			@Override
			public void onFinally() {
			}
		});
		c.discoverHosts(port, TIMEOUT);
		c.close();
	}

	public interface HostDiscoveryListener {
		/**
		 * Is called when the {@linkplain ServerDiscoveryHandler} finds a host.
		 *
		 * @param address
		 * @param datagramPacket
		 * @see DiscoveryResponsePacket
		 */
		public void onHostDiscovered(String address,
				DiscoveryResponsePacket datagramPacket);
	}
}

package de.eskalon.commons.net;

import java.net.DatagramPacket;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientDiscoveryHandler;

import de.damios.guacamole.Preconditions;
import de.eskalon.commons.net.packets.DiscoveryResponsePacket;

/**
 * This class takes care of discovering available game servers.
 */
public class ServerDiscoveryHandler<P extends DiscoveryResponsePacket> {

	private final int timeout;
	private final Class<P> packetClass;

	public ServerDiscoveryHandler(Class<P> packetClass, int timeout) {
		this.timeout = timeout;
		this.packetClass = packetClass;
	}

	/**
	 * @param port
	 *            The port to listen on.
	 * @param listener
	 *            The listener that is informed when a server is found.
	 */
	public void discoverHosts(int port, HostDiscoveryListener<P> listener) {
		Preconditions.checkArgument(port > 0, "the port must be valid");
		Preconditions.checkNotNull(listener, "the listener cannot be null");

		Client c = new Client();
		c.getKryo().register(packetClass);
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
				if (packetClass.isInstance(obj))
					listener.onHostDiscovered(
							datagramPacket.getAddress().getHostAddress(),
							packetClass.cast(obj));
			}

			@Override
			public void onFinally() {
			}
		});
		c.discoverHosts(port, timeout);
		c.close();
	}

	public interface HostDiscoveryListener<P extends DiscoveryResponsePacket> {
		/**
		 * Is called when the {@linkplain ServerDiscoveryHandler} finds a host.
		 *
		 * @param address
		 * @param datagramPacket
		 * @see DiscoveryResponsePacket
		 */
		public void onHostDiscovered(String address, P datagramPacket);
	}
}

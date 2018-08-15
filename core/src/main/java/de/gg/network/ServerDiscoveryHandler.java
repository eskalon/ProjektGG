package de.gg.network;

import java.net.DatagramPacket;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientDiscoveryHandler;

import de.gg.network.message.DiscoveryResponsePacket;

/**
 * This class takes care of discovering available game servers.
 */
public class ServerDiscoveryHandler {

	/**
	 * @param port
	 *            The port to listen on.
	 * @param listener
	 *            The listener that is informed when a server is found.
	 */
	public void discoverHosts(int port, HostDiscoveryListener listener) {
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
				DiscoveryResponsePacket packet = (DiscoveryResponsePacket) c
						.getKryo().readClassAndObject(
								new Input(datagramPacket.getData()));
				listener.onHostDiscovered(
						datagramPacket.getAddress().getHostAddress(), packet);
			}

			@Override
			public void onFinally() {
			}
		});
		c.discoverHosts(port, 4500);
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

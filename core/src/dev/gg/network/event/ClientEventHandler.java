package dev.gg.network.event;

import dev.gg.network.Player;
import dev.gg.screen.LobbyScreen;

/**
 * This class is used to distribute multiplayer events to an appropriate
 * handler, i.e. the {@link LobbyScreen}.
 */
public interface ClientEventHandler {

	/**
	 * Called when a player disconnects.
	 * 
	 * @param player
	 *            The disconnected player.
	 */
	public default void onPlayerDisconnect(Player player) {
	}

	/**
	 * Called when a player connects. Basically the same as
	 * {@link #onPlayerChanged(short, Player)}
	 * 
	 * @param id
	 *            The new player's ID.
	 * @param player
	 *            The new player.
	 */
	public default void onPlayerConnect(Player player) {
	}

	/**
	 * Called upon a new chat message. i>Not</i> called for one clients own
	 * messages.
	 * 
	 * @param senderId
	 *            The sending player's ID.
	 * @param message
	 *            The message.
	 */
	public default void onNewChatMessage(short senderId, String message) {
	}

	/**
	 * Called when one of the clients changes (new icon, getting ready, etc.).
	 * <i>Not</i> called for one clients own changes.
	 */
	public default void onPlayerChanged() {
	}

}
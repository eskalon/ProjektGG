package dev.gg.network;

import java.util.HashMap;

import dev.gg.core.Player;
import dev.gg.network.message.ClientTurnMessage;

public class ServerPlayer {

	private HashMap<Integer, ClientTurnMessage> messages;
	private Player player;

	public ServerPlayer(Player player) {
		this.player = player;
		this.messages = new HashMap<>();
	}

	public boolean isReadyForTurn(int turn) {
		return messages.containsKey(turn);
	}

	public HashMap<Integer, ClientTurnMessage> getMessages() {
		return messages;
	}

	public ClientTurnMessage getMessageForTurn(int turn) {
		return messages.get(turn);
	}

	public void addMessage(int turn, ClientTurnMessage msg) {
		messages.put(turn, msg);
	}

	public Player getPlayer() {
		return player;
	}

}

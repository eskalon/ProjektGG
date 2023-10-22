package de.eskalon.commons.net.packets.data;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.IntMap;

public class LobbyData<G, S, P> {

	private G sessionSetup;
	private @Nullable S gameState;
	private IntMap<P> players;

	public LobbyData() {
		// default public constructor
	}

	public LobbyData(G sessionSetup, @Nullable S gameState) {
		this.sessionSetup = sessionSetup;
		this.gameState = gameState;
		this.players = new IntMap<>();
	}

	public void setSessionSetup(G sessionSetup) {
		this.sessionSetup = sessionSetup;
	}

	public void setGameState(@Nullable S gameState) {
		this.gameState = gameState;
	}

	public G getSessionSetup() {
		return sessionSetup;
	}

	public @Nullable S getGameState() {
		return gameState;
	}

	public IntMap<P> getPlayers() {
		return players;
	}

}

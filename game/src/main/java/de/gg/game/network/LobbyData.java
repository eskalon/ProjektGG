package de.gg.game.network;

import javax.annotation.Nullable;

import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;

public class LobbyData {

	private GameSessionSetup sessionSetup;

	private @Nullable SavedGame savedGame;

	public LobbyData(GameSessionSetup sessionSetup,
			@Nullable SavedGame savedGame) {
		this.sessionSetup = sessionSetup;
		this.savedGame = savedGame;
	}

	public GameSessionSetup getSessionSetup() {
		return sessionSetup;
	}

	public SavedGame getSavedGame() {
		return savedGame;
	}

}

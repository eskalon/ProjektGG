package dev.gg.screens;

import java.io.IOException;

import com.esotericsoftware.kryonet.network.impl.Server;

import dev.network.MultiplayerGame;

public class LobbyCreationScreen extends BaseUIScreen {

	@Override
	protected void initUI() {
		game.setCurrentGame(new MultiplayerGame());
		
		try {
			game.getCurrentGame().setUpServer(54555);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

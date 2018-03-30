package dev.gg.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import dev.gg.core.LobbyPlayer;
import dev.gg.core.LobbyPlayer.PlayerIcon;
import dev.gg.data.GameSessionSetup.GameDifficulty;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.setting.GameSettings;

/**
 * This class takes care of registering all classes needed by the multiplayer
 * endpoints.
 * 
 * @see Kryo#register(Class)
 */
public class NetworkRegisterer {

	private NetworkRegisterer() {

	}

	/**
	 * Registers all needed classes to the given kryo serialization manager.
	 * 
	 * @param kryo
	 *            The kryo serialization manager.
	 */
	public static void registerClasses(Kryo kryo) {
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);

		kryo.register(LobbyPlayer.class);
		kryo.register(PlayerIcon.class);

		kryo.register(GameSettings.class);
		kryo.register(GameDifficulty.class);

		kryo.register(ChatMessageSentMessage.class);
		kryo.register(GameSetupMessage.class);
		kryo.register(PlayerChangedMessage.class);
		kryo.register(PlayerJoinedMessage.class);
		kryo.register(PlayerLeftMessage.class);
	}

}

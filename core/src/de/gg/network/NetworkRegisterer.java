package de.gg.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import de.gg.game.AuthoritativeResultListener;
import de.gg.game.SlaveActionListener;
import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.RoundEndData;
import de.gg.game.type.PlayerIcon;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;

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

		kryo.register(GameDifficulty.class);
		kryo.register(GameSessionSetup.class);

		kryo.register(ChatMessageSentMessage.class);
		kryo.register(GameSetupMessage.class);
		kryo.register(PlayerChangedMessage.class);
		kryo.register(PlayerJoinedMessage.class);
		kryo.register(PlayerLeftMessage.class);

		kryo.register(AuthoritativeResultListener.class);
		kryo.register(SlaveActionListener.class);

		kryo.register(RoundEndData.class);
	}

}

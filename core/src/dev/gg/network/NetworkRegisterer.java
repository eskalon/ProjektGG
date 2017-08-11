package dev.gg.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import dev.gg.command.PlayerCommand;
import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.core.Player;
import dev.gg.core.Player.PlayerIcon;
import dev.gg.data.GameSettings;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.ClientTurnMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.network.message.SeverTurnMessage;

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

		kryo.register(Player.class);
		kryo.register(PlayerIcon.class);

		kryo.register(GameSettings.class);
		kryo.register(GameDifficulty.class);

		kryo.register(PlayerCommand.class);

		kryo.register(ChatMessageSentMessage.class);
		kryo.register(ClientTurnMessage.class);
		kryo.register(GameSetupMessage.class);
		kryo.register(PlayerChangedMessage.class);
		kryo.register(PlayerJoinedMessage.class);
		kryo.register(PlayerLeftMessage.class);
		kryo.register(SeverTurnMessage.class);
	}

}

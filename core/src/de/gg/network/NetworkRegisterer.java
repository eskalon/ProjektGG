package de.gg.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import de.gg.game.data.GameDifficulty;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.data.GameSpeed;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.type.PlayerIcon;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.Religion;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;
import de.gg.network.message.ServerFullMessage;
import de.gg.network.message.ServerRejectionMessage;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.network.rmi.SlaveActionListener;

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
		// Basic classes
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);

		// Lobby (Player) Stuff
		kryo.register(LobbyPlayer.class);
		kryo.register(PlayerIcon.class);
		kryo.register(Religion.class);
		kryo.register(ProfessionType.class);

		// Map Stuff
		kryo.register(GameDifficulty.class);
		kryo.register(GameSessionSetup.class);

		// Messages
		kryo.register(ChatMessageSentMessage.class);
		kryo.register(GameSetupMessage.class);
		kryo.register(PlayerChangedMessage.class);
		kryo.register(PlayerJoinedMessage.class);
		kryo.register(PlayerLeftMessage.class);
		kryo.register(ServerRejectionMessage.class);
		kryo.register(ServerFullMessage.class);

		// Listeners
		kryo.register(AuthoritativeResultListener.class);
		kryo.register(SlaveActionListener.class);

		// RMI
		kryo.register(GameSpeed.class);
		kryo.register(PositionType.class);
		kryo.register(VoteResults.class);
	}

}

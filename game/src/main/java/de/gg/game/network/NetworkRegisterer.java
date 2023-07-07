package de.gg.game.network;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

import de.gg.engine.network.message.LobbyJoinRequestMessage;
import de.gg.engine.network.message.LobbyJoinedMessage;
import de.gg.engine.network.message.ConnectionEstablishedMessage;
import de.gg.engine.network.message.ConnectionRejectedMessage;
import de.gg.game.model.types.GameDifficulty;
import de.gg.game.model.types.GameMap;
import de.gg.game.model.types.GameSpeed;
import de.gg.game.model.types.PlayerIcon;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.types.ProfessionType;
import de.gg.game.model.types.Religion;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.network.rmi.SlaveActionListener;
import de.gg.game.session.GameSessionSetup;

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
	 * Registers all needed classes to the given Kryo serialization manager.
	 *
	 * @param kryo
	 *            The Kryo serialization manager.
	 */
	public static void registerClasses(Kryo kryo) {
		// Basic classes
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);

		// Lobby (Player) Stuff
		kryo.register(PlayerData.class);
		kryo.register(PlayerIcon.class);
		kryo.register(Religion.class);
		kryo.register(ProfessionType.class);

		// Messages
		kryo.register(ConnectionEstablishedMessage.class);
		kryo.register(ConnectionRejectedMessage.class);
		kryo.register(LobbyJoinRequestMessage.class);
		kryo.register(LobbyJoinedMessage.class);

		// Map Stuff
		kryo.register(GameDifficulty.class);
		kryo.register(GameMap.class);
		kryo.register(GameSessionSetup.class);

		// Listeners
		kryo.register(AuthoritativeResultListener.class);
		kryo.register(SlaveActionListener.class);

		// RMI
		kryo.register(GameSpeed.class);
		kryo.register(PositionType.class);
		kryo.register(BallotResults.class);
	}

}

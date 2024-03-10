package de.eskalon.commons.net.packets.data;

import org.jspecify.annotations.Nullable;

import com.badlogic.gdx.utils.IntMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class LobbyData<G, S, P> {

	private @Getter @Setter G sessionSetup;
	private @Getter @Setter @Nullable S gameState;
	private @Getter IntMap<P> players;

	public LobbyData(G sessionSetup, @Nullable S gameState) {
		this.sessionSetup = sessionSetup;
		this.gameState = gameState;
		this.players = new IntMap<>();
	}

}

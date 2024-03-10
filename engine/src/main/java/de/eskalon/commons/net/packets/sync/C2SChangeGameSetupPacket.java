package de.eskalon.commons.net.packets.sync;

import org.jspecify.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This message is sent by the hosting client to indicate a change in the game
 * setup, e.g. a map change.
 * 
 * @param <G>
 * @param <S>
 */
@AllArgsConstructor
@NoArgsConstructor
public final class C2SChangeGameSetupPacket<G, S> {

	private @Getter G sessionSetup;
	private @Getter @Nullable S gameState;

}

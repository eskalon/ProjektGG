package de.eskalon.commons.net.packets.lockstep;

import java.util.List;

import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class S2CActionsDistributionPacket {

	private @Getter int turn;
	private @Getter List<PlayerActionsWrapper> actions;

}

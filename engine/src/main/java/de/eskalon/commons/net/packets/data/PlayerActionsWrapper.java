package de.eskalon.commons.net.packets.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerActionsWrapper {

	private @Getter short playerId;
	private @Getter List<IPlayerAction> actions;

}
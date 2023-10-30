package de.eskalon.gg.simulation.actions;

import de.eskalon.commons.net.packets.data.IPlayerAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class GameSpeedChangeAction implements IPlayerAction {

	private @Getter boolean speedUp;

}

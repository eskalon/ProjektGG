package de.eskalon.gg.net.packets;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class S2CVoteFinishedPacket {

	private @Getter HashMap<Short, Integer> individualVotes;

}

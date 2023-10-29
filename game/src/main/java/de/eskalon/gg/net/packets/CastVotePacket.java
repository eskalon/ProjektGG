package de.eskalon.gg.net.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class CastVotePacket {

	private @Getter int option;

}

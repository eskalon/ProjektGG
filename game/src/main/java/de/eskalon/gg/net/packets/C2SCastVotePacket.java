package de.eskalon.gg.net.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class C2SCastVotePacket {

	private @Getter int option;

}

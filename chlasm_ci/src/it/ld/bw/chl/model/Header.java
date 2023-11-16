/* Copyright (c) 2023 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.bw.chl.model;

import java.io.IOException;

import it.ld.bw.chl.exceptions.InvalidChlException;
import it.ld.bw.chl.exceptions.UnknownVersionException;
import it.ld.bw.chl.exceptions.UnsupportedVersionException;
import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class Header extends Section {
	public static final int BW1 = 7;
	public static final int BWCI = 8;
	public static final int BW2 = 12;
	
	private String magic = "LHVM";
	private int version;
	
	public String getMagic() {
		return magic;
	}
	
	public void setMagic(String magic) throws InvalidChlException {
		if (!("LHVM".equals(magic))) throw new InvalidChlException("Invalid CHL file (wrong magic string)");
		this.magic = magic;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) throws UnknownVersionException, UnsupportedVersionException {
		if (version == BW1) {
			throw new UnsupportedVersionException("Black & White 1", version);
		} else if (version == BWCI) {
			this.version = version;
		} else if (version == BW2) {
			throw new UnsupportedVersionException("Black & White 2", version);
		} else {
			throw new UnknownVersionException(version);
		}
	}
	
	@Override
	public int getLength() {
		return 8;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws IOException, InvalidChlException, UnknownVersionException {
		setMagic(new String(str.readNBytes(4), ASCII));
		setVersion(str.readInt());
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws IOException, InvalidChlException, UnknownVersionException {
		str.write(magic.getBytes(ASCII));
		str.writeInt(version);
	}
	
	@Override
	public String toString() {
		return magic + " version " + version;
	}
}

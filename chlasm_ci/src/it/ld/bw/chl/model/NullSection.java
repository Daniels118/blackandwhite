package it.ld.bw.chl.model;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class NullSection extends Section {
	private final int size;
	
	public NullSection(int size) {
		this.size = size;
	}
	
	@Override
	public int getLength() {
		return size;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		for (int i = 0; i < size; i+=4) {
			int v = str.readInt();
			if (v != 0) {
				throw new Exception("Unexpected value in NullSection at offset "+i);
			}
		}
	}
	
	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.write(new byte[size]);
	}
	
	@Override
	public String toString() {
		return "Null["+size+"]";
	}
}

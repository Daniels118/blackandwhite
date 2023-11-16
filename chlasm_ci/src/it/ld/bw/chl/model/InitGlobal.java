package it.ld.bw.chl.model;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public class InitGlobal extends Struct {
	private DataType type;
	private float floatVal;
	private String name;
	
	public InitGlobal() {}
	
	public InitGlobal(DataType type, String name, Object val) {
		this.type = type;
		switch (type) {
			case FLOAT:
				floatVal = (Float)val;
				break;
			default:
				throw new RuntimeException("Unsupported type "+type+" in init global");
		}
		this.name = name;
	}
	
	public InitGlobal(String name, float val) {
		this.type = DataType.FLOAT;
		this.floatVal = val;
		this.name = name;
	}
	
	public DataType getType() {
		return type;
	}
	
	public void setType(DataType type) {
		this.type = type;
	}
	
	public float getFloat() {
		return floatVal;
	}
	
	public void setFloat(float v) {
		floatVal = v;
	}
	
	public String getAsString() {
		switch (type) {
			case FLOAT:
				return String.valueOf(floatVal);
			default:
				throw new RuntimeException("Unsupported type "+type+" in init global");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getLength() {
		return 8 + name.length() + 1;
	}

	@Override
	public void read(EndianDataInputStream str) throws Exception {
		int t = str.readInt();
		if (t < 0 || t >= DataType.values().length) {
			throw new Exception("Invalid datatype "+t+" in init global");
		}
		type = DataType.values()[t];
		switch (type) {
			case FLOAT:
				floatVal = str.readFloat();
				break;
			default:
				throw new Exception("Unsupported type "+type+" in init global");
		}
		name = readZString(str);
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		str.writeInt(type.ordinal());
		switch (type) {
			case FLOAT:
				str.writeFloat(floatVal);
				break;
			default:
				throw new Exception("Unsupported type "+type+" in init global");
		}
		writeZString(str, name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InitGlobal)) return false;
		InitGlobal other = (InitGlobal) obj;
		if (this.type != other.type) return false;
		if (this.floatVal != other.floatVal) return false;
		if (!this.name.equals(other.name)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		String v = "";
		switch (type) {
			case FLOAT:
				v = String.valueOf(floatVal);
				break;
			default:
				v = "Unsupported type "+type;
		}
		return type.keyword + " " + name + " = " + v;
	}
}

package it.ld.bw.chl.lang.java;

public class LHConst {
	public final String typename;
	public final String name;
	private final Object value;
	
	public LHConst(String typename, String name, String strval) {
		this.typename = typename;
		this.name = name;
		if ("int".equals(typename)) {
			this.value = Integer.parseInt(strval);
		} else if ("float".equals(typename)) {
			this.value = Float.parseFloat(strval);
		} else if ("boolean".equals(typename)) {
			this.value = Boolean.parseBoolean(strval);
		} else {
			throw new RuntimeException("Unsupported field type: " + typename);
		}
	}
	
	public LHConst(String typename, String name, Object val) {
		this.typename = typename;
		this.name = name;
		this.value = val;
		if ("int".equals(typename)) {
			
		} else if ("float".equals(typename)) {
			
		} else if ("boolean".equals(typename)) {
			
		} else {
			throw new RuntimeException("Unsupported field type: " + typename);
		}
	}
	
	public int intval() {
		return (int)value;
	}
	
	public float floatval() {
		return (float)value;
	}
	
	public boolean boolval() {
		return (boolean)value;
	}
}

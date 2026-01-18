package chl.lang;

public enum ScaffoldInfo implements LHEnum {
	NORMAL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

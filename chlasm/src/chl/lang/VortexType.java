package chl.lang;

public enum VortexType implements LHEnum {
	IN,
	OUT,
	VOLCANO;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

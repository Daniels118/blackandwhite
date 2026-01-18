package chl.lang;

public enum FireballType implements LHEnum {
	NORMAL,
	PU1,
	PU2;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

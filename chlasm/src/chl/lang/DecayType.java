package chl.lang;

public enum DecayType implements LHEnum {
	FORWARD,
	REVERSE,
	IMMEDIATE,
	RANDOM;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

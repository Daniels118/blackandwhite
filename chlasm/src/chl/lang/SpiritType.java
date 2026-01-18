package chl.lang;

public enum SpiritType implements LHEnum {
	NONE,
	GOOD,
	EVIL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

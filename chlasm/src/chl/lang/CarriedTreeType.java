package chl.lang;

public enum CarriedTreeType implements LHEnum {
	NONE,
	EVERGREEN,
	FRUIT,
	HARDWOOD;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

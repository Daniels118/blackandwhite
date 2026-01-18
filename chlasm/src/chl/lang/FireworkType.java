package chl.lang;

public enum FireworkType implements LHEnum {
	STARBURST,
	GALAXY;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

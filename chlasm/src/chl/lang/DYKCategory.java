package chl.lang;

public enum DYKCategory implements LHEnum {
	NAVIGATION,
	CREATURE,
	VILLAGE_LIFE,
	MIRACLES,
	MISC;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

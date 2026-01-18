package chl.lang;

public enum SpellIconInfo implements LHEnum {
	BASIC,
	TOWN_CENTRE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

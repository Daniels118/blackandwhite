package chl.lang;

public enum ShowNeedsInfo implements LHEnum {
	HUNGER,
	LIFE,
	HIDING,
	WORKSHOP_WOOD;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

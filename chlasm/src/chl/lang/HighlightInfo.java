package chl.lang;

public enum HighlightInfo implements LHEnum {
	SCRIPT_NORMAL,
	SCRIPT_BRONZE,
	SCRIPT_SILVER,
	SCRIPT_GOLD;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum GuidanceAlignment implements LHEnum {
	NONE,
	GOOD,
	EVIL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

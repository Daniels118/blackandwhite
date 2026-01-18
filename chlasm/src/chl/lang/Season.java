package chl.lang;

public enum Season implements LHEnum {
	SPRING,
	SUMMER,
	AUTUMN,
	WINTER;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

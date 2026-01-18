package chl.lang;

public enum DetailLevel implements LHEnum {
	HIGH,
	NORMAL,
	LOW;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

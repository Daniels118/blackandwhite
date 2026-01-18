package chl.lang;

public enum LeaderAnimation implements LHEnum {
	NORMAL,
	FAST,
	FRANTIC;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

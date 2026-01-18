package chl.lang;

public enum SpeedMax implements LHEnum {
	WALK,
	RUN;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum PenInfo implements LHEnum {
	FEED,
	TRAIN,
	SLEEP,
	DISCIPLINE,
	PLEASURE,
	WALKWAY;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum TownAttitudeToCreature implements LHEnum {
	NONE,
	CURIOSITY,
	FEAR,
	RESPECT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

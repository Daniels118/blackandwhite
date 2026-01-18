package chl.lang;

public enum PlayerInfo implements LHEnum {
	NORMAL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

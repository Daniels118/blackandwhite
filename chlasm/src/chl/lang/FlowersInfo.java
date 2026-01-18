package chl.lang;

//CI only
public enum FlowersInfo implements LHEnum {
	WHITE_ONE,
	PINK;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum PostType implements LHEnum {
	SIDE_LINE,
	GOAL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

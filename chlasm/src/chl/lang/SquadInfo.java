package chl.lang;

public enum SquadInfo implements LHEnum {
	LINE,
	LARGE_LINE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

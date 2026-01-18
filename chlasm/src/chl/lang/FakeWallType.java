package chl.lang;

public enum FakeWallType implements LHEnum {
	SECTION_INFO_BASIC;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

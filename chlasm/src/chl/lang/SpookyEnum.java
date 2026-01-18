package chl.lang;

public enum SpookyEnum implements LHEnum {
	NAME_01,
	NAME_02,
	NAME_03,
	NAME_04,
	NAME_05;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

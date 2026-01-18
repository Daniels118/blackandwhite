package chl.lang;

public enum CitadelType implements LHEnum {
	DEFENCE,
	CIVIC,
	PEN,
	WORSHIP;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

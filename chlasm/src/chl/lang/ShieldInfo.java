package chl.lang;

public enum ShieldInfo implements LHEnum {
	MAGIC,
	PHYSICAL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

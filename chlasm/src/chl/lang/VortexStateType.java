package chl.lang;

public enum VortexStateType implements LHEnum {
	INACTIVE,
	ACTIVE,
	FADE_IN,
	FADE_OUT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

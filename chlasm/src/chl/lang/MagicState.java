package chl.lang;

public enum MagicState implements LHEnum {
	BEGIN,
	NORMAL,
	DEATH,
	BEGIN_RADIATE,
	BEGIN_SPIRAL,
	NORMAL_SPIRAL;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

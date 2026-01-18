package chl.lang;

public enum DeleteMode implements LHEnum {
	WITH_FADE,
	WITH_EXPLOSION,
	WITH_TEMPLE_EXPLODE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

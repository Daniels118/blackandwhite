package chl.lang;

public enum SoundSurfaceType implements LHEnum {
	NONE,
	GRASS,
	GRAVEL,
	HARD,
	MUD,
	SNOW,
	DEEP_WATER,
	SHALLOW_WATER,
	LOOSE_FOLIAGE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

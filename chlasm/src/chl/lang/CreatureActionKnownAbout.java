package chl.lang;

public enum CreatureActionKnownAbout implements LHEnum {
	BUILD,
	USE_FIELD,
	USE_TOTEM,
	USE_STORAGE_PIT,
	FISH,
	DANCE,
	NONE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

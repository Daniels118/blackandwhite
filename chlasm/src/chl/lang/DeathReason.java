package chl.lang;

public enum DeathReason implements LHEnum {
	NONE,
	STARVING,
	SPELL,
	ANIMAL,
	CHANT,
	PLAYER_INTERACTION,
	PLAYER_INTERACTION_DROWN,
	SACRIFICE,
	EXHAUSTION,
	OLD_AGE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

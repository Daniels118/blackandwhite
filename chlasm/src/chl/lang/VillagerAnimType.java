package chl.lang;

public enum VillagerAnimType implements LHEnum {
	WALKING,
	RUNNING,
	PLAYING,
	SCARED,
	SLEEPING,
	DEATH_ELECTRIC,
	DEATH_FIRE,
	DEATH_AGE,
	DEATH_SQUASH,
	EXCITED,
	KNACKERED,
	WAITING,
	HAVING_A_SHIT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

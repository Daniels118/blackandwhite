package chl.lang;

public enum CreatureActionLearningType implements LHEnum {
	NORMAL,
	MAGIC;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

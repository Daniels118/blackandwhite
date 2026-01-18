package chl.lang;

public enum EffectType implements LHEnum {
	BURN,
	CRUSH,
	HIT,
	HEAL,
	APPLY_FORCE,
	ALIGNMENT_MODIFICATION,
	BELIEF_MODIFICATION;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

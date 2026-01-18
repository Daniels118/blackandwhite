package chl.lang;

public enum CastRuleType implements LHEnum {
	ANYWHERE,
	ON_LAND,
	IN_INFLUENCE,
	ON_LAND_IN_INFLUENCE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

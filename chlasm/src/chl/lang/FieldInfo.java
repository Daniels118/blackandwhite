package chl.lang;

public enum FieldInfo implements LHEnum {
	WHEAT,
	WHEAT_WITH_FENCE,
	CORN,
	CORN_WITH_FENCE,
	CEREAL,
	CEREAL_WITH_FENCE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

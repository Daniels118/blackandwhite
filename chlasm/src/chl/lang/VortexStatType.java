package chl.lang;

public enum VortexStatType implements LHEnum {
	TOTAL_OBJECTS,
	RESOURCE_FOOD,
	RESOURCE_WOOD,
	VILLAGER,
	ONESHOT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

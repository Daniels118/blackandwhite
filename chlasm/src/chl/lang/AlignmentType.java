package chl.lang;

public enum AlignmentType implements LHEnum {
	ANIMAL_NICE,
	ANIMAL_NASTY,
	CREATURE,
	PRIEST,
	SKELETON,
	VILLAGER,
	BUILDING,
	PLANT,
	FIELD,
	FEATURE,
	MOBILE_OBJECT,
	LAND,
	SCRIPT,
	UNIMPORTANT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

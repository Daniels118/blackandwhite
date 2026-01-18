package chl.lang;

public enum TownDesireInfo implements LHEnum {
	NONE(-1),
	FOR_FOOD(0),
	FOR_WOOD(1),
	FOR_PLAYTIME(2),
	FOR_PROTECTION(3),
	FOR_MERCY(4),
	FOR_ABODES(5),
	FOR_CIVIC_BUILDING(6),
	FOR_SUPPLY_WORSHIP(7),
	FOR_CHILDREN(8),
	TO_BUILD(9),
	FOR_RAIN(10),
	FOR_SUN(11),
	TO_REPAIR(12),
	TO_SUPPLY_WORKSHOP(13),
	TO_BUILD_WONDER(14),
	FOR_RELAXATION(15),
	FOR_SLEEP(16);
	
	private final int code;
	
	private TownDesireInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

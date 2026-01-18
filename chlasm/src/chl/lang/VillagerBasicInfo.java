package chl.lang;

public enum VillagerBasicInfo implements LHEnum {
	NONE(-1),
	FIRST(0),
	HOUSEWIFE_FEMALE(0),
	FORESTER_MALE(1),
	FISHERMAN_MALE(2),
	FARMER_MALE(3),
	SHEPHERD_MALE(4),
	LEADER_MALE(5),
	TRADER_MALE(6);
	
	private final int code;
	
	private VillagerBasicInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

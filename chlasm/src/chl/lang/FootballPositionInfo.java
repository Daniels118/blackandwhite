package chl.lang;

//CI only
public enum FootballPositionInfo implements LHEnum {
	NONE_FREE(-1),
	LEFT_ATTACK_1(0),
	LEFT_ATTACK_2(1),
	GOAL_1(2),
	GOAL_2(3),
	LEFT_DEFENCE_1(4),
	LEFT_DEFENCE_2(5),
	RIGHT_ATTACK_1(6),
	RIGHT_ATTACK_2(7),
	RIGHT_DEFENCE_1(8),
	RIGHT_DEFENCE_2(9);
	
	private final int code;
	
	private FootballPositionInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

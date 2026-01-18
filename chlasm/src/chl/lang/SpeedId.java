package chl.lang;

public enum SpeedId implements LHEnum {
	DEFAULT(0),
	FLEEING(1),
	BRISK_WALK(2),
	DANCING(3),
	STALKING(2),
	ATTACK(3),
	HUNTING(4),
	WANDER(4),
	RUNNING(5);
	
	private final int code;
	
	private SpeedId(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

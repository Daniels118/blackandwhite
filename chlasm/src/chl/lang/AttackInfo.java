package chl.lang;

public enum AttackInfo implements LHEnum {
	NONE(-1),
	HAND_TO_HAND(0),
	ARROW(1);
	
	private final int code;
	
	private AttackInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

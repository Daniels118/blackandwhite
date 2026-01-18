package chl.lang;

public enum LeashType implements LHEnum {
	NONE(-1),
	FIRST(0),
	EVIL(1),
	ROPE(2),
	GOOD(3);
	
	private final int code;
	
	private LeashType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

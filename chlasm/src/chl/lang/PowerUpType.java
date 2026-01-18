package chl.lang;

public enum PowerUpType implements LHEnum {
	NONE(-1),
	ONE(0),
	TWO(1),
	THREE(2);
	
	private final int code;
	
	private PowerUpType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

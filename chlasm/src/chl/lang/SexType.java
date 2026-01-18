package chl.lang;

public enum SexType implements LHEnum {
	NONE(-1),
	MALE(0),
	FEMALE(1);
	
	private final int code;
	
	private SexType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

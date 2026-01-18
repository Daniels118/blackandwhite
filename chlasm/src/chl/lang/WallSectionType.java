package chl.lang;

public enum WallSectionType implements LHEnum {
	NONE(-1),
	BASIC(0);
	
	private final int code;
	
	private WallSectionType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

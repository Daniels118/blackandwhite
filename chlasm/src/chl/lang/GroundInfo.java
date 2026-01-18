package chl.lang;

public enum GroundInfo implements LHEnum {
	NONE(-1),
	NORMAL(0),
	ABODE(1);
	
	private final int code;
	
	private GroundInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

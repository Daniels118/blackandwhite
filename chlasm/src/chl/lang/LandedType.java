package chl.lang;

public enum LandedType implements LHEnum {
	LANDED_STANDING(0),
	LANDED_ON_FRONT(1),
	LANDED_ON_BACK(2),
	LANDED_ON_RIGHT_SIDE(1),
	LANDED_ON_LEFT_SIDE(2),
	LANDED_DEFAULT(3);
	
	private final int code;
	
	private LandedType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

package chl.lang;

public enum ResourceType implements LHEnum {
	ANY(-2),
	NONE(-1),
	FOOD(0),
	WOOD(1);
	
	private final int code;
	
	private ResourceType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

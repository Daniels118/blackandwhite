package chl.lang;

public enum TribeType implements LHEnum {
	NONE(-1),
	FIRST(0),
	CELTIC(0),
	AFRICAN(1),
	AZTEC(2),
	JAPANESE(3),
	INDIAN(4),
	EGYPTIAN(5),
	GREEK(6),
	NORSE(7),
	TIBETAN(8),
	LAST(9);
	
	private final int code;
	
	private TribeType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

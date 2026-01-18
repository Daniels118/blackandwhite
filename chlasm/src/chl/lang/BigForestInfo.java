package chl.lang;

public enum BigForestInfo implements LHEnum {
	NONE(-1),
	FOREST_1(0),
	FOREST_2(1),
	FOREST_3(2),
	FOREST_4(3);
	
	private final int code;
	
	private BigForestInfo(int code) {
		this.code = code;
	}
	
	@Override
	public int value() {
		return code;
	}
}

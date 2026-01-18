package chl.lang;

public enum FishFarmInfo implements LHEnum {
	NONE(-1),
	NORMAL(0);
	
	private final int code;
	
	private FishFarmInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

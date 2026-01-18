package chl.lang;

public enum ClimateInfo implements LHEnum {
	CLIMATE_INFO_NONE(-1),
	CLIMATE_INFO_WORLD_DEFAULT(0),
	CLIMATE_INFO_DESERT(1),
	CLIMATE_INFO_POLAR(2),
	CLIMATE_INFO_JUNGLE(3),
	CLIMATE_INFO_GENERAL_SNOW(4),
	CLIMATE_INFO_COLD_RAIN(5),
	CLIMATE_INFO_INTERNET(6);
	
	private final int code;
	
	private ClimateInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

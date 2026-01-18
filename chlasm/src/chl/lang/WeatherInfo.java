package chl.lang;

public enum WeatherInfo implements LHEnum {
	NONE(-1),
	MONSOON(0),
	RAIN(1),
	DRIZZLE(2),
	SLEET(3),
	SNOW_NO_SETTLE(4),
	SNOW(5),
	BLIZZARD(6);
	
	public final int code;
	
	private WeatherInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

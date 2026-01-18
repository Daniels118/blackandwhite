package chl.lang;

public enum EffectInfo implements LHEnum {
	NONE(-1),
	HIT(0),
	ARROW(1),
	BURN(2),
	CRUSH(3),
	WATER_PAIL(4),
	BUILD(5),
	CHOP(6),
	DIG(7),
	CITADEL_HIT(8),
	NURTURE(9),
	FIELD_CROP_GROW(10),
	WEATHER_LIGHTNING(11);
	
	private final int code;
	
	private EffectInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

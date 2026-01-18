package chl.lang;

public enum FoodType implements LHEnum {
	NONE(0),
	MEAT(1),
	VEGETABLE(2),
	GRAZE(4),
	ANY(3),
	MEAT_VEGETABLE(3),
	VEGETABLE_GRAZE(6);
	
	private final int code;
	
	private FoodType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

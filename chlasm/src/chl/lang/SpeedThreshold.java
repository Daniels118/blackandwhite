package chl.lang;

public enum SpeedThreshold implements LHEnum {
	VILLAGER_NORMAL,
	VILLAGER_MAN,
	ANIMAL_COW,
	ANIMAL_HORSE,
	ANIMAL_SHEEP,
	ANIMAL_PIG,
	ANIMAL_LION,
	ANIMAL_TIGER,
	ANIMAL_LEOPARD,
	ANIMAL_WOLF;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

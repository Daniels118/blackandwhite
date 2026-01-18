package chl.lang;

public enum TownHappinessConsideration implements LHEnum {
	AMOUNT_OF_FOOD,
	AMOUNT_OF_WOOD,
	NUMBER_OF_BUILDINGS,
	NUMBER_OF_PEOPLE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

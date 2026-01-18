package chl.lang;

public enum PotType implements LHEnum {
	POT,
	PILE_FOOD,
	PILE_WOOD;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

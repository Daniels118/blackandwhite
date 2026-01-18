package chl.lang;

public enum VillagerMakeDinner implements LHEnum {
	YES,
	MAKING,
	NO,
	NOT_YET;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

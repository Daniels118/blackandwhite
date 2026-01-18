package chl.lang;

public enum VillagerMustBe implements LHEnum {
	DONT_CARE,
	AT_HOME,
	NOT_AT_HOME;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

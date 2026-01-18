package chl.lang;

public enum VillagerNumber implements LHEnum {
	HOUSEWIFE,
	FORESTER,
	FISHERMAN,
	FARMER,
	SHEPHERD,
	LEADER,
	TRADER;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

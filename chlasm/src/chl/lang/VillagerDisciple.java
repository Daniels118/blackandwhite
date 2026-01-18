package chl.lang;

public enum VillagerDisciple implements LHEnum {
	NONE,
	FARMER,
	FORESTER,
	FISHERMAN,
	BUILDER,
	BREEDER,
	PROTECTION,
	MISSIONARY,
	CRAFTSMAN,
	TRADER,
	CHANGE_HOUSE,
	WORSHIP,
	FROM_VORTEX;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

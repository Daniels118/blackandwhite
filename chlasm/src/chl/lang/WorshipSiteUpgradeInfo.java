package chl.lang;

public enum WorshipSiteUpgradeInfo implements LHEnum {
	FAST_FOOD;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum WorhipSiteInfo implements LHEnum {
	CELTIC,
	AFRICAN,
	AZTEC,
	JAPANESE,
	INDIAN,
	EGYPTIAN,
	GREEK,
	NORSE,
	TIBETAN;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

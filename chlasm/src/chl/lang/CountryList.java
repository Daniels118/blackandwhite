package chl.lang;

public enum CountryList implements LHEnum {
	NONE,
	COUNTRY_1,
	COUNTRY_2,
	COUNTRY_3,
	COUNTRY_4,
	COUNTRY_5,
	COUNTRY_6,
	COUNTRY_7,
	COUNTRY_8;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

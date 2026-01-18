package chl.lang;

public enum DiscreteAlignmentValue implements LHEnum {
	DEVILISH,
	EVIL,
	BAD,
	NEUTRAL,
	NICE,
	GOOD,
	ANGELIC;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

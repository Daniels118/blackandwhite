package chl.lang;

public enum MagicLivingInfo implements LHEnum {
	PRIEST,
	SKELETON;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

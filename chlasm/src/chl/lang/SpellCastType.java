package chl.lang;

public enum SpellCastType implements LHEnum {
	IN_HAND,
	HAND_GESTURE,
	HAND_POSITION;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

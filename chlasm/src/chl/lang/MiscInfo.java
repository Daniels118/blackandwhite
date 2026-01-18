package chl.lang;

public enum MiscInfo implements LHEnum {
	HELP_ORB,
	HELP_ORB_HOLDER,
	FIREFLY,
	ARENA_SPELL_ICON;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

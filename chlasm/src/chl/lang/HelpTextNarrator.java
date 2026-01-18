package chl.lang;

public enum HelpTextNarrator implements LHEnum {
	NONE,
	DEFAULT,
	GOOD_SPIRIT,
	EVIL_SPIRIT,
	WOMAN,
	MAN,
	OGRE,
	KHAZAR,
	LETHYS,
	NEMESIS,
	BOY,
	BIG_VOICE,
	TRAINER;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

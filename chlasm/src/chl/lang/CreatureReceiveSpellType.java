package chl.lang;

public enum CreatureReceiveSpellType implements LHEnum {
	FREEZE,
	SMALL,
	BIG,
	WEAK,
	STRONG,
	FAT,
	THIN,
	INVISIBLE,
	COMPASSIONATE,
	ANGRY,
	HUNGRY,
	FRIGHTENED,
	TIRED,
	ILL,
	THIRSTY,
	ITCHY;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

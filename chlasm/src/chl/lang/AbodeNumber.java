package chl.lang;

public enum AbodeNumber implements LHEnum {
	INVALID(-1),
	A(0),
	B(1),
	C(2),
	D(3),
	E(4),
	F(5),
	TOTEM(6),
	STORAGE_PIT(7),
	CRECHE(8),
	WORKSHOP(9),
	WONDER(10),
	GRAVEYARD(11),
	TOWN_CENTRE(12),
	FOOTBALL_PITCH(13),
	SPELL_DISPENSER(14),
	FIELD(15);
	
	private final int code;
	
	private AbodeNumber(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

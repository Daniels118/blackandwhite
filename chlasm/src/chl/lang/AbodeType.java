package chl.lang;

public enum AbodeType implements LHEnum {
	GENERAL(1),
	LIVING_QUARTERS(2),
	CIVIC(4),
	WINDMILL(10),
	TOTEM(20),
	STORAGE_PIT(36),
	CRECHE(68),
	WORKSHOP(132),
	WONDER(256),
	GRAVEYARD(516),
	TOWN_CENTRE(1028),
	CITADEL(2052),
	FOOTBALL_PITCH(4100),
	SPELL_DISPENSER(8196),
	FIELD(16388),
	ANY(32767);
	
	private final int code;
	
	private AbodeType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

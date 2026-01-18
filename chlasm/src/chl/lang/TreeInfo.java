package chl.lang;

public enum TreeInfo implements LHEnum {
	BEECH,
	BIRCH,
	CEDAR,
	CONIFER,
	CONIFER_A,
	OAK,
	OAK_A,
	OLIVE,
	PALM,
	PALM_A,
	PALM_B,
	PALM_C,
	PINE,
	BUSH,
	BUSH_A,
	BUSH_B,
	CYPRESS,
	CYPRESS_A,
	COPSE,
	COPSE_A,
	HEDGE,
	HEDGE_A,
	BURNT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

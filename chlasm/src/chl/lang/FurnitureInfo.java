package chl.lang;

public enum FurnitureInfo implements LHEnum {
	NONE(-1),
	AXE(0),
	BAG(1),
	BALL(2),
	BENCH(3),
	BUCKET(4),
	BUCKET02(5),
	CHEST(6),
	CHEST_TOP(7),
	CROOK(8),
	FISHING_ROD(9),
	FRAME(10),
	FRAME_SKIN(11),
	HAMMER(12),
	MALLET_HEAVY(13),
	SAW(14),
	SCYTHE(15),
	SPADE(16),
	STOOL(17),
	STOOL01(18),
	TABLE(19),
	TROUGH(20),
	WASHING_LINE_AMCN(21),
	WASHING_LINE_AZTC(22),
	WASHING_LINE_CELT(23),
	WASHING_LINE_EGPT(24),
	WASHING_LINE_GREK(25),
	WASHING_LINE_JAPN(26),
	WASHING_LINE_NORS(27),
	WASHING_LINE_TIBT(28),
	WELL(29),
	WELL_COVERED(30);
	
	private final int code;
	
	private FurnitureInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

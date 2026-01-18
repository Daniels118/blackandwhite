package chl.lang;

public enum MobileObjectInfo implements LHEnum {
	NONE(-1),
	EGPT_BARREL(0),
	EGPT_CART(1),
	EGPT_POT_A(2),
	EGPT_POT_B(3),
	MAGIC_FOOD(4),
	LUMP_OF_POO(5),
	WATER_JUG(6),
	ARROW(7),
	BALL(8),
	CART(9),
	FOOD_POT(10),
	WOOD_POT(11),
	PILE_FOOD(12),
	PILE_WOOD(13),
	MAGIC_WOOD(14),
	CROP(15),
	OLD_SCAFFOLD(16),
	CHAMPI(17),
	MAGIC_MUSHROOM(18),
	TOADSTOOL(19),
	CREATURE_SWAP_ORB(20),
	CREED(21),
	EYES(22),
	ARK(23),
	WHALE(24),
	ONEOFF_SPELLSEED(25),
	HANOI_PUZZLE_BASE(26),
	HANOI_PUZZLE_PART1(27),
	HANOI_PUZZLE_PART2(28),
	HANOI_PUZZLE_PART3(29),
	HANOI_PUZZLE_PART4(30),
	CAULDRON(31);
	
	private final int code;
	
	private MobileObjectInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

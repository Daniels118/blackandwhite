package chl.lang;

public enum AnimalInfo implements LHEnum {
	NONE(-1),
	LION(0),
	TIGER(1),
	WOLF(2),
	LEOPARD(3),
	SHEEP(4),
	GOAT(5),
	TORTOISE(6),
	ZEBRA(7),
	COW(8),
	HORSE(9),
	PIG(10),
	CROW(11),
	DOVE(12),
	SWALLOW(13),
	PIGEON(14),
	SEAGULL(15),
	BAT(16),
	VULTURE(17),
	CITADEL_DOVE(18),
	CITADEL_BAT(19),
	SPELL_DOVE(20),
	SPELL_BAT(21),
	SPELL_WOLF(22),
	PUZZLE_LION(23),
	PUZZLE_SHEEP(24),
	PUZZLE_WOLF(25),
	PUZZLE_VILLAGER(26),
	PUZZLE_HORSE(27),
	PUZZLE_COW(28),
	PUZZLE_TORTOISE(29),
	PUZZLE_PIG(30);
	
	private final int code;
	
	private AnimalInfo(int code) {
		this.code = code;
	}
	
	@Override
	public int value() {
		return code;
	}
}

package chl.lang;

public enum LivingType implements LHEnum {
	ANY(-1),
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
	APES(18),
	BEAR(19),
	HELP_SPIRIT(20),
	VILLAGER(21),
	LAST(22);
	
	private final int code;
	
	private LivingType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

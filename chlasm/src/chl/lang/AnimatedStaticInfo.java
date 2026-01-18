package chl.lang;

public enum AnimatedStaticInfo implements LHEnum {
	NONE(-1),
	NORS_GATE(0),
	GATE_STONE_PLINTH(1),
	PIPER_CAVE_ENTRANCE(2),
	CHESS_PION_TEAMA(3),
	CHESS_PION_TEAMB(4),
	CHESS_TOWER_TEAMA(5),
	CHESS_TOWER_TEAMB(6),
	CHESS_KNIGHT_TEAMA(7),
	CHESS_KNIGHT_TEAMB(8),
	CHESS_MAD_TEAMA(9),
	CHESS_MAD_TEAMB(10),
	CHESS_QUEEN_TEAMA(11),
	CHESS_QUEEN_TEAMB(12),
	CHESS_KING_TEAMA(13),
	CHESS_KING_TEAMB(14),
	PHONE_BOX(15);
	
	private final int code;
	
	private AnimatedStaticInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

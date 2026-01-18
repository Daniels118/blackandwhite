package chl.lang;

public enum PuzzleGameType implements LHEnum {
	NONE,
	TREE_1,
	TREE_2,
	HANOI,
	MAZE_1,
	MAZE_2,
	TOTEM_1,
	TOTEM_2,
	TOTEM_3,
	TOTEM_4,
	THESIUS1,
	THESIUS2,
	TILT1,
	TILT2,
	FISHES1,
	FISHES2,
	IMMERSION,
	IMMERSION2,
	LIONSHEEP,
	CHESS;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

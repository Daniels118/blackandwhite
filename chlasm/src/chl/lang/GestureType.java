package chl.lang;

public enum GestureType implements LHEnum {
	NONE,
	SPIRAL,
	INVERSE_SPIRAL,
	S_SHAPE,
	CIRCLE,
	SCRIBBLE,
	THREE,
	VERTICAL_SCRIBBLE,
	STAR,
	FORK_RIGHT,
	FORK_UP,
	FORK_LEFT,
	FORK_DOWN,
	HEART,
	R_SHAPE,
	SQUARE_SPIRIAL,
	CYRILLIC_L,
	E_SHAPE,
	REVERSE_S,
	INFINITY,
	W_SHAPE,
	HOUSE,																																																																																																																																																																																																																																																														
	INVERSE_SQUARE_SPIRAL,																																																																																																																																																																																																																																																														
	SQUARE_WAVE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public interface Hand {
	@Action("GET_HAND_POSITION()")
	public static Coord getPosition() {return null;}
	
	@Action("GET_HAND_STATE()")
	public static int getState() {return 0;}
}

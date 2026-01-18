package chl.lang;

public final class Game {
	private Game() {}
	
	@Action("POSITION_CLICKED(position, radius)")
	public static boolean clicked(Coord position, float radius) {return false;}
	@Action("POSITION_CLICKED(x, 0.0, z, radius)")
	public static boolean clicked(float x, float z, float radius) {return false;}
	
	@Action("GET_OBJECT_CLICKED()")
	public static LHObject getObjectClicked() {return null;}
	@Action("GET_OBJECT_HELD1()")
	public static LHObject getObjectHeld() {return null;}
	@Action("GET_HIT_OBJECT()")
	public static LHObject getObjectHit() {return null;}
	@Action("GET_OBJECT_WHICH_HIT()")
	public static LHObject getObjectWhichHit() {return null;}
}

package chl.lang;

public interface Marker extends GameThingWithPos {
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, position)")
	public static Marker create(Coord position) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, x, y, z)")
	public static Marker create(float x, float y, float z) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, x, 0f, z)")
	public static Marker create(float x, float z) {return null;}
}

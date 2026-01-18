package chl.lang;

public interface Influence extends GameThingWithPos {
	@Action("INFLUENCE_OBJECT(target, radius, 0, anti)")
	public static Influence create(LHObject target, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_OBJECT(target, radius, 0, false)")
	public static Influence create(LHObject target, float radius) {return null;}
	@Action("INFLUENCE_POSITION(position, radius, 0, anti)")
	public static Influence create(Coord position, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_POSITION(x, y, z, radius, 0, anti)")
	public static Influence create(float x, float y, float z, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_POSITION(x, y, z, radius, 0, false)")
	public static Influence create(float x, float y, float z, float radius) {return null;}
	@Action("INFLUENCE_POSITION(x, 0f, z, radius, 0, false)")
	public static Influence create(float x, float z, float radius) {return null;}
	@Action("INFLUENCE_POSITION(position, radius, 0, false)")
	public static Influence create(Coord position, float radius) {return null;}
}

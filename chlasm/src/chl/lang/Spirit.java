package chl.lang;

public final class Spirit {
	private Spirit() {}
	
	@Action("SPIRIT_EJECT(type)")
	public static void eject(SpiritType type) {}
	
	@Action("SPIRIT_POINT_POS(type, position, inWorld)")
	public static void pointTo(SpiritType type, Coord position, boolean inWorld) {}
	@Action("SPIRIT_POINT_POS(type, position, false)")
	public static void pointTo(SpiritType type, Coord position) {}
	
	@Action("SPIRIT_POINT_GAME_THING(type, target, inWorld)")
	public static void pointTo(SpiritType type, GameThingWithPos target, boolean inWorld) {}
	@Action("SPIRIT_POINT_GAME_THING(type, target, false)")
	public static void pointTo(SpiritType type, GameThingWithPos target) {}
}

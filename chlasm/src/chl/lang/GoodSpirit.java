package chl.lang;

public final class GoodSpirit {
	private GoodSpirit() {}
	
	@Action("SPIRIT_EJECT(HELP_SPIRIT_TYPE_GOOD)")
	public static void eject() {}
	
	@Action("SPIRIT_POINT_POS(HELP_SPIRIT_TYPE_GOOD, position, inWorld)")
	public static void pointTo(Coord position, boolean inWorld) {}
	@Action("SPIRIT_POINT_POS(HELP_SPIRIT_TYPE_GOOD, position, false)")
	public static void pointTo(Coord position) {}
	
	@Action("SPIRIT_POINT_GAME_THING(HELP_SPIRIT_TYPE_GOOD, target, inWorld)")
	public static void pointTo(GameThingWithPos target, boolean inWorld) {}
	@Action("SPIRIT_POINT_GAME_THING(HELP_SPIRIT_TYPE_GOOD, target, false)")
	public static void pointTo(GameThingWithPos target) {}
}

package chl.lang;

public final class EvilSpirit {
	private EvilSpirit() {}
	
	@Action("SPIRIT_EJECT(HELP_SPIRIT_TYPE_EVIL)")
	public static void eject() {}
	
	@Action("SPIRIT_POINT_POS(HELP_SPIRIT_TYPE_EVIL, position, inWorld)")
	public static void pointTo(Coord position, boolean inWorld) {}
	@Action("SPIRIT_POINT_POS(HELP_SPIRIT_TYPE_EVIL, position, false)")
	public static void pointTo(Coord position) {}
	
	@Action("SPIRIT_POINT_GAME_THING(HELP_SPIRIT_TYPE_EVIL, target, inWorld)")
	public static void pointTo(GameThingWithPos target, boolean inWorld) {}
	@Action("SPIRIT_POINT_GAME_THING(HELP_SPIRIT_TYPE_EVIL, target, false)")
	public static void pointTo(GameThingWithPos target) {}
}

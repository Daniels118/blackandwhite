package chl.lang;

public final class Factory {
	private Factory() {}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ABODE, subtype, position)")
	public static Abode createAbode(AbodeType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ABODE, subtype, pos)")
	public static Abode createAbode(AbodeType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ANIMAL, subtype, position)")
	public static Animal createAnimal(LivingType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ANIMAL, subtype, pos)")
	public static Animal createAnimal(LivingType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ANIMATED_STATIC, subtype, position)")
	public static AnimatedStatic createAnimatedStatic(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ANIMATED_STATIC, subtype, pos)")
	public static AnimatedStatic createAnimatedStatic(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ARK, subtype, position)")
	public static Ark createArk(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ARK, subtype, pos)")
	public static Ark createArk(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_BALL, 0, position)")
	public static Ball createBall(Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_BALL, 0, pos)")
	public static Ball createBall(float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_BIRD, subtype, position)")
	public static Bird createBird(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_BIRD, subtype, pos)")
	public static Bird createBird(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_VILLAGER_CHILD, subtype, position)")
	public static Child createChild(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_VILLAGER_CHILD, subtype, pos)")
	public static Child createChild(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_CITADEL, subtype, position)")
	public static Citadel createCitadel(CitadelType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_CITADEL, subtype, pos)")
	public static Citadel createCitadel(CitadelType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_COMPUTER_PLAYER, subtype, position)")
	public static ComputerPlayer createComputerPlayer(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_COMPUTER_PLAYER, subtype, pos)")
	public static ComputerPlayer createComputerPlayer(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATURE_CREATE_RELATIVE_TO_CREATURE(ref, scale, position, type)")
	public static Creature createCreature(Creature ref, float scale, Coord position, CreatureType type) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_CREATURE, subtype, position)")
	public static Creature createCreature(CreatureType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_CREATURE, subtype, pos)")
	public static Creature createCreature(CreatureType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("DANCE_CREATE(obj, type, position, duration)")
	public static Dance createDance(LHObject obj, DanceInfo type, Coord position, float duration) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_DEAD_TREE, subtype, position)")
	public static DeadTree createDeadTree(TreeType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_DEAD_TREE, subtype, pos)")
	public static DeadTree createDeadTree(TreeType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_DIE, subtype, position)")
	public static Die createDie(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_DIE, subtype, pos)")
	public static Die createDie(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_FEATURE, subtype, position)")
	public static Feature createFeature(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_FEATURE, subtype, pos)")
	public static Feature createFeature(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_FIELD, subtype, position)")
	public static Field createField(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_FIELD, subtype, pos)")
	public static Field createField(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("FLOCK_CREATE(position)")
	public static Flock createFlock(Coord position) {return null;}
	
	@Action("CREATE_HIGHLIGHT(type, pos, challenge)")
	public static Highlight createHighlight(HighlightInfo type, Coord pos, Challenge challenge) {return null;}
	
	@Action("INFLUENCE_OBJECT(target, radius, 0, anti)")
	public static Influence createInfluence(LHObject target, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_OBJECT(target, radius, 0, false)")
	public static Influence createInfluence(LHObject target, float radius) {return null;}
	@Action("INFLUENCE_POSITION(position, radius, 0, anti)")
	public static Influence createInfluence(Coord position, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_POSITION(x, y, z, radius, 0, anti)")
	public static Influence createInfluence(float x, float y, float z, float radius, boolean anti) {return null;}
	@Action("INFLUENCE_POSITION(x, y, z, radius, 0, false)")
	public static Influence createInfluence(float x, float y, float z, float radius) {return null;}
	@Action("INFLUENCE_POSITION(x, 0f, z, radius, 0, false)")
	public static Influence createInfluence(float x, float z, float radius) {return null;}
	@Action("INFLUENCE_POSITION(position, radius, 0, false)")
	public static Influence createInfluence(Coord position, float radius) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, position)")
	public static Marker createMarker(Coord position) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, x, y, z)")
	public static Marker createMarker(float x, float y, float z) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MARKER, 0, x, 0f, z)")
	public static Marker createMarker(float x, float z) {return null;}
	
	@Action("CREATE_MIST(pos, scale, r, g, b, transparency, heightRatio)")
	public static Mist createMist(Coord pos, float scale, float r, float g, float b, float transparency, float heightRatio) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MOBILE_OBJECT, subtype, position)")
	public static MobileObject createMobileObject(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_MOBILE_OBJECT, subtype, pos)")
	public static MobileObject createMobileObject(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_MOBILE_STATIC, subtype, position)")
	public static MobileStatic createMobileStatic(MobileStaticInfo subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_MOBILE_STATIC, subtype, pos)")
	public static MobileStatic createMobileStatic(MobileStaticInfo subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ONE_SHOT_SPELL, subtype, position)")
	public static OneShotSpell createOneShotSpell(SpellSeedType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ONE_SHOT_SPELL, subtype, pos)")
	public static OneShotSpell createOneShotSpell(SpellSeedType subtype, float angle, float scale, Coord pos) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ONE_SHOT_SPELL_IN_HAND, subtype, position)")
	public static OneShotSpell createOneShotSpellInHand(SpellSeedType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ONE_SHOT_SPELL_IN_HAND, subtype, pos)")
	public static OneShotSpell createOneShotSpellInHand(SpellSeedType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_POO, subtype, position)")
	public static Poo createPoo(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_POO, subtype, pos)")
	public static Poo createPoo(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_PUZZLE_GAME, subtype, position)")
	public static PuzzleGame createPuzzleGame(PuzzleGameType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_PUZZLE_GAME, subtype, pos)")
	public static PuzzleGame createPuzzleGame(PuzzleGameType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE_REWARD(reward, position, fromSky)")
	public static Reward createReward(RewardObjectInfo reward, Coord position, boolean fromSky) {return null;}
	@Action("CREATE_REWARD(reward, town, position, fromSky)")
	public static Reward createReward(RewardObjectInfo reward, Town town, Coord position, boolean fromSky) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_ROCK, subtype, position)")
	public static Rock createRock(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_ROCK, subtype, pos)")
	public static Rock createRock(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_SCAFFOLD, subtype, position)")
	public static Scaffold createScaffold(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_SCAFFOLD, subtype, pos)")
	public static Scaffold createScaffold(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_SPECIAL_FIELD, subtype, position)")
	public static SpecialField createSpecialField(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_SPECIAL_FIELD, subtype, pos)")
	public static SpecialField createSpecialField(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_SPELL_DISPENSER, subtype, position)")
	public static SpellDispenser createSpellDispenser(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_SPELL_DISPENSER, subtype, pos)")
	public static SpellDispenser createSpellDispenser(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_SPELL_SEED, subtype, position)")
	public static SpellSeed createSpellSeed(SpellSeedType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_SPELL_SEED, subtype, pos)")
	public static SpellSeed createSpellSeed(SpellSeedType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_STORE, subtype, position)")
	public static Store createStore(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_STORE, subtype, pos)")
	public static Store createStore(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE_TIMER(timeout)")
	public static Timer createTimer(float timeout) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_TOTEM, subtype, position)")
	public static Totem createTotem(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_TOTEM, subtype, pos)")
	public static Totem createTotem(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_TOTEM_STATUE, subtype, position)")
	public static TotemStatue createTotemStatue(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_TOTEM_STATUE, subtype, pos)")
	public static TotemStatue createTotemStatue(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_TOWN, subtype, position)")
	public static Town createTown(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_TOWN, subtype, pos)")
	public static Town createTown(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_TREE, subtype, position)")
	public static Tree createTree(TreeType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_TREE, subtype, pos)")
	public static Tree createTree(TreeType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE_RANDOM_VILLAGER_OF_TRIBE(tribe, pos)")
	public static Villager createVillager(TribeType tribe, Coord pos) {return null;}
	@Action("CREATE(SCRIPT_OBJECT_TYPE_VILLAGER, subtype, position)")
	public static Villager createVillager(VillagerInfo subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_VILLAGER, subtype, pos)")
	public static Villager createVillager(VillagerInfo subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_VORTEX, subtype, position)")
	public static Vortex createVortex(VortexType subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_VORTEX, subtype, pos)")
	public static Vortex createVortex(VortexType subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_WEATHER_THING, subtype, position)")
	public static Weather createWeather(WeatherInfo subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_WEATHER_THING, subtype, pos)")
	public static Weather createWeather(WeatherInfo subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_WHALE, subtype, position)")
	public static Whale createWhale(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_WHALE, subtype, pos)")
	public static Whale createWhale(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
	
	@Action("CREATE(SCRIPT_OBJECT_TYPE_WORSHIP_SITE, subtype, position)")
	public static WorshipSite createWorshipSite(ScriptObjectSubtype subtype, Coord position) {return null;}
	@Action("CREATE_WITH_ANGLE_AND_SCALE(angle, scale, SCRIPT_OBJECT_TYPE_WORSHIP_SITE, subtype, pos)")
	public static WorshipSite createWorshipSite(ScriptObjectSubtype subtype, float angle, float scale, Coord pos) {return null;}
}

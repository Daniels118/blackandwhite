package chl.lang;

public enum SpotVisualType implements LHEnum {
	NONE(0),
	APPLY_SPELL_EFFECT(1),		//object only?
	GRIP_LANDSCAPE(2),
	SPELL_SUCCEED_CAST(3),
	SPELL_FAIL_CAST(4),
	SINGLE_FIREWORK(5),
	TOWN_FIREWORKS(6),
	TOWN_FIREWORKS_PU1(7),
	TOWN_FIREWORKS_PU2(8),
	MAGIC_OBJECT_CREATED(9),
	COMMAND_SUCCEED(10),
	COMMAND_FAIL(11),
	CREATURE_TARGET(12),
	CREATURE_CAST_VISUAL(13),
	VILLAGER_TELEPORT(14),
	FIRE_FX(15),		//object only?
	FIRE_FX_ON_OBJECT(16),
	MAGIC_FX(17),
	MAGIC_FX_ON_OBJECT(18),
	MAGIC_FX_ON_CITADEL(19),
	/**Sort of northern lights (from-to, no scale)
	 */
	MAGIC_BEAM(20),
	MAGIC_BEAM_ON_CITADEL(21),
	STEAM(22),
	SMOKE(23),
	DUST(24),
	/**Fire with smoke
	 */
	BONFIRE(25),
	/**Dark smoke (spot)
	 */
	EVIL_SMOKE(26),
	OBJECT_APPEAR(27),
	OBJECT_DISAPPEAR(28),
	BANG(29),
	SING_STONES_GLOW(30),
	/**Spawned from the town center when you conquer the town (spot)
	 */
	PLAYER_ICON_FOUNTAIN(31),
	EXPLOSION_CITADEL(32),
	HEAL_FX(33),		//object only?
	HIGHLIGHT_ON_OBJECT(34),
	LIGHTNING_SINGLE_STRIKE(35),
	BEAM_EXPLOSION_FX(36),
	BUTTERFLIES(37),
	BUTTERFLIES_ON_OBJECT(38),
	FLIES(39),
	FLIES_ON_OBJECT(40),
	MAGIC_BEAM_CREATURE_SWAP(41),
	/**Giant spherical glow (spot, no scale)
	 */
	FLASH(42),
	/**Carnival confetti
	 */
	TICKER_TAPE(43),
	FOREST_CREATED(44),
	SINGING_STONES_HEAL(45),
	PILEFOOD_SPEEDUP(46),
	SEE_THIS_BEAM(47),
	SEE_THIS_BEAM2(48),
	TEST(49);
	
	private final int code;
	
	private SpotVisualType(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

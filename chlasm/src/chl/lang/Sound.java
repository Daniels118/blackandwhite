package chl.lang;

public final class Sound {
	private Sound() {}
	
	@Action("GAME_PLAY_SAY_SOUND_EFFECT(false, sound, position, true)")
	public static void startSaySound(int sound, Coord position) {}
	@Action("GAME_PLAY_SAY_SOUND_EFFECT(false, sound, 0f, 0f, 0f, false)")
	public static void startSaySound(int sound) {}
	
	@Action("GAME_PLAY_SAY_SOUND_EFFECT(true, sound, position, true)")
	public static void startSayExtraSound(int sound, Coord position) {}
	@Action("GAME_PLAY_SAY_SOUND_EFFECT(true, sound, 0f, 0f, 0f, false)")
	public static void startSayExtraSound(int sound) {}
}

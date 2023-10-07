/* Copyright (c) 2023 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.bw.chl.model;

import java.util.HashMap;
import java.util.Map;

import it.ld.bw.chl.exceptions.InvalidNativeFunctionException;

/* When a function returns a value, if the return value is going to be put into a variable, then
 * POPF must be used; if the return value has to be thrown away, then POPO is used.
 * When an Object is passed to a function, then PUSHO is used.
 * When an ObjectFloat is passed to a function, then PUSHF is used, unless you want to refer to a null object,
 * in which case you should use "PUSHO 0" (see FLOCK_DETACH).
 * When an ObjectInt is passed to a function, then PUSHI is used.
 * */

//TODO complete functions description

public enum NativeFunction {
	NONE(),
	SET_CAMERA_POSITION("Coord position"),
	SET_CAMERA_FOCUS("Coord position"),
	MOVE_CAMERA_POSITION("Coord position, float time"),
	MOVE_CAMERA_FOCUS("Coord position, float time"),
	GET_CAMERA_POSITION("", "Coord"),
	GET_CAMERA_FOCUS("", "Coord"),
	SPIRIT_EJECT("ENUM_HELP_SPIRIT_TYPE spirit"),
	SPIRIT_HOME("ENUM_HELP_SPIRIT_TYPE spirit"),
	SPIRIT_POINT_POS("ENUM_HELP_SPIRIT_TYPE spirit, Coord position, bool inWorld"),
	SPIRIT_POINT_GAME_THING("ENUM_HELP_SPIRIT_TYPE spirit, ObjectFloat target, bool inWorld"),
	GAME_THING_FIELD_OF_VIEW("ObjectFloat object", "bool"),
	POS_FIELD_OF_VIEW("Coord position", "bool"),
	RUN_TEXT("bool singleLine, ENUM_HELP_TEXT textID, int withInteraction"),
	TEMP_TEXT("bool singleLine, StrPtr string, int withInteraction"),
	TEXT_READ("", "bool"),
	GAME_THING_CLICKED("ObjectFloat object", "bool"),
	SET_SCRIPT_STATE("ObjectFloat object, int"),
	SET_SCRIPT_STATE_POS("ObjectInt object, Coord position"),
	SET_SCRIPT_FLOAT("ObjectInt object, float"),
	SET_SCRIPT_ULONG("Object object, int, int"),
	GET_PROPERTY("Property prop, ObjectFloat object", "int|float"),	//Call with SYS or SYS2 depending on the property
	SET_PROPERTY("Property prop, ObjectFloat object, float val", true),
	GET_POSITION("ObjectFloat object", "Coord"),
	SET_POSITION("ObjectFloat object, Coord position"),
	GET_DISTANCE("Coord p0, Coord p1", "float"),
	CALL("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, Coord position, bool alwaysFalse", "float"),
	CREATE("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, Coord position", "Object"),
	RANDOM("float min, float max", "float"),
	DLL_GETTIME("", "float"),			//"time" statement, see section 12.13 of CHL doc.
	START_CAMERA_CONTROL("", "bool"),
	END_CAMERA_CONTROL(),
	SET_WIDESCREEN("bool enabled"),
	MOVE_GAME_THING("ObjectFloat object, Coord position, float radius"),
	SET_FOCUS("ObjectFloat object, Coord position"),
	HAS_CAMERA_ARRIVED("", "bool"),
	FLOCK_CREATE("Coord position", "Object"),
	FLOCK_ATTACH("ObjectFloat obj, ObjectFloat flock, bool", "Object"),	//Always discard return value with POPO
	FLOCK_DETACH("ObjectFloat obj, ObjectFloat flock", "Object"),	/*
								Use "PUSHO 0" to refer to a random object in the flock.
								Returns the detached object (useful when detaching a random one).*/
	FLOCK_DISBAND("ObjectFloat flock"),
	ID_SIZE("ObjectFloat flock", "float"),
	FLOCK_MEMBER("ObjectFloat obj, ObjectFloat flock", "bool"),
	GET_HAND_POSITION("", "Coord"),
	PLAY_SOUND_EFFECT("int sound, int soundbank, Coord position, bool withPosition"),
	START_MUSIC("int music"),
	STOP_MUSIC(),
	ATTACH_MUSIC("int music, ObjectFloat target"),
	DETACH_MUSIC("ObjectFloat object"),
	OBJECT_DELETE("ObjectFloat obj, int withFade"),	//Use "ZERO [varname]" just after to clear the object reference.
	FOCUS_FOLLOW("ObjectFloat target"),
	POSITION_FOLLOW("ObjectFloat target"),	//Never found
	CALL_NEAR("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, Coord position, float radius, bool", "Object"),
	SPECIAL_EFFECT_POSITION("int, Coord position, float", "float"),
	SPECIAL_EFFECT_OBJECT("int, ObjectFloat obj, float", "float"),
	DANCE_CREATE("ObjectFloat obj, int, Coord position, float", "Object"),
	CALL_IN("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, ObjectFloat obj, bool", "Object"),
	CHANGE_INNER_OUTER_PROPERTIES("ObjectFloat obj, float inner, float outer, float calm"),
	SNAPSHOT("bool quest, Coord position, Coord focus, float success, float alignment, ENUM_HELP_TEXT titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"),
	GET_ALIGNMENT("int", "float", true),
	SET_ALIGNMENT(2),					//Never found; guess (int, float)
	INFLUENCE_OBJECT("ObjectFloat obj, float, int, int", "Object"),
	INFLUENCE_POSITION("Coord position, float, int, int", "Object"),
	GET_INFLUENCE("float, bool, Coord position", "float"),
	SET_INTERFACE_INTERACTION("ENUM_SCRIPT_INTERFACE_LEVELS level", true),
	PLAYED("ObjectFloat obj", "bool"),
	RANDOM_ULONG("int min, int max", "int", true),
	SET_GAMESPEED("float speed", true),
	CALL_IN_NEAR("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, ObjectFloat obj, Coord pos, float radius, bool excludingScripted", "Object"),
	OVERRIDE_STATE_ANIMATION("ObjectFloat obj, DETAIL_ANIM_TYPES animType"),
	CREATURE_CREATE_RELATIVE_TO_CREATURE("ObjectFloat, float, Coord position, int", "Object"),
	CREATURE_LEARN_EVERYTHING("ObjectFloat creature"),
	CREATURE_SET_KNOWS_ACTION("ObjectFloat creature, ENUM_CREATURE_ACTION_TYPES typeOfAction, int action, ENUM_SCRIPT_BOOL knows"),
	CREATURE_SET_AGENDA_PRIORITY("ObjectFloat creature, float priority"),
	CREATURE_TURN_OFF_ALL_DESIRES(1),	//Never found; guess (ObjectFloat creature)
	CREATURE_LEARN_DISTINCTION_ABOUT_ACTIVITY_OBJECT(4),	//Never found
	CREATURE_DO_ACTION("ObjectFloat creature, ENUM_CREATURE_ACTION, ObjectFloat target, ObjectFloat withObject"),
	IN_CREATURE_HAND("ObjectFloat obj, ObjectFloat creature", "bool"),
	CREATURE_SET_DESIRE_VALUE("ObjectFloat creature, ENUM_CREATURE_DESIRES desire, float value"),
	CREATURE_SET_DESIRE_ACTIVATED3("ObjectFloat creature, ENUM_CREATURE_DESIRES desire, ENUM_SCRIPT_BOOL active"),
	CREATURE_SET_DESIRE_ACTIVATED2("ObjectFloat creature, ENUM_SCRIPT_BOOL active"),
	CREATURE_SET_DESIRE_MAXIMUM("ObjectFloat creature, ENUM_CREATURE_DESIRES desire, float value"),
	CONVERT_CAMERA_POSITION("int", "Coord"),
	CONVERT_CAMERA_FOCUS("int", "Coord"),
	CREATURE_SET_PLAYER("ObjectFloat creature"),
	START_COUNTDOWN_TIMER(1),			//Never found
	CREATURE_INITIALISE_NUM_TIMES_PERFORMED_ACTION(2),	//Never found
	CREATURE_GET_NUM_TIMES_ACTION_PERFORMED(2, 1),	//Never found
	REMOVE_COUNTDOWN_TIMER(),			//Never found
	GET_OBJECT_DROPPED(1, 1),			//Never found
	CLEAR_DROPPED_BY_OBJECT(1),			//Never found; guess (ObjectFloat creature)
	CREATE_REACTION("ObjectFloat object, ENUM_REACTION reaction"),
	REMOVE_REACTION("ObjectFloat object"),	//Never found
	GET_COUNTDOWN_TIMER(0, 1),			//Never found
	START_DUAL_CAMERA("ObjectFloat obj1, ObjectFloat obj2"),
	UPDATE_DUAL_CAMERA("ObjectFloat obj1, ObjectFloat obj2"),	//Never found
	RELEASE_DUAL_CAMERA(),
	SET_CREATURE_HELP(1),				//Never found
	GET_TARGET_OBJECT("ObjectFloat obj", "Object"),
	CREATURE_DESIRE_IS(2, 1),			//Never found
	COUNTDOWN_TIMER_EXISTS("", "bool"),	//Never found
	LOOK_GAME_THING("ENUM_HELP_SPIRIT_TYPE spirit, ObjectFloat target"),
	GET_OBJECT_DESTINATION("ObjectFloat obj", "Coord"),	//Never found
	CREATURE_FORCE_FINISH("ObjectFloat creature"),	//Never found
	HIDE_COUNTDOWN_TIMER(),				//Never found
	GET_ACTION_TEXT_FOR_OBJECT("ObjectFloat obj", "int"),	//The return value is used as second parameter in RUN_TEXT
	CREATE_DUAL_CAMERA_WITH_POINT("ObjectFloat obj, Coord position"),	//Never found
	SET_CAMERA_TO_FACE_OBJECT(2),		//Never found
	MOVE_CAMERA_TO_FACE_OBJECT("ObjectFloat target, float distance, float time"),
	GET_MOON_PERCENTAGE("", "float"),	//Never found
	POPULATE_CONTAINER("ObjectFloat obj, float quantity, ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype"),
	ADD_REFERENCE(1, 1),				//Never found
	REMOVE_REFERENCE(1, 1),				//Never found
	SET_GAME_TIME("float time"),
	GET_GAME_TIME("", "float"),
	GET_REAL_TIME(0, 1),				//Never found
	GET_REAL_DAY1(0, 1),				//Never found
	GET_REAL_DAY2(0, 1),				//Never found
	GET_REAL_MONTH(0, 1),				//Never found
	GET_REAL_YEAR(0, 1),				//Never found
	RUN_CAMERA_PATH("int cameraEnum"),
	START_DIALOGUE("", "bool"),
	END_DIALOGUE(),
	IS_SPIRIT_READY("", "bool"),
	CHANGE_WEATHER_PROPERTIES("ObjectFloat storm, float temperature, float rainfall, float snowfall, float overcast, float fallspeed"),
	CHANGE_LIGHTNING_PROPERTIES("ObjectFloat storm, float sheetmin, float sheetmax, float forkmin, float forkmax"),
	CHANGE_TIME_FADE_PROPERTIES("ObjectFloat storm, float duration, float fadeTime"),
	CHANGE_CLOUD_PROPERTIES("ObjectFloat storm, float numClouds, float blackness, float elevation"),
	SET_HEADING_AND_SPEED("ObjectFloat, Coord position, float speed"),
	START_GAME_SPEED(),
	END_GAME_SPEED(),
	BUILD_BUILDING("Coord position, float desire"),
	SET_AFFECTED_BY_WIND("bool enabled, ObjectFloat object"),
	WIDESCREEN_TRANSISTION_FINISHED("", "bool"),
	GET_RESOURCE("int resource, ObjectFloat obj", "float"),
	ADD_RESOURCE("int resource, float quantity, ObjectFloat obj", "float"),
	REMOVE_RESOURCE("int resource, float quantity, ObjectFloat obj", "float"),
	GET_TARGET_RELATIVE_POS("Coord p0, Coord p1, float, float", "Coord"),
	STOP_POINTING("ENUM_HELP_SPIRIT_TYPE spirit"),
	STOP_LOOKING("ENUM_HELP_SPIRIT_TYPE spirit"),
	LOOK_AT_POSITION("ENUM_HELP_SPIRIT_TYPE spirit, Coord position"),
	PLAY_SPIRIT_ANIM(5),				//Never found
	CALL_IN_NOT_NEAR("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, ObjectFloat obj, Coord pos, float radius, bool excludingScripted", "bool"),
	SET_CAMERA_ZONE("StrPtr filename"),	//filename is relative to folder Data\Zones, eg. Land1Zone5.exc
	GET_OBJECT_STATE("ObjectFloat obj", "float"),	//Return value must be casted to float
	REVEAL_COUNTDOWN_TIMER(),			//Never found
	SET_TIMER_TIME("ObjectFloat timer, float time"),
	CREATE_TIMER("float timeout", "Object"),
	GET_TIMER_TIME_REMAINING("ObjectFloat timer", "float"),
	GET_TIMER_TIME_SINCE_SET("ObjectFloat timer", "float"),
	MOVE_MUSIC(2),						//Never found
	GET_INCLUSION_DISTANCE("", "float"),	//Never found
	GET_LAND_HEIGHT("Coord position", "float"),
	LOAD_MAP("StrPtr path"),			//path is relative to installation, eg. "scripts/land2.txt"
	STOP_ALL_SCRIPTS_EXCLUDING("StrPtr scriptName"),	//Never found
	STOP_ALL_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename"),
	STOP_SCRIPT("StrPtr scriptName"),
	CLEAR_CLICKED_OBJECT(),
	CLEAR_CLICKED_POSITION(),			//Never found
	POSITION_CLICKED(4, 1),				//Never found
	RELEASE_FROM_SCRIPT("ObjectFloat obj"),
	GET_OBJECT_HAND_IS_OVER("", "bool"),	//Never found
	ID_POISONED_SIZE("ObjectFloat obj", "float"),
	IS_POISONED("ObjectFloat obj", "bool"),
	CALL_POISONED_IN(4, 1),				//Never found
	CALL_NOT_POISONED_IN("ENUM_SCRIPT_OBJECT_TYPES type, Subtype subtype, ObjectFloat obj, bool", "Object"),
	SPIRIT_PLAYED("ENUM_HELP_SPIRIT_TYPE spirit", "bool"),	//Never found
	CLING_SPIRIT("ENUM_HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
	FLY_SPIRIT("ENUM_HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
	SET_ID_MOVEABLE("bool moveable, ObjectFloat obj"),
	SET_ID_PICKUPABLE("bool pickupable, ObjectFloat obj"),
	IS_ON_FIRE("ObjectFloat obj", "bool"),
	IS_FIRE_NEAR("Coord position, float radius", "bool"),
	STOP_SCRIPTS_IN_FILES("StrPtr sourceFilename"),
	SET_POISONED("bool poisoned, ObjectFloat obj"),
	SET_TEMPERATURE("ObjectFloat obj, float temperature"),
	SET_ON_FIRE("bool enable, ObjectFloat object, float burnSpeed"),
	SET_TARGET("ObjectFloat obj, Coord position, float time"),
	WALK_PATH("ObjectFloat object, bool forward, int camera_enum, float valFrom, float valTo"),
	FOCUS_AND_POSITION_FOLLOW(2),
	GET_WALK_PATH_PERCENTAGE(1, 1),
	CAMERA_PROPERTIES("float distance, float speed, float angle, bool enableBehind"),
	ENABLE_DISABLE_MUSIC(2),			//Never found
	GET_MUSIC_OBJ_DISTANCE(1, 1),
	GET_MUSIC_ENUM_DISTANCE(1, 1),
	SET_MUSIC_PLAY_POSITION(4),			//Never found
	ATTACH_OBJECT_LEASH_TO_OBJECT(2),	//Never found
	ATTACH_OBJECT_LEASH_TO_HAND(1),		//Never found; guess (ObjectFloat creature)
	DETACH_OBJECT_LEASH("ObjectFloat creature"),
	SET_CREATURE_ONLY_DESIRE("ObjectFloat creature, ENUM_CREATURE_DESIRES desire, float value"),	//value must be 86400 (timeout?)
	SET_CREATURE_ONLY_DESIRE_OFF("ObjectFloat creature"),
	RESTART_MUSIC(1),					//Never found
	MUSIC_PLAYED1(1, 1),
	IS_OF_TYPE(3, 1),
	CLEAR_HIT_OBJECT(),
	GAME_THING_HIT(1, 1),
	SPELL_AT_THING(8, 1),
	SPELL_AT_POS(10, 1),
	CALL_PLAYER_CREATURE(1, 1),
	GET_SLOWEST_SPEED(1, 1),
	GET_OBJECT_HELD1(0, 1),
	HELP_SYSTEM_ON("", "bool"),			//Never found
	SHAKE_CAMERA("Coord position, float radius, float amplitude, float duration"),
	SET_ANIMATION_MODIFY("bool enable, ObjectFloat creature"),
	SET_AVI_SEQUENCE("bool enable, int aviSequence"),
	PLAY_GESTURE(5),
	DEV_FUNCTION("int func"),
	HAS_MOUSE_WHEEL("", "bool"),
	NUM_MOUSE_BUTTONS(0, 1),
	SET_CREATURE_DEV_STAGE("ObjectFloat creature, ENUM_DEVELOPMENT_PHASE stage"),
	SET_FIXED_CAM_ROTATION(4),			//Never found
	SWAP_CREATURE("ObjectFloat fromCreature, ObjectFloat toCreature"),
	GET_ARENA(5, 1),
	GET_FOOTBALL_PITCH(1, 1),
	STOP_ALL_GAMES(1),					//Never found; guess (ObjectFloat object)
	ATTACH_TO_GAME(3),					//Never found
	DETACH_FROM_GAME(3),				//Never found
	DETACH_UNDEFINED_FROM_GAME(2),		//Never found
	SET_ONLY_FOR_SCRIPTS(2),			//Never found
	START_MATCH_WITH_REFEREE(2),		//Never found
	GAME_TEAM_SIZE(2),
	GAME_TYPE(1, 1),
	GAME_SUB_TYPE(1, 1),
	IS_LEASHED(1, 1),
	SET_CREATURE_HOME("ObjectFloat creature, Coord position"),
	GET_HIT_OBJECT(0, 1),
	GET_OBJECT_WHICH_HIT(0, 1),
	GET_NEAREST_TOWN_OF_PLAYER(5, 1),
	SPELL_AT_POINT(5, 1),
	SET_ATTACK_OWN_TOWN(2),				//Never found
	IS_FIGHTING(1, 1),
	SET_MAGIC_RADIUS("ObjectFloat object, float radius"),
	TEMP_TEXT_WITH_NUMBER(4),			//Never found
	RUN_TEXT_WITH_NUMBER("bool alwaysFalse, int string, float number, int alwaysZero"),
	CREATURE_SPELL_REVERSION(2),		//Never found 
	GET_DESIRE(2, 1),
	GET_EVENTS_PER_SECOND(1, 1),
	GET_TIME_SINCE(1, 1),
	GET_TOTAL_EVENTS(1, 1),
	UPDATE_SNAPSHOT("float success, float alignment, ENUM_HELP_TEXT titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"), //The challengeID is set at compile time by the previous "challenge" statement
	CREATE_REWARD(5, 1),
	CREATE_REWARD_IN_TOWN(6, 1),
	SET_FADE("float red, float green, float blue, float time"),
	SET_FADE_IN(1),
	FADE_FINISHED(0, 1),
	SET_PLAYER_MAGIC(3),
	HAS_PLAYER_MAGIC(2, 1),
	SPIRIT_SPEAKS(2, 1),
	BELIEF_FOR_PLAYER(2, 1),
	GET_HELP(1, 1),
	SET_LEASH_WORKS("bool enable, ObjectFloat creature"),
	LOAD_MY_CREATURE("Coord position"),
	OBJECT_RELATIVE_BELIEF("ObjectFloat object, float player, float belief"),
	CREATE_WITH_ANGLE_AND_SCALE(7, 1),
	SET_HELP_SYSTEM("bool enable"),	//Never found
	SET_VIRTUAL_INFLUENCE("bool enable, float player"),
	SET_ACTIVE("bool active, ObjectFloat object"),
	THING_VALID("Object object", "bool"),
	VORTEX_FADE_OUT("ObjectFloat vortex"),
	REMOVE_REACTION_OF_TYPE("ObjectFloat object, ENUM_REACTION reaction"),
	CREATURE_LEARN_EVERYTHING_EXCLUDING(2),
	PLAYED_PERCENTAGE(1, 1),
	OBJECT_CAST_BY_OBJECT(2, 1),
	IS_WIND_MAGIC_AT_POS(1, 1),
	CREATE_MIST(9, 1),
	SET_MIST_FADE(6),
	GET_OBJECT_FADE(1, 1),
	PLAY_HAND_DEMO("StrPtr string, bool withPause, bool withoutHandModify"),
	IS_PLAYING_HAND_DEMO("", "bool"),
	GET_ARSE_POSITION(1, 3),
	IS_LEASHED_TO_OBJECT(2, 1),
	GET_INTERACTION_MAGNITUDE(1, 1),
	IS_CREATURE_AVAILABLE(1, 1),
	CREATE_HIGHLIGHT(5, 1),
	GET_OBJECT_HELD2(1, 1),
	GET_ACTION_COUNT(2, 1),
	GET_OBJECT_LEASH_TYPE(1, 1),
	SET_FOCUS_FOLLOW("ObjectFloat target"),
	SET_POSITION_FOLLOW("ObjectFloat target"),
	SET_FOCUS_AND_POSITION_FOLLOW("ObjectFloat target, float distance"),
	SET_CAMERA_LENS(1),
	MOVE_CAMERA_LENS("float lens, float time"),
	CREATURE_REACTION(2),				//Never found
	CREATURE_IN_DEV_SCRIPT("bool enable, ObjectFloat creature"),
	STORE_CAMERA_DETAILS(),				//Never found
	RESTORE_CAMERA_DETAILS(),			//Never found
	START_ANGLE_SOUND1("bool enable"),
	SET_CAMERA_POS_FOC_LENS(7),			//Never found
	MOVE_CAMERA_POS_FOC_LENS(8),		//Never found
	GAME_TIME_ON_OFF("bool enable"),
	MOVE_GAME_TIME("float hourOfTheDay, float duration"),
	SET_HIGH_GRAPHICS_DETAIL("bool enable, ObjectFloat object"),
	SET_SKELETON("bool enable, ObjectFloat object"),
	IS_SKELETON(1, 1),
	PLAYER_SPELL_CAST_TIME(1, 1),
	PLAYER_SPELL_LAST_CAST(1, 1),
	GET_LAST_SPELL_CAST_POS(1, 3),
	ADD_SPOT_VISUAL_TARGET_POS("ObjectFloat object, Coord position"),
	ADD_SPOT_VISUAL_TARGET_OBJECT("ObjectFloat object, ObjectFloat target"),
	SET_INDESTRUCTABLE("bool indestructible, ObjectFloat object"),
	SET_GRAPHICS_CLIPPING(2),			//Never found
	SPIRIT_APPEAR("ENUM_HELP_SPIRIT_TYPE spirit"),
	SPIRIT_DISAPPEAR("ENUM_HELP_SPIRIT_TYPE spirit"),
	SET_FOCUS_ON_OBJECT("ObjectFloat object, ObjectFloat target"),
	RELEASE_OBJECT_FOCUS(1),
	IMMERSION_EXISTS("", "bool"),
	SET_DRAW_LEASH("bool enable"),
	SET_DRAW_HIGHLIGHT("bool enable"),
	SET_OPEN_CLOSE("bool open, ObjectFloat object"),
	SET_INTRO_BUILDING("bool enable"),	//Never found
	CREATURE_FORCE_FRIENDS("bool enable, ObjectFloat creature, ObjectFloat targetCreature"),
	MOVE_COMPUTER_PLAYER_POSITION("float player, Coord position, float speed, bool withFixedHeight"),
	ENABLE_DISABLE_COMPUTER_PLAYER1("bool enable, float player"),
	GET_COMPUTER_PLAYER_POSITION(1, 3),
	SET_COMPUTER_PLAYER_POSITION("float player, Coord position, bool alwaysFalse"),
	GET_STORED_CAMERA_POSITION(0, 3),
	GET_STORED_CAMERA_FOCUS(0, 3),
	CALL_NEAR_IN_STATE(8, 1),
	SET_CREATURE_SOUND("bool enable"),
	CREATURE_INTERACTING_WITH(2, 1),
	SET_SUN_DRAW(1),
	OBJECT_INFO_BITS(1, 1),
	SET_HURT_BY_FIRE("bool enable, ObjectFloat object"),
	CONFINED_OBJECT(5),					//Never found
	CLEAR_CONFINED_OBJECT(1),
	GET_OBJECT_FLOCK(1, 1),
	SET_PLAYER_BELIEF("ObjectFloat object, float player, float belief"),
	PLAY_JC_SPECIAL("int feature"),
	IS_PLAYING_JC_SPECIAL(1, 1),
	VORTEX_PARAMETERS(8),
	LOAD_CREATURE("ENUM_CREATURE_TYPE type, StrPtr mindFilename, float player, Coord position"),
	IS_SPELL_CHARGING(1, 1),
	IS_THAT_SPELL_CHARGING(2, 1),
	OPPOSING_CREATURE(1, 1),
	FLOCK_WITHIN_LIMITS(1, 1),
	HIGHLIGHT_PROPERTIES("ObjectFloat object, int text, int category"),
	LAST_MUSIC_LINE(1, 1),
	HAND_DEMO_TRIGGER(0, 1),
	GET_BELLY_POSITION(1, 3),
	SET_CREATURE_CREED_PROPERTIES("ObjectFloat creature, ENUM_HAND_GLOW handGlow, float scale, float power, float time"),
	GAME_THING_CAN_VIEW_CAMERA(2, 1),
	GAME_PLAY_SAY_SOUND_EFFECT(6),
	SET_TOWN_DESIRE_BOOST("ObjectFloat object, ENUM_TOWN_DESIRE_INFO desire, float boost"),
	IS_LOCKED_INTERACTION(1, 1),
	SET_CREATURE_NAME("ObjectFloat creature, ENUM_HELP_TEXT textID"),
	COMPUTER_PLAYER_READY(1, 1),
	ENABLE_DISABLE_COMPUTER_PLAYER2("bool pause, float player"),
	CLEAR_ACTOR_MIND(1),
	ENTER_EXIT_CITADEL(1),				//Never found; guess (bool)
	START_ANGLE_SOUND2("bool enable"),
	THING_JC_SPECIAL(3),
	MUSIC_PLAYED2(1, 1),
	UPDATE_SNAPSHOT_PICTURE("Coord position, Coord focus, float success, float alignment, ENUM_HELP_TEXT titleStrID, bool takingPicture, int challengeID"),
	STOP_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename, StrPtr scriptName"),
	CREATE_RANDOM_VILLAGER_OF_TRIBE(4, 1),
	TOGGLE_LEASH("int player"),
	GAME_SET_MANA("ObjectFloat object, float mana"),
	SET_MAGIC_PROPERTIES("ObjectFloat object, ENUM_MAGIC_TYPE magicType, float duration"),
	SET_GAME_SOUND("bool enable"),
	SEX_IS_MALE(1, 1),
	GET_FIRST_HELP(1, 1),
	GET_LAST_HELP(1, 1),
	IS_ACTIVE(1, 1),
	SET_BOOKMARK_POSITION(4),			//Never found
	SET_SCAFFOLD_PROPERTIES("ObjectFloat object, ENUM_ABODE_NUMBER type, float size, bool destroy"),
	SET_COMPUTER_PLAYER_PERSONALITY("float player, StrPtr aspect, float probability"),
	SET_COMPUTER_PLAYER_SUPPRESSION(3),	//Never found; guess (float player, StrPtr aspect, float probability)
	FORCE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, ObjectFloat obj1, ObjectFloat obj2"),
	QUEUE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, ObjectFloat obj1, ObjectFloat obj2"),
	GET_TOWN_WITH_ID(1, 1),
	SET_DISCIPLE("ObjectFloat object, ENUM_VILLAGER_DISCIPLE discipleType, bool withSound"),
	RELEASE_COMPUTER_PLAYER("float player"),
	SET_COMPUTER_PLAYER_SPEED("float player, float speed"),
	SET_FOCUS_FOLLOW_COMPUTER_PLAYER("float player"),	//Never found
	SET_POSITION_FOLLOW_COMPUTER_PLAYER("float player"),	//Never found
	CALL_COMPUTER_PLAYER(1, 1),
	CALL_BUILDING_IN_TOWN(4, 1),
	SET_CAN_BUILD_WORSHIPSITE("bool enable, ObjectFloat object"),
	GET_FACING_CAMERA_POSITION(1, 3),
	SET_COMPUTER_PLAYER_ATTITUDE("float player1, float player2, float attitude"),	//Attitude in range [-1, 1]; -1=nice, 1=reactive
	GET_COMPUTER_PLAYER_ATTITUDE(2, 1),
	LOAD_COMPUTER_PLAYER_PERSONALITY(2),	//Never found; guess (float player, StrPtr filename)
	SAVE_COMPUTER_PLAYER_PERSONALITY(2),	//Never found; guess (float player, StrPtr filename)
	SET_PLAYER_ALLY("float player1, float player2, float percentage"),
	CALL_FLYING(6, 1),
	SET_OBJECT_FADE_IN("ObjectFloat object, float time"),
	IS_AFFECTED_BY_SPELL(2, 1),
	SET_MAGIC_IN_OBJECT("bool enable, int spell, ObjectFloat object"),
	ID_ADULT_SIZE(1, 1),
	OBJECT_CAPACITY(1, 1),
	OBJECT_ADULT_CAPACITY(1, 1),
	SET_CREATURE_AUTO_FIGHTING("bool enable, ObjectFloat creature"),
	IS_AUTO_FIGHTING(1, 1),
	SET_CREATURE_QUEUE_FIGHT_MOVE("ObjectFloat creature, ENUM_FIGHT_MOVES move"),
	SET_CREATURE_QUEUE_FIGHT_SPELL(2),	//Never found; guess (ObjectFloat creature, ENUM_FIGHT_SPELLS spell)
	SET_CREATURE_QUEUE_FIGHT_STEP(2),	//Never found; guess (ObjectFloat creature, ENUM_FIGHT_STEPS step)
	GET_CREATURE_FIGHT_ACTION(1, 1),
	CREATURE_FIGHT_QUEUE_HITS(1, 1),
	SQUARE_ROOT(1, 1),
	GET_PLAYER_ALLY(2, 1),
	SET_PLAYER_WIND_RESISTANCE(2, 1),
	GET_PLAYER_WIND_RESISTANCE(2, 1),
	PAUSE_UNPAUSE_CLIMATE_SYSTEM("bool enable"),
	PAUSE_UNPAUSE_STORM_CREATION_IN_CLIMATE_SYSTEM("bool enable"),	//Never found
	GET_MANA_FOR_SPELL(1, 1),
	KILL_STORMS_IN_AREA("Coord position, float radius"),
	INSIDE_TEMPLE("", "bool"),
	RESTART_OBJECT("ObjectFloat object"),	//Never found
	SET_GAME_TIME_PROPERTIES(3),		//Never found
	RESET_GAME_TIME_PROPERTIES(),
	SOUND_EXISTS("", "bool"),			//Never found
	GET_TOWN_WORSHIP_DEATHS(1, 1),
	GAME_CLEAR_DIALOGUE(),
	GAME_CLOSE_DIALOGUE(),
	GET_HAND_STATE(0, 1),
	SET_INTERFACE_CITADEL("bool enable"),
	MAP_SCRIPT_FUNCTION("StrPtr command"),
	WITHIN_ROTATION(0, 1),
	GET_PLAYER_TOWN_TOTAL(1, 1),
	SPIRIT_SCREEN_POINT(3),
	KEY_DOWN(1, 1),
	SET_FIGHT_EXIT(1),					//Never found; guess (bool enable)
	GET_OBJECT_CLICKED(0, 1),
	GET_MANA(1, 1),
	CLEAR_PLAYER_SPELL_CHARGING("float player"),
	STOP_SOUND_EFFECT(3),
	GET_TOTEM_STATUE(1, 1),
	SET_SET_ON_FIRE("bool enable, ObjectFloat object"),
	SET_LAND_BALANCE(2),				//Never found
	SET_OBJECT_BELIEF_SCALE(2),			//Never found
	START_IMMERSION("ENUM_IMMERSION_EFFECT_TYPE effect"),
	STOP_IMMERSION("ENUM_IMMERSION_EFFECT_TYPE effect"),	//Never found
	STOP_ALL_IMMERSION(),				//Never found
	SET_CREATURE_IN_TEMPLE("bool enable"),
	GAME_DRAW_TEXT("ENUM_HELP_TEXT textID, float across, float down, float width, float height, float size, float fade"),
	GAME_DRAW_TEMP_TEXT("StrPtr string, float across, float down, float width, float height, float size, float fade"),
	FADE_ALL_DRAW_TEXT("float time"),
	SET_DRAW_TEXT_COLOUR("float red, float green, float blue"),
	SET_CLIPPING_WINDOW("float across, float down, float width, float height, float time"),
	CLEAR_CLIPPING_WINDOW("float time"),
	SAVE_GAME_IN_SLOT("int slot"),		//Never found
	SET_OBJECT_CARRYING("ObjectFloat object, ENUM_CARRIED_OBJECT carriedObj"),
	POS_VALID_FOR_CREATURE(3, 1),
	GET_TIME_SINCE_OBJECT_ATTACKED(2, 1),
	GET_TOWN_AND_VILLAGER_HEALTH_TOTAL(1, 1),
	GAME_ADD_FOR_BUILDING(2),			//Never found
	ENABLE_DISABLE_ALIGNMENT_MUSIC("bool enable"),
	GET_DEAD_LIVING(4, 1),
	ATTACH_SOUND_TAG(4),
	DETACH_SOUND_TAG(3),
	GET_SACRIFICE_TOTAL(1, 1),
	GAME_SOUND_PLAYING(2, 1),
	GET_TEMPLE_POSITION(1, 3),
	CREATURE_AUTOSCALE(3),
	GET_SPELL_ICON_IN_TEMPLE(2, 1),
	GAME_CLEAR_COMPUTER_PLAYER_ACTIONS(1),
	GET_FIRST_IN_CONTAINER(1, 1),
	GET_NEXT_IN_CONTAINER(2, 1),
	GET_TEMPLE_ENTRANCE_POSITION(3, 3),
	SAY_SOUND_EFFECT_PLAYING(2, 1),
	SET_HAND_DEMO_KEYS(1),
	CAN_SKIP_TUTORIAL("", "bool"),
	CAN_SKIP_CREATURE_TRAINING("", "bool"),
	IS_KEEPING_OLD_CREATURE("", "bool"),
	CURRENT_PROFILE_HAS_CREATURE("", "bool");
	
	/**If varargs is false, this is the exact number of values popped from the stack.
	 * If varargs is true, this is the minimum number of values popped from the stack.
	 */
	public final int pop;
	/**The number of values pushed on the stack on return.
	 * Ususally 0, 1 or 3 (for Coord)
	 */
	public final int push;
	/**The high-level argument types and names*/
	public final Argument[] args;
	/**The high-level return types*/
	public final ArgType[] returnTypes;
	/**Tells whether the number of values popped from the stack is variable or not.*/
	public final boolean varargs;
	/**Tells whether the function must be called with SYS2 or not.*/
	public final boolean sys2;
	
	NativeFunction() {
		this(0, 0);
	}
	
	NativeFunction(int pop) {
		this(pop, 0);
	}
	
	NativeFunction(String sArgs) {
		this(sArgs, null);
	}
	
	NativeFunction(String sArgs, String sRet) {
		this(sArgs, sRet, false);
	}
	
	NativeFunction(String sArgs, boolean sys2) {
		this(sArgs, null, false);
	}
	
	NativeFunction(String sArgs, String sRet, boolean sys2) {
		boolean varargs = false;
		if (sArgs == null || sArgs.isEmpty()) {
			this.pop = 0;
			this.args = new Argument[0];
		} else {
			String[] sArgArray = sArgs.split("\\s*,\\s*", -1);
			this.args = new Argument[sArgArray.length];
			int n = 0;
			for (int i = 0; i < args.length; i++) {
				Argument arg = Argument.parse(sArgArray[i]);
				args[i] = arg;
				if (arg.varargs) {
					varargs = true;
				} else {
					n += arg.type == ArgType.COORD ? 3 : 1;
				}
			}
			this.pop = n;
		}
		this.varargs = varargs;
		//
		if (sRet == null || sRet.isEmpty()) {
			this.push = 0;
			this.returnTypes = new ArgType[0];
		} else {
			String[] sRetArray = sRet.split("\\s*,\\s*", -1);
			this.returnTypes = new ArgType[sRetArray.length];
			int n = 0;
			for (int i = 0; i < returnTypes.length; i++) {
				ArgType type = ArgType.fromKeyword(sRetArray[i]);
				returnTypes[i] = type;
				n += type == ArgType.COORD ? 3 : 1;
			}
			this.push = n;
		}
		//
		this.sys2 = sys2;
	}
	
	NativeFunction(int pop, int push) {
		this.pop = pop;
		this.push = push;
		this.args = new Argument[pop];
		for (int i = 0; i < pop; i++) {
			args[i] = new Argument(ArgType.UNKNOWN, null, false);
		}
		this.varargs = false;
		this.returnTypes = new ArgType[push];
		for (int i = 0; i < push; i++) {
			returnTypes[i] = ArgType.UNKNOWN;
		}
		this.sys2 = false;
	}
	
	public String getArgsString() {
		StringBuilder b = new StringBuilder();
		if (args.length > 0) {
			b.append(args[0].toString());
			for (int i = 1; i < args.length; i++) {
				b.append(", ");
				b.append(args[i].toString());
			}
		}
		return b.toString();
	}
	
	public String getReturnTypesString() {
		StringBuilder b = new StringBuilder();
		if (returnTypes.length > 0) {
			b.append(returnTypes[0].toString());
			for (int i = 1; i < returnTypes.length; i++) {
				b.append(", ");
				b.append(returnTypes[i].toString());
			}
		}
		return b.toString();
	}
	
	public String getInfoString() {
		String s = "[" + pop;
		if (varargs) s += "+";
		s += ", " + push + "] (" + getArgsString() + ")";
		if (returnTypes.length > 0) {
			s += " returns (" + getReturnTypesString() + ")";
		}
		return s;
	}
	
	public static NativeFunction fromCode(int code) throws InvalidNativeFunctionException {
		NativeFunction[] functions = values();
		if (code < 0 || code >= functions.length) {
			throw new InvalidNativeFunctionException(code);
		}
		return functions[code];
	}
	
	
	public static class Argument {
		public final ArgType type;
		/**The guessed name of the argument. May be null if we don't know what this argument means.
		 */
		public final String name;
		/**Tells if this argument may occurr a variable number of times
		 */
		public final boolean varargs;
		
		Argument(ArgType type, String name, boolean varargs) {
			this.type = type;
			this.name = name;
			this.varargs = varargs;
		}
		
		static Argument parse(String expr) {
			if (expr.contains("...")) {
				String[] tks = expr.split("\\.\\.\\.");
				ArgType type = ArgType.fromKeyword(tks[0].trim());
				String name = tks.length >= 2 ? tks[1].trim() : null;
				return new Argument(type, name, true);
			} else {
				String[] tks = expr.split("\\s+");
				ArgType type = ArgType.fromKeyword(tks[0].trim());
				String name = tks.length >= 2 ? tks[1].trim() : null;
				return new Argument(type, name, false);
			}
		}
		
		@Override
		public String toString() {
			String s = type.toString();
			if (varargs) s += "...";
			if (name != null) {
				s += " " + name;
			}
			return s;
		}
	}
	
	
	public enum ArgType {
		UNKNOWN("?"),
		INT("int"),
		FLOAT("float"),
		COORD("Coord"),					//3 floats
		BOOL("bool"),
		OBJECT("Object"),
		OBJECT_FLOAT("ObjectFloat"),
		OBJECT_INT("ObjectInt"),
		INT_OR_FLOAT("int|float"),
		STRPTR("StrPtr"),				//int (byte offset in data section)
		PROPERTY("Property"),			//int, TODO must be extracted from the code...
		ENUM_SCRIPT_OBJECT_TYPES(),		//see enums.txt
		SUBTYPE("Subtype"),				//various enums defined in info2.txt
		ENUM_SCRIPT_BOOL(),				//see enums.txt
		ENUM_SCRIPT_INTERFACE_LEVELS(),	//see enums.txt
		ENUM_HELP_SPIRIT_TYPE(),		//defined in info2.txt
		ENUM_VILLAGER_DISCIPLE(),		//defined in info2.txt
		ENUM_HELP_TEXT(),				//defined in InfoScript1.txt
		ENUM_CREATURE_ACTION(),			//defined in InfoCreature1.txt
		ENUM_CREATURE_DESIRES(),		//defined in info2.txt
		ENUM_DEVELOPMENT_PHASE(),		//defined in InfoCreature1.txt
		ENUM_CREATURE_ACTION_TYPES(),	//see enum.txt
		ENUM_MAGIC_TYPE(),				//defined in info2.txt
		ENUM_HAND_GLOW(),				//see enum.txt
		ENUM_FIGHT_MOVES(),				//see enum.txt
		ENUM_CREATURE_TYPE(),			//defined in InfoCreature1.txt
		ENUM_TOWN_DESIRE_INFO(),		//defined in info2.txt
		ENUM_IMMERSION_EFFECT_TYPE(),	//defined in info2.txt
		ENUM_CARRIED_OBJECT(),			//defined in info2.txt
		ENUM_REACTION(),				//defined in info2.txt
		ENUM_ABODE_NUMBER(),			//defined in info2.txt
		DETAIL_ANIM_TYPES();			//defined in info1.txt
		
		private static final Map<String, ArgType> map = new HashMap<>();
		
		static {
			for (ArgType t : values()) {
				map.put(t.keyword, t);
			}
		}
		
		public final String keyword;
		
		private ArgType() {
			this.keyword = this.name();
		}
		
		private ArgType(String keyword) {
			this.keyword = keyword;
		}
		
		@Override
		public String toString() {
			return keyword;
		}
		
		public static ArgType fromKeyword(String keyword) {
			ArgType t = map.get(keyword);
			if (t == null) throw new IllegalArgumentException("Invalid ArgType: "+keyword);
			return t;
		}
	}
}

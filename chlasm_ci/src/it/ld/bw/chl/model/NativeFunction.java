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

/* Script variables are of type float, so proper conversion must be done when required. Almost all
 * functions treats objects as float values, with few exceptions.
 * 
 * When a function returns a value, if the return value is going to be put into a variable, then
 * POPF must be used (preceded by CASTF if the function returns anything other than an int); if the
 * return value has to be thrown away, then POPO/POPF is used.
 * When a function expects an ObjectObj, then PUSHO is used.
 * When a function expects an Object, then PUSHF is used, unless you want to pass a null object,
 * in which case you should use "PUSHO 0" (see FLOCK_DETACH).
 * When an ObjectInt is passed to a function, then PUSHI is used.
 * 
 * About order of parameters
 * For the sake of clarity, and to be consistent with the CALL/START instructions, parameters in the
 * signature of native functions are reported in the same order they are pushed to the stack, but for
 * various reasons that I'm not going to explain here, this is certainly the opposite.
 * For example, the function MOVE_CAMERA_FOCUS(Coord position, float time)
 * which by the truth is MOVE_CAMERA_FOCUS(float x, float y, float z, float time)
 * is certainly defined in C as MOVE_CAMERA_FOCUS(float time, float z, float y, float x).
 * 
 * Some functions are overloaded, so I have appended a numeric suffix to distinguish them.
 * I have also renamed some function to a name that better explains what the function actually does.
 * */

//TODO complete functions description

public enum NativeFunction {
	/*  0*/ NONE(),
	/*  1*/ SET_CAMERA_POSITION("Coord position"),
	/*  2*/ SET_CAMERA_FOCUS("Coord position"),
	/*  3*/ MOVE_CAMERA_POSITION("Coord position, float time"),
	/*  4*/ MOVE_CAMERA_FOCUS("Coord position, float time"),
	/*  5*/ GET_CAMERA_POSITION("", "Coord"),
	/*  6*/ GET_CAMERA_FOCUS("", "Coord"),
	/*  7*/ SPIRIT_EJECT("HELP_SPIRIT_TYPE spirit"),
	/*  8*/ SPIRIT_HOME("HELP_SPIRIT_TYPE spirit"),
	/*  9*/ SPIRIT_POINT_POS("HELP_SPIRIT_TYPE spirit, Coord position, bool inWorld"),
	/* 10*/ SPIRIT_POINT_GAME_THING("HELP_SPIRIT_TYPE spirit, Object target, bool inWorld"),
	/* 11*/ GAME_THING_FIELD_OF_VIEW("Object object", "bool"),
	/* 12*/ POS_FIELD_OF_VIEW("Coord position", "bool"),
	/* 13*/ RUN_TEXT(4, 0),
	/* 14*/ TEMP_TEXT(4, 0),
	/* 15*/ TEXT_READ("", "bool"),
	/* 16*/ GAME_THING_CLICKED("Object object", "bool"),
	/* 17*/ SET_SCRIPT_STATE("Object object, VILLAGER_STATES state"),
	/* 18*/ GET_PROPERTY("SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object", "int|float"),
	/* 19*/ SET_PROPERTY("SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val", true),
	/* 20*/ GET_POSITION("Object object", "Coord"),
	/* 21*/ SET_POSITION("Object object, Coord position"),
	/* 22*/ GET_DISTANCE("Coord p0, Coord p1", "float"),
	/* 23*/ CALL("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position, bool excludingScripted", "float"),
	/* 24*/ CREATE("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position", "Object"),
	/* 25*/ RANDOM("float min, float max", "float"),
	/* 26*/ DLL_GETTIME("", "float"),
	/* 27*/ START_CAMERA_CONTROL("", "bool"),
	/* 28*/ END_CAMERA_CONTROL(),
	/* 29*/ SET_WIDESCREEN("bool enabled"),
	/* 30*/ MOVE_GAME_THING("Object object, Coord position, float radius"),
	/* 31*/ SET_FOCUS("Object object, Coord position"),
	/* 32*/ HAS_CAMERA_ARRIVED("", "bool"),
	/* 33*/ FLOCK_CREATE("Coord position", "Object"),
	/* 34*/ FLOCK_ATTACH("Object obj, Object flock, bool asLeader", "Object"),
	/* 35*/ FLOCK_DETACH("Object obj, Object flock", "Object"),
	/* 36*/ FLOCK_DISBAND("Object flock"),
	/* 37*/ ID_SIZE("Object container", "float"),
	/* 38*/ FLOCK_MEMBER("Object obj, Object flock", "bool"),
	/* 39*/ GET_HAND_POSITION("", "Coord"),
	/* 40*/ PLAY_SOUND_EFFECT("int sound, AUDIO_SFX_BANK_TYPE soundbank, Coord position, bool withPosition"),
	/* 41*/ START_MUSIC(2, 0),
	/* 42*/ STOP_MUSIC(),
	/* 43*/ ATTACH_MUSIC("int music, Object target"),
	/* 44*/ DETACH_MUSIC("Object object"),
	/* 45*/ OBJECT_DELETE("Object obj, int withFade"),
	/* 46*/ FOCUS_FOLLOW("Object target"),
	/* 47*/ POSITION_FOLLOW("Object target"),
	/* 48*/ CALL_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position, float radius, bool excludingScripted", "Object"),
	/* 49*/ SPECIAL_EFFECT_POSITION("int effect, Coord position, float duration", "Object"),
	/* 50*/ SPECIAL_EFFECT_OBJECT("int effect, Object target, float duration", "Object"),
	/* 51*/ DANCE_CREATE("Object obj, DANCE_INFO type, Coord position, float duration", "Object"),
	/* 52*/ CALL_IN("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Object container, bool excludingScripted", "Object"),
	/* 53*/ CHANGE_INNER_OUTER_PROPERTIES("Object obj, float inner, float outer, float calm"),
	/* 54*/ SNAPSHOT("bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"),
	/* 55*/ GET_ALIGNMENT("int zero", "float", true),
	/* 56*/ SET_ALIGNMENT(2),
	/* 57*/ INFLUENCE_OBJECT("Object target, float radius, int zero, int anti", "Object"),
	/* 58*/ INFLUENCE_POSITION("Coord position, float radius, int zero, int anti", "Object"),
	/* 59*/ GET_INFLUENCE("float player, bool raw, Coord position", "float"),
	/* 60*/ SET_INTERFACE_INTERACTION("SCRIPT_INTERFACE_LEVEL level", true),
	/* 61*/ PLAYED("Object obj", "bool"),
	/* 62*/ RANDOM_ULONG("int min, int max", "int", true),
	/* 63*/ SET_GAMESPEED("float speed", true),
	/* 64*/ CALL_IN_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Object container, Coord pos, float radius, bool excludingScripted", "Object"),
	/* 65*/ OVERRIDE_STATE_ANIMATION("Object obj, DETAIL_ANIM_TYPES animType"),
	/* 66*/ CREATURE_CREATE_RELATIVE_TO_CREATURE(7, 1),
	/* 67*/ CREATURE_LEARN_EVERYTHING("Object creature"),
	/* 68*/ CREATURE_SET_KNOWS_ACTION(5, 0),
	/* 69*/ CREATURE_SET_AGENDA_PRIORITY("Object creature, float priority"),
	/* 70*/ CREATURE_TURN_OFF_ALL_DESIRES(1),
	/* 71*/ CREATURE_LEARN_DISTINCTION_ABOUT_ACTIVITY_OBJECT(4),
	/* 72*/ CREATURE_DO_ACTION("Object creature, CREATURE_ACTION, Object target, Object withObject"),
	/* 73*/ IN_CREATURE_HAND("Object obj, Object creature", "bool"),
	/* 74*/ CREATURE_SET_DESIRE_VALUE("Object creature, CREATURE_DESIRES desire, float value"),
	/* 75*/ CREATURE_SET_DESIRE_ACTIVATED3("Object creature, CREATURE_DESIRES desire, SCRIPT_BOOL active"),
	/* 76*/ CREATURE_SET_DESIRE_ACTIVATED(2, 0),
	/* 77*/ CREATURE_SET_DESIRE_MAXIMUM("Object creature, CREATURE_DESIRES desire, float value"),
	/* 78*/ CONVERT_CAMERA_POSITION("int", "Coord"),
	/* 79*/ CONVERT_CAMERA_FOCUS("int camera_enum", "Coord"),
	/* 80*/ CREATURE_SET_PLAYER(2, 0),
	/* 81*/ CREATURE_INITIALISE_NUM_TIMES_PERFORMED_ACTION(2),
	/* 82*/ CREATURE_GET_NUM_TIMES_ACTION_PERFORMED(2, "float"),
	/* 83*/ GET_OBJECT_DROPPED("Object creature", "Object"),
	/* 84*/ CLEAR_DROPPED_BY_OBJECT("Object creature"),
	/* 85*/ CREATE_REACTION("Object object, REACTION reaction"),
	/* 86*/ REMOVE_REACTION("Object object"),
	/* 87*/ GET_COUNTDOWN_TIMER("", "float"),
	/* 88*/ START_DUAL_CAMERA("Object obj1, Object obj2"),
	/* 89*/ UPDATE_DUAL_CAMERA("Object obj1, Object obj2"),
	/* 90*/ RELEASE_DUAL_CAMERA(),
	/* 91*/ SET_CREATURE_HELP(1),
	/* 92*/ GET_TARGET_OBJECT("Object obj", "Object"),
	/* 93*/ CREATURE_DESIRE_IS(2, 1),
	/* 94*/ COUNTDOWN_TIMER_EXISTS("", "bool"),
	/* 95*/ LOOK_GAME_THING("HELP_SPIRIT_TYPE spirit, Object target"),
	/* 96*/ GET_OBJECT_DESTINATION("Object obj", "Coord"),
	/* 97*/ CREATURE_FORCE_FINISH("Object creature"),
	/* 98*/ GET_ACTION_TEXT_FOR_OBJECT("Object obj", "int"),
	/* 99*/ CREATE_DUAL_CAMERA_WITH_POINT("Object obj, Coord position"),
	/*100*/ SET_CAMERA_TO_FACE_OBJECT(2),
	/*101*/ MOVE_CAMERA_TO_FACE_OBJECT("Object target, float distance, float time"),
	/*102*/ GET_MOON_PERCENTAGE("", "float"),
	/*103*/ POPULATE_CONTAINER("Object obj, float quantity, SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype"),
	/*104*/ ADD_REFERENCE(1, 1),
	/*105*/ REMOVE_REFERENCE(1, 1),
	/*106*/ SET_GAME_TIME("float time"),
	/*107*/ GET_GAME_TIME("", "float"),
	/*108*/ GET_REAL_TIME("", "float"),
	/*109*/ GET_REAL_DAY1("", "float"),
	/*110*/ GET_REAL_DAY2("", "float"),
	/*111*/ GET_REAL_MONTH("", "float"),
	/*112*/ GET_REAL_YEAR("", "float"),
	/*113*/ RUN_CAMERA_PATH("int cameraEnum"),
	/*114*/ START_DIALOGUE("", "bool"),
	/*115*/ END_DIALOGUE(),
	/*116*/ IS_DIALOGUE_READY("", "bool"),
	/*117*/ CHANGE_WEATHER_PROPERTIES("Object storm, float temperature, float rainfall, float snowfall, float overcast, float fallspeed"),
	/*118*/ CHANGE_LIGHTNING_PROPERTIES("Object storm, float sheetmin, float sheetmax, float forkmin, float forkmax"),
	/*119*/ CHANGE_TIME_FADE_PROPERTIES("Object storm, float duration, float fadeTime"),
	/*120*/ CHANGE_CLOUD_PROPERTIES("Object storm, float numClouds, float blackness, float elevation"),
	/*121*/ SET_HEADING_AND_SPEED(8, 0),
	/*122*/ START_GAME_SPEED(),
	/*123*/ END_GAME_SPEED(),
	/*124*/ BUILD_BUILDING("Coord position, float desire"),
	/*125*/ SET_AFFECTED_BY_WIND("bool enabled, Object object"),
	/*126*/ WIDESCREEN_TRANSISTION_FINISHED("", "bool"),
	/*127*/ GET_RESOURCE("RESOURCE_TYPE resource, Object container", "float"),
	/*128*/ ADD_RESOURCE("RESOURCE_TYPE resource, float quantity, Object container", "float"),
	/*129*/ REMOVE_RESOURCE("RESOURCE_TYPE resource, float quantity, Object container", "float"),
	/*130*/ GET_TARGET_RELATIVE_POS("Coord from, Coord to, float distance, float angle", "Coord"),
	/*131*/ STOP_POINTING("HELP_SPIRIT_TYPE spirit"),
	/*132*/ STOP_LOOKING("HELP_SPIRIT_TYPE spirit"),
	/*133*/ LOOK_AT_POSITION("HELP_SPIRIT_TYPE spirit, Coord position"),
	/*134*/ PLAY_SPIRIT_ANIM(5),
	/*135*/ CALL_IN_NOT_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Object container, Coord pos, float radius, bool excludingScripted", "Object"),
	/*136*/ SET_CAMERA_ZONE("StrPtr filename"),
	/*137*/ GET_OBJECT_STATE("Object obj", "int"),
	/*138*/ SET_TIMER_TIME("Object timer, float time"),
	/*139*/ CREATE_TIMER("float timeout", "Object"),
	/*140*/ GET_TIMER_TIME_REMAINING("Object timer", "float"),
	/*141*/ GET_TIMER_TIME_SINCE_SET("Object timer", "float"),
	/*142*/ MOVE_MUSIC(2),
	/*143*/ GET_INCLUSION_DISTANCE("", "float"),
	/*144*/ GET_LAND_HEIGHT("Coord position", "float"),
	/*145*/ LOAD_MAP("StrPtr path"),
	/*146*/ STOP_ALL_SCRIPTS_EXCLUDING("StrPtr scriptName"),
	/*147*/ STOP_ALL_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename"),
	/*148*/ STOP_SCRIPT("StrPtr scriptName"),
	/*149*/ CLEAR_CLICKED_OBJECT(),
	/*150*/ CLEAR_CLICKED_POSITION(),
	/*151*/ POSITION_CLICKED(4, "bool"),
	/*152*/ RELEASE_FROM_SCRIPT("Object obj"),
	/*153*/ GET_OBJECT_HAND_IS_OVER("", "Object"),
	/*154*/ ID_POISONED_SIZE("Object container", "float"),
	/*155*/ IS_POISONED("Object obj", "bool"),
	/*156*/ CALL_POISONED_IN("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Object container, bool excludingScripted", "Object"),
	/*157*/ CALL_NOT_POISONED_IN("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Object container, bool excludingScripted", "Object"),
	/*158*/ SPIRIT_PLAYED("HELP_SPIRIT_TYPE spirit", "bool"),
	/*159*/ CLING_SPIRIT("HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
	/*160*/ FLY_SPIRIT("HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
	/*161*/ SET_ID_MOVEABLE("bool moveable, Object obj"),
	/*162*/ SET_ID_PICKUPABLE("bool pickupable, Object obj"),
	/*163*/ IS_ON_FIRE("Object obj", "bool"),
	/*164*/ IS_FIRE_NEAR("Coord position, float radius", "bool"),
	/*165*/ STOP_SCRIPTS_IN_FILES("StrPtr sourceFilename"),
	/*166*/ SET_POISONED("bool poisoned, Object obj"),
	/*167*/ SET_TEMPERATURE("Object obj, float temperature"),
	/*168*/ SET_ON_FIRE("bool enable, Object object, float burnSpeed"),
	/*169*/ SET_TARGET("Object obj, Coord position, float time"),
	/*170*/ WALK_PATH("Object object, bool forward, int camera_enum, float valFrom, float valTo"),
	/*171*/ FOCUS_AND_POSITION_FOLLOW(2),
	/*172*/ GET_WALK_PATH_PERCENTAGE("Object object", "float"),
	/*173*/ CAMERA_PROPERTIES("float distance, float speed, float angle, bool enableBehind"),
	/*174*/ ENABLE_DISABLE_MUSIC(2),
	/*175*/ GET_MUSIC_OBJ_DISTANCE("Object source", "float"),
	/*176*/ GET_MUSIC_ENUM_DISTANCE("int type", "float"),
	/*177*/ SET_MUSIC_PLAY_POSITION(4),
	/*178*/ ATTACH_OBJECT_LEASH_TO_OBJECT(2),
	/*179*/ ATTACH_OBJECT_LEASH_TO_HAND(1),
	/*180*/ DETACH_OBJECT_LEASH("Object creature"),
	/*181*/ SET_CREATURE_ONLY_DESIRE("Object creature, CREATURE_DESIRES desire, float value"),
	/*182*/ SET_CREATURE_ONLY_DESIRE_OFF("Object creature"),
	/*183*/ RESTART_MUSIC(2, 0),
	/*184*/ MUSIC_PLAYED1(1, 1),
	/*185*/ IS_OF_TYPE("Object object, SCRIPT_OBJECT_TYPE type, int subtype", "bool"),
	/*186*/ CLEAR_HIT_OBJECT(),
	/*187*/ GAME_THING_HIT("Object object", "bool"),
	/*188*/ SPELL_AT_THING(9, 1),
	/*189*/ SPELL_AT_POS(11, 1),
	/*190*/ CALL_PLAYER_CREATURE("float player", "Object"),
	/*191*/ GET_SLOWEST_SPEED("Object flock", "float"),
	/*192*/ GET_OBJECT_HELD1("", "Object"),
	/*193*/ HELP_SYSTEM_ON("", "bool"),
	/*194*/ SHAKE_CAMERA("Coord position, float radius, float amplitude, float duration"),
	/*195*/ SET_ANIMATION_MODIFY("bool enable, Object creature"),
	/*196*/ SET_AVI_SEQUENCE("bool enable, int aviSequence"),
	/*197*/ PLAY_GESTURE(5),
	/*198*/ DEV_FUNCTION("int func"),
	/*199*/ HAS_MOUSE_WHEEL("", "bool"),
	/*200*/ NUM_MOUSE_BUTTONS("", "float"),
	/*201*/ SET_CREATURE_DEV_STAGE("Object creature, DEVELOPMENT_PHASE stage"),
	/*202*/ SET_FIXED_CAM_ROTATION(4),
	/*203*/ SWAP_CREATURE("Object fromCreature, Object toCreature"),
	/*204*/ GET_ARENA(5, "Object"),
	/*205*/ GET_FOOTBALL_PITCH("Object town", "Object"),
	/*206*/ STOP_ALL_GAMES(1),
	/*207*/ ATTACH_TO_GAME(3),
	/*208*/ DETACH_FROM_GAME(3),
	/*209*/ DETACH_UNDEFINED_FROM_GAME(2),
	/*210*/ SET_ONLY_FOR_SCRIPTS(2),
	/*211*/ START_MATCH_WITH_REFEREE(2),
	/*212*/ GAME_TEAM_SIZE(2),
	/*213*/ GAME_TYPE("Object object", "int"),
	/*214*/ GAME_SUB_TYPE("Object object", "int"),
	/*215*/ IS_LEASHED("Object object", "bool"),
	/*216*/ SET_CREATURE_HOME("Object creature, Coord position"),
	/*217*/ GET_HIT_OBJECT("", "Object"),
	/*218*/ GET_OBJECT_WHICH_HIT("", "Object"),
	/*219*/ GET_NEAREST_TOWN_OF_PLAYER(5, "Object"),
	/*220*/ SPELL_AT_POINT("MAGIC_TYPE spell, Coord position, float radius", "Object"),
	/*221*/ SET_ATTACK_OWN_TOWN(2),
	/*222*/ IS_FIGHTING("Object object", "bool"),
	/*223*/ SET_MAGIC_RADIUS("Object object, float radius"),
	/*224*/ TEMP_TEXT_WITH_NUMBER(5, 0),
	/*225*/ RUN_TEXT_WITH_NUMBER(5, 0),
	/*226*/ CREATURE_SPELL_REVERSION(2),
	/*227*/ GET_DESIRE(2, "float"),
	/*228*/ GET_EVENTS_PER_SECOND("HELP_EVENT_TYPE type", "float"),
	/*229*/ GET_TIME_SINCE("HELP_EVENT_TYPE type", "float"),
	/*230*/ GET_TOTAL_EVENTS("HELP_EVENT_TYPE type", "float"),
	/*231*/ UPDATE_SNAPSHOT("float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"),
	/*232*/ CREATE_REWARD("REWARD_OBJECT_INFO reward, Coord position, bool fromSky", "Object"),
	/*233*/ CREATE_REWARD_IN_TOWN("REWARD_OBJECT_INFO reward, Object town, Coord position, bool fromSky", "Object"),
	/*234*/ SET_FADE("float red, float green, float blue, float time"),
	/*235*/ SET_FADE_IN("float duration"),
	/*236*/ FADE_FINISHED("", "bool"),
	/*237*/ SET_PLAYER_MAGIC(3),
	/*238*/ HAS_PLAYER_MAGIC("MAGIC_TYPE spell, float player", "bool"),
	/*239*/ SPIRIT_SPEAKS("HELP_SPIRIT_TYPE spirit, int textID", "bool"),
	/*240*/ BELIEF_FOR_PLAYER("Object object, float player", "float"),
	/*241*/ GET_HELP("Object object", "float"),
	/*242*/ SET_LEASH_WORKS("bool enable, Object creature"),
	/*243*/ LOAD_MY_CREATURE("Coord position"),
	/*244*/ OBJECT_RELATIVE_BELIEF("Object object, float player, float belief"),
	/*245*/ CREATE_WITH_ANGLE_AND_SCALE("float angle, float scale, SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord pos", "Object"),
	/*246*/ SET_HELP_SYSTEM("bool enable"),
	/*247*/ SET_VIRTUAL_INFLUENCE("bool enable, float player"),
	/*248*/ SET_ACTIVE("bool active, Object object"),
	/*249*/ THING_VALID("ObjectObj object", "bool"),
	/*250*/ VORTEX_FADE_OUT("Object vortex"),
	/*251*/ REMOVE_REACTION_OF_TYPE("Object object, REACTION reaction"),
	/*252*/ CREATURE_LEARN_EVERYTHING_EXCLUDING(2),
	/*253*/ PLAYED_PERCENTAGE("Object object", "float"),
	/*254*/ OBJECT_CAST_BY_OBJECT("Object spellInstance, Object caster", "bool"),
	/*255*/ IS_WIND_MAGIC_AT_POS(1, "bool"),
	/*256*/ CREATE_MIST("Coord pos, float scale, float r, float g, float b, float transparency, float heightRatio", "Object"),
	/*257*/ SET_MIST_FADE("Object mist, float startScale, float endScale, float startTransparency, float endTransparency, float duration"),
	/*258*/ GET_OBJECT_FADE("Object object", "float"),
	/*259*/ PLAY_HAND_DEMO("StrPtr string, bool withPause, bool withoutHandModify"),
	/*260*/ IS_PLAYING_HAND_DEMO("", "bool"),
	/*261*/ GET_ARSE_POSITION("Object object", "Coord"),
	/*262*/ IS_LEASHED_TO_OBJECT("Object object, Object target", "bool"),
	/*263*/ GET_INTERACTION_MAGNITUDE("Object creature", "float"),
	/*264*/ IS_CREATURE_AVAILABLE("CREATURE_TYPE type", "bool"),
	/*265*/ CREATE_HIGHLIGHT("HIGHLIGHT_INFO type, Coord position, int challengeID", "Object"),
	/*266*/ GET_OBJECT_HELD(1, 1),
	/*267*/ GET_ACTION_COUNT("CREATURE_ACTION action, Object creature", "float"),
	/*268*/ GET_OBJECT_LEASH_TYPE("Object object", "int"),
	/*269*/ SET_FOCUS_FOLLOW("Object target"),
	/*270*/ SET_POSITION_FOLLOW("Object target"),
	/*271*/ SET_FOCUS_AND_POSITION_FOLLOW("Object target, float distance"),
	/*272*/ SET_CAMERA_LENS("float lens"),
	/*273*/ MOVE_CAMERA_LENS("float lens, float time"),
	/*274*/ CREATURE_REACTION(2),
	/*275*/ CREATURE_IN_DEV_SCRIPT("bool enable, Object creature"),
	/*276*/ STORE_CAMERA_DETAILS(),
	/*277*/ RESTORE_CAMERA_DETAILS(),
	/*278*/ START_ANGLE_SOUND1("bool enable"),
	/*279*/ SET_CAMERA_POS_FOC_LENS(7),
	/*280*/ MOVE_CAMERA_POS_FOC_LENS(8),
	/*281*/ GAME_TIME_ON_OFF("bool enable"),
	/*282*/ MOVE_GAME_TIME("float hourOfTheDay, float duration"),
	/*283*/ SET_HIGH_GRAPHICS_DETAIL("bool enable, Object object"),
	/*284*/ SET_SKELETON("bool enable, Object object"),
	/*285*/ IS_SKELETON("Object object", "bool"),
	/*286*/ PLAYER_SPELL_CAST_TIME("float player", "float"),
	/*287*/ PLAYER_SPELL_LAST_CAST("float player", "int"),
	/*288*/ GET_LAST_SPELL_CAST_POS("float player", "Coord"),
	/*289*/ ADD_SPOT_VISUAL_TARGET_POS("Object object, Coord position"),
	/*290*/ ADD_SPOT_VISUAL_TARGET_OBJECT("Object object, Object target"),
	/*291*/ SET_INDESTRUCTABLE("bool indestructible, Object object"),
	/*292*/ SET_GRAPHICS_CLIPPING(2),
	/*293*/ SPIRIT_APPEAR("HELP_SPIRIT_TYPE spirit"),
	/*294*/ SPIRIT_DISAPPEAR("HELP_SPIRIT_TYPE spirit"),
	/*295*/ SET_FOCUS_ON_OBJECT("Object object, Object target"),
	/*296*/ RELEASE_OBJECT_FOCUS("Object creature"),
	/*297*/ IMMERSION_EXISTS("", "bool"),
	/*298*/ SET_DRAW_LEASH("bool enable"),
	/*299*/ SET_DRAW_HIGHLIGHT("bool enable"),
	/*300*/ SET_OPEN_CLOSE("bool open, Object object"),
	/*301*/ SET_INTRO_BUILDING("bool enable"),
	/*302*/ CREATURE_FORCE_FRIENDS("bool enable, Object creature, Object targetCreature"),
	/*303*/ MOVE_COMPUTER_PLAYER_POSITION("float player, Coord position, float speed, bool withFixedHeight"),
	/*304*/ ENABLE_DISABLE_COMPUTER_PLAYER1("bool enable, float player"),
	/*305*/ GET_COMPUTER_PLAYER_POSITION("float player", "Coord"),
	/*306*/ SET_COMPUTER_PLAYER_POSITION("float player, Coord position, bool withFixedHeight"),
	/*307*/ GET_STORED_CAMERA_POSITION("", "Coord"),
	/*308*/ GET_STORED_CAMERA_FOCUS("", "Coord"),
	/*309*/ CALL_NEAR_IN_STATE("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, int state, Coord position, float radius, bool excludingScripted", "Object"),
	/*310*/ SET_CREATURE_SOUND("bool enable"),
	/*311*/ CREATURE_INTERACTING_WITH(2, "bool"),
	/*312*/ SET_SUN_DRAW(1),
	/*313*/ OBJECT_INFO_BITS("Object object", "float"),
	/*314*/ SET_HURT_BY_FIRE("bool enable, Object object"),
	/*315*/ CONFINED_OBJECT(5),
	/*316*/ CLEAR_CONFINED_OBJECT(1),
	/*317*/ GET_OBJECT_FLOCK("Object member", "Object"),
	/*318*/ SET_PLAYER_BELIEF("Object object, float player, float belief"),
	/*319*/ PLAY_JC_SPECIAL("int feature"),
	/*320*/ IS_PLAYING_JC_SPECIAL("int feature", "bool"),
	/*321*/ VORTEX_PARAMETERS("Object vortex, Object town, Coord position, float distance, float radius, Object flock"),
	/*322*/ LOAD_CREATURE("CREATURE_TYPE type, StrPtr mindFilename, float player, Coord position"),
	/*323*/ IS_SPELL_CHARGING(1, "bool"),
	/*324*/ IS_THAT_SPELL_CHARGING(2, "bool"),
	/*325*/ OPPOSING_CREATURE("int god", "int"),
	/*326*/ FLOCK_WITHIN_LIMITS("Object object", "bool"),
	/*327*/ HIGHLIGHT_PROPERTIES("Object object, int text, int category"),
	/*328*/ LAST_MUSIC_LINE("float line", "bool"),
	/*329*/ HAND_DEMO_TRIGGER("", "bool"),
	/*330*/ GET_BELLY_POSITION("Object object", "Coord"),
	/*331*/ SET_CREATURE_CREED_PROPERTIES("Object creature, HAND_GLOW handGlow, float scale, float power, float time"),
	/*332*/ GAME_THING_CAN_VIEW_CAMERA("Object object, float degrees", "bool"),
	/*333*/ GAME_PLAY_SAY_SOUND_EFFECT("bool extra, int sound, Coord position, bool withPosition"),
	/*334*/ SET_TOWN_DESIRE_BOOST("Object object, TOWN_DESIRE_INFO desire, float boost"),
	/*335*/ IS_LOCKED_INTERACTION("Object object", "bool"),
	/*336*/ SET_CREATURE_NAME("Object creature, int textID"),
	/*337*/ COMPUTER_PLAYER_READY("float player", "bool"),
	/*338*/ ENABLE_DISABLE_COMPUTER_PLAYER2("bool enable, float player"),
	/*339*/ CLEAR_ACTOR_MIND(1),
	/*340*/ ENTER_EXIT_CITADEL(1),
	/*341*/ START_ANGLE_SOUND2("bool enable"),
	/*342*/ THING_JC_SPECIAL(4, 0),
	/*343*/ MUSIC_PLAYED2(1, 1),
	/*344*/ UPDATE_SNAPSHOT_PICTURE("Coord position, Coord focus, float success, float alignment, int titleStrID, bool takingPicture, int challengeID"),
	/*345*/ STOP_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename, StrPtr scriptName"),
	/*346*/ CREATE_RANDOM_VILLAGER_OF_TRIBE("TRIBE_TYPE tribe, Coord position", "Object"),
	/*347*/ TOGGLE_LEASH("int player"),
	/*348*/ GAME_SET_MANA("Object object, float mana"),
	/*349*/ SET_MAGIC_PROPERTIES("Object object, MAGIC_TYPE magicType, float duration"),
	/*350*/ SET_GAME_SOUND("bool enable"),
	/*351*/ SEX_IS_MALE("Object object", "bool"),
	/*352*/ GET_FIRST_HELP("Object object", "float"),
	/*353*/ GET_LAST_HELP("Object object", "float"),
	/*354*/ IS_ACTIVE("Object object", "bool"),
	/*355*/ SET_BOOKMARK_POSITION(4),
	/*356*/ SET_SCAFFOLD_PROPERTIES("Object object, ABODE_NUMBER type, float size, bool destroy"),
	/*357*/ SET_COMPUTER_PLAYER_PERSONALITY("float player, StrPtr aspect, float probability"),
	/*358*/ SET_COMPUTER_PLAYER_SUPPRESSION(3),
	/*359*/ FORCE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, Object obj1, Object obj2"),
	/*360*/ QUEUE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, Object obj1, Object obj2"),
	/*361*/ GET_TOWN_WITH_ID("float id", "Object"),
	/*362*/ SET_DISCIPLE("Object object, VILLAGER_DISCIPLE discipleType, bool withSound"),
	/*363*/ RELEASE_COMPUTER_PLAYER("float player"),
	/*364*/ SET_COMPUTER_PLAYER_SPEED("float player, float speed"),
	/*365*/ SET_FOCUS_FOLLOW_COMPUTER_PLAYER("float player"),
	/*366*/ SET_POSITION_FOLLOW_COMPUTER_PLAYER("float player"),
	/*367*/ CALL_COMPUTER_PLAYER("float player", "Object"),
	/*368*/ CALL_BUILDING_IN_TOWN(4, 1),
	/*369*/ SET_CAN_BUILD_WORSHIPSITE("bool enable, Object object"),
	/*370*/ GET_FACING_CAMERA_POSITION("float distance", "Coord"),
	/*371*/ SET_COMPUTER_PLAYER_ATTITUDE("float player1, float player2, float attitude"),
	/*372*/ GET_COMPUTER_PLAYER_ATTITUDE("float player1, float player2", "float"),
	/*373*/ LOAD_COMPUTER_PLAYER_PERSONALITY(2),
	/*374*/ SAVE_COMPUTER_PLAYER_PERSONALITY(2),
	/*375*/ SET_PLAYER_ALLY("float player1, float player2, float percentage"),
	/*376*/ CALL_FLYING("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position, float radius, bool excluding scripted", "Object"),
	/*377*/ SET_OBJECT_FADE_IN("Object object, float time"),
	/*378*/ IS_AFFECTED_BY_SPELL("Object object", "bool"),
	/*379*/ SET_MAGIC_IN_OBJECT("bool enable, int MAGIC_TYPE, Object object"),
	/*380*/ ID_ADULT_SIZE("Object container", "float"),
	/*381*/ OBJECT_CAPACITY("Object container", "float"),
	/*382*/ OBJECT_ADULT_CAPACITY("Object container", "float"),
	/*383*/ SET_CREATURE_AUTO_FIGHTING("bool enable, Object creature"),
	/*384*/ IS_AUTO_FIGHTING("Object creature", "bool"),
	/*385*/ SET_CREATURE_QUEUE_FIGHT_MOVE(3, 0),
	/*386*/ SET_CREATURE_QUEUE_FIGHT_SPELL("Object creature, int spell"),
	/*387*/ SET_CREATURE_QUEUE_FIGHT_STEP("Object creature, int step"),
	/*388*/ GET_CREATURE_FIGHT_ACTION("Object creature", "int"),
	/*389*/ CREATURE_FIGHT_QUEUE_HITS("Object creature", "float"),
	/*390*/ GET_PLAYER_ALLY("float player1, float player2", "float"),
	/*391*/ SET_PLAYER_WIND_RESISTANCE(2, 1),
	/*392*/ GET_PLAYER_WIND_RESISTANCE(2, 1),
	/*393*/ PAUSE_UNPAUSE_CLIMATE_SYSTEM("bool enable"),
	/*394*/ PAUSE_UNPAUSE_STORM_CREATION_IN_CLIMATE_SYSTEM("bool enable"),
	/*395*/ GET_MANA_FOR_SPELL("MAGIC_TYPE spell", "float"),
	/*396*/ KILL_STORMS_IN_AREA("Coord position, float radius"),
	/*397*/ INSIDE_TEMPLE("", "bool"),
	/*398*/ RESTART_OBJECT("Object object"),
	/*399*/ SET_GAME_TIME_PROPERTIES(3),
	/*400*/ RESET_GAME_TIME_PROPERTIES(),
	/*401*/ SOUND_EXISTS("", "bool"),
	/*402*/ GET_TOWN_WORSHIP_DEATHS("Object town", "float"),
	/*403*/ GAME_CLEAR_DIALOGUE(),
	/*404*/ GAME_CLOSE_DIALOGUE(),
	/*405*/ GET_HAND_STATE("", "int"),
	/*406*/ SET_INTERFACE_CITADEL("bool enable"),
	/*407*/ MAP_SCRIPT_FUNCTION("StrPtr command"),
	/*408*/ WITHIN_ROTATION("", "bool"),
	/*409*/ GET_PLAYER_TOWN_TOTAL("float player", "float"),
	/*410*/ SPIRIT_SCREEN_POINT(3),
	/*411*/ KEY_DOWN("int key", "bool"),
	/*412*/ SET_FIGHT_CAMERA_EXIT(1, 0),
	/*413*/ GET_OBJECT_CLICKED("", "Object"),
	/*414*/ GET_MANA("Object worshipSite", "float"),
	/*415*/ CLEAR_PLAYER_SPELL_CHARGING("float player"),
	/*416*/ STOP_SOUND_EFFECT("bool alwaysFalse, int sound, AUDIO_SFX_BANK_TYPE soundbank"),
	/*417*/ GET_TOTEM_STATUE("Object town", "Object"),
	/*418*/ SET_SET_ON_FIRE("bool enable, Object object"),
	/*419*/ SET_LAND_BALANCE(2),
	/*420*/ SET_OBJECT_BELIEF_SCALE(2),
	/*421*/ START_IMMERSION("IMMERSION_EFFECT_TYPE effect"),
	/*422*/ STOP_IMMERSION("IMMERSION_EFFECT_TYPE effect"),
	/*423*/ STOP_ALL_IMMERSION(),
	/*424*/ SET_CREATURE_IN_TEMPLE("bool enable"),
	/*425*/ GAME_DRAW_TEXT("int textID, float across, float down, float width, float height, float size, float fade"),
	/*426*/ GAME_DRAW_TEMP_TEXT("StrPtr string, float across, float down, float width, float height, float size, float fade"),
	/*427*/ FADE_ALL_DRAW_TEXT("float time"),
	/*428*/ SET_DRAW_TEXT_COLOUR("float red, float green, float blue"),
	/*429*/ SET_CLIPPING_WINDOW("float across, float down, float width, float height, float time"),
	/*430*/ CLEAR_CLIPPING_WINDOW("float time"),
	/*431*/ SAVE_GAME_IN_SLOT("int slot"),
	/*432*/ SET_OBJECT_CARRYING("Object object, CARRIED_OBJECT carriedObj"),
	/*433*/ POS_VALID_FOR_CREATURE("Coord position", "bool"),
	/*434*/ GET_TIME_SINCE_OBJECT_ATTACKED("float player, Object town", "float"),
	/*435*/ GET_TOWN_AND_VILLAGER_HEALTH_TOTAL("Object town", "float"),
	/*436*/ GAME_ADD_FOR_BUILDING(2),
	/*437*/ ENABLE_DISABLE_ALIGNMENT_MUSIC("bool enable"),
	/*438*/ GET_DEAD_LIVING("Coord position, float radius", "Object"),
	/*439*/ ATTACH_SOUND_TAG("bool threeD, int sound, AUDIO_SFX_BANK_TYPE soundbank, Object target"),
	/*440*/ DETACH_SOUND_TAG("int sound, AUDIO_SFX_BANK_TYPE soundbank, Object target"),
	/*441*/ GET_SACRIFICE_TOTAL("Object worshipSite", "float"),
	/*442*/ GAME_SOUND_PLAYING("int sound, AUDIO_SFX_BANK_TYPE soundbank", "bool"),
	/*443*/ GET_TEMPLE_POSITION("float player", "Coord"),
	/*444*/ CREATURE_AUTOSCALE("bool enable, Object creature, float size"),
	/*445*/ GET_SPELL_ICON_IN_TEMPLE("MAGIC_TYPE spell, Object temple", "Object"),
	/*446*/ GAME_CLEAR_COMPUTER_PLAYER_ACTIONS("float player"),
	/*447*/ GET_FIRST_IN_CONTAINER("Object container", "Object"),
	/*448*/ GET_NEXT_IN_CONTAINER("Object container, Object after", "Object"),
	/*449*/ GET_TEMPLE_ENTRANCE_POSITION("float player, float radius, float height", "Coord"),
	/*450*/ SAY_SOUND_EFFECT_PLAYING("bool alwaysFalse, int sound", "bool"),
	/*451*/ SET_HAND_DEMO_KEYS(1),
	/*452*/ CAN_SKIP_TUTORIAL("", "bool"),
	/*453*/ CAN_SKIP_CREATURE_TRAINING("", "bool"),
	/*454*/ IS_KEEPING_OLD_CREATURE("", "bool"),
	/*455*/ CURRENT_PROFILE_HAS_CREATURE("", "bool"),
	/*456*/ THING_PLAY_ANIM(3, 0),
	/*457*/ SET_SCRIPT_STATE_WITH_PARAMS(8, 0),
	/*458*/ START_COUNTDOWN_TIMER(2, 0),
	/*459*/ END_COUNTDOWN_TIMER(0, 0),
	/*460*/ SET_COUNTDOWN_TIMER_DRAW(1, 0),
	/*461*/ SET_OBJECT_SCORE(2, 0),
	/*462*/ GET_OBJECT_SCORE(1, 1),
	/*463*/ SET_CREATURE_FOLLOW_MASTER(2, 0),
	/*464*/ SET_CREATURE_DISTANCE_FROM_HOME(2, 0),
	/*465*/ GAME_DELETE_FIRE(4, 0),
	/*466*/ GET_OBJECT_EP(2, 3),
	/*467*/ GET_COUNTDOWN_TIMER_TIME(0, 1),
	/*468*/ SET_OBJECT_IN_PLAYER_HAND(2, 0),
	/*469*/ CREATE_PLAYER_TEMPLE(4, 1),
	/*470*/ START_CANNON_CAMERA(0, 0),
	/*471*/ END_CANNON_CAMERA(0, 0),
	/*472*/ GET_LANDING_POS(5, 3),
	/*473*/ SET_CREATURE_MASTER(2, 0),
	/*474*/ SET_CANNON_PERCENTAGE(1, 0),
	/*475*/ SET_DIE_ROLL_CHECK(2, 0),
	/*476*/ SET_CAMERA_HEADING_FOLLOW(2, 0),
	/*477*/ SET_CANNON_STRENGTH(1, 0),
	/*478*/ GAME_CREATE_TOWN(5, 1),
	/*479*/ SET_OBJECT_NAVIGATION(2, 0),
	/*480*/ DO_ACTION_AT_POS(6, 0),
	/*481*/ GET_OBJECT_DESIRE(3, 1),
	/*482*/ GET_CREATURE_CURRENT_ACTION(1, 1),
	/*483*/ GET_CREATURE_SPELL_SKILL(2, 1),
	/*484*/ GET_CREATURE_KNOWS_ACTION(2, 1),
	/*485*/ CALL_BUILDING_WOODPILE_IN_TOWN(1, 1),
	/*486*/ GET_MOUSE_ACROSS(0, 1),
	/*487*/ GET_MOUSE_DOWN(0, 1),
	/*488*/ SET_DOLPHIN_MOVE(7, 0),
	/*489*/ MOUSE_DOWN(1, 1),
	/*490*/ IN_WIDESCREEN(0, 1),
	/*491*/ AFFECTED_BY_SNOW(2, 0),
	/*492*/ SET_DOLPHIN_SPEED(1, 0),
	/*493*/ SET_DOLPHIN_WAIT(1, 0),
	/*494*/ FIRE_GUN(1, 0),
	/*495*/ GUN_ANGLE_PITCH(3, 0),
	/*496*/ SET_OBJECT_TATTOO(3, 0),
	/*497*/ CREATURE_CLEAR_FIGHT_QUEUE(1, 0),
	/*498*/ CAN_BE_LEASHED(2, 0),
	/*499*/ SET_BOOKMARK_ON_OBJECT(2, 0),
	/*500*/ SET_OBJECT_LIGHTBULB(2, 0),
	/*501*/ SET_CREATURE_CAN_DROP(2, 0),
	/*502*/ PLAY_SPIRIT_ANIM_IN_WORLD(6, 0),
	/*503*/ SET_OBJECT_COLOUR(4, 0),
	/*504*/ EFFECT_FROM_FILE(1, 1),
	/*505*/ ALEX_SPECIAL_EFFECT_POSITION(4, 1),
	/*506*/ DELETE_FRAGMENTS_IN_RADIUS(4, 0),
	/*507*/ DELETE_FRAGMENTS_FOR_OBJECT(1, 0),
	/*508*/ SET_CAMERA_AUTO_TRACK(2, 0),
	/*509*/ CREATURE_HELP_ON(0, 1),
	/*510*/ CREATURE_CAN_LEARN(3, 0),
	/*511*/ GET_OBJECT_HAND_POSITION(2, 3),
	/*512*/ CREATURE_SET_RIGHT_HAND_ONLY(2, 0),
	/*513*/ GAME_HOLD_WIDESCREEN(0, 0),
	/*514*/ CREATURE_CREATE_YOUNG_WITH_KNOWLEDGE(5, 1),
	/*515*/ STOP_DIALOGUE_SOUND(0, 0),
	/*516*/ GAME_THING_HIT_LAND(1, 1),
	/*517*/ GET_LAST_OBJECT_WHICH_HIT_LAND(0, 1),
	/*518*/ CLEAR_HIT_LAND_OBJECT(0, 0),
	/*519*/ SET_DRAW_SCOREBOARD(1, 0),
	/*520*/ GET_BRACELET_POSITION(1, 3),
	/*521*/ SET_FIGHT_LOCK(1, 0),
	/*522*/ SET_VILLAGER_SOUND(1, 0),
	/*523*/ CLEAR_SPELLS_ON_OBJECT(1, 0),
	/*524*/ ENABLE_OBJECT_IMMUNE_TO_SPELLS(2, 0),
	/*525*/ IS_OBJECT_IMMUNE_TO_SPELLS(1, 1),
	/*526*/ GET_OBJECT_OBJECT_LEASHED_TO(1, 1),
	/*527*/ SET_FIGHT_QUEUE_ONLY(1, 0);
	
	/**If varargs is false, this is the exact number of values popped from the stack.
	 * If varargs is true, this is the minimum number of values popped from the stack; the exact
	 * number is instruction-dependent.
	 */
	public final int pop;
	/**The number of values pushed on the stack on return.
	 * Valid values are 0, 1 or 3 (for Coord).
	 */
	public final int push;
	/**The high-level argument types and names*/
	public final Argument[] args;
	/**The high-level return type. Null if no value is returned.*/
	public final ArgType returnType;
	/**Tells whether the number of values popped from the stack is variable or not.*/
	public final boolean varargs;
	/**Tells whether the function must be called with SYS2 or not.*/
	public final boolean sys2;
	
	NativeFunction() {
		this(0, 0);
	}
	
	//TODO comment out when finished
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
					n += arg.type.stackCount;
				}
			}
			this.pop = n;
		}
		this.varargs = varargs;
		//
		if (sRet == null || sRet.isEmpty()) {
			this.push = 0;
			this.returnType = null;
		} else {
			returnType = ArgType.fromKeyword(sRet);
			this.push = returnType.stackCount;
		}
		//
		this.sys2 = sys2;
	}
	
	//TODO comment out when finished
	NativeFunction(int pop, int push) {
		this.pop = pop;
		this.push = push;
		this.args = new Argument[pop];
		for (int i = 0; i < pop; i++) {
			args[i] = new Argument(ArgType.UNKNOWN, null, false);
		}
		this.varargs = false;
		if (push == 0) {
			this.returnType = null;
		} else if (push == 1) {
			this.returnType = ArgType.UNKNOWN;
		} else if (push == 3) {
			this.returnType = ArgType.COORD;
		} else {
			throw new IllegalArgumentException("Invalid number of return values: " + push);
		}
		this.sys2 = false;
	}
	
	//TODO comment out when finished
	NativeFunction(int pop, String sRet) {
		this.pop = pop;
		this.args = new Argument[pop];
		for (int i = 0; i < pop; i++) {
			args[i] = new Argument(ArgType.UNKNOWN, null, false);
		}
		this.varargs = false;
		if (sRet == null || sRet.isEmpty()) {
			this.push = 0;
			this.returnType = null;
		} else {
			returnType = ArgType.fromKeyword(sRet);
			this.push = returnType.stackCount;
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
	
	public String getInfoString() {
		String s = "[" + pop;
		if (varargs) s += "+";
		s += ", " + push + "] (" + getArgsString() + ")";
		if (returnType != null) {
			s += " returns (" + returnType + ")";
		}
		return s;
	}
	
	public String getCStyleSignature() {
		return (returnType == null ? "void" : returnType.toString())
				+ " " + this
				+ "(" + getArgsString() + ")";
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
		COORD("Coord", 3),				//3 floats
		BOOL("bool"),
		OBJECT_OBJ("ObjectObj"),
		OBJECT_FLOAT("Object"),
		OBJECT_INT("ObjectInt"),
		INT_OR_FLOAT("int|float"),		//used only with GET_PROPERTY
		STRPTR("StrPtr"),				//int (byte offset in data section)
		
		/* The following are enums. Be aware that many enum values defined in .h files are obsolete,
		 * .txt files are more up to date. TODO: check all required values and write our own version.
		 */
		SCRIPT_OBJECT_TYPE(),			//defined in ScriptEnums.h
		SCRIPT_OBJECT_SUBTYPE(),		//various enums defined in info2.txt
		SCRIPT_OBJECT_PROPERTY_TYPE(),	//defined in ScriptEnums.h - TODO: assign a type to each property
		SCRIPT_BOOL(),					//defined in ScriptEnums.h
		SCRIPT_INTERFACE_LEVEL(),		//defined in ScriptEnums.h
		MAGIC_TYPE(),					//defined in Enum.h
		TOWN_DESIRE_INFO(),				//defined in Enum.h
		IMMERSION_EFFECT_TYPE(),		//defined in Enum.h
		CARRIED_OBJECT(),				//defined in Enum.h
		REACTION(),						//defined in Enum.h
		ABODE_NUMBER(),					//defined in Enum.h
		TRIBE_TYPE(),					//defined in Enum.h
		DANCE_INFO(),					//defined in Enum.h
		REWARD_OBJECT_INFO(),			//defined in Enum.h
		HIGHLIGHT_INFO(),				//defined in Enum.h
		RESOURCE_TYPE(),				//defined in Enum.h
		HELP_SPIRIT_TYPE(),				//defined in Enum.h
		VILLAGER_DISCIPLE(),			//defined in Enum.h
		CREATURE_DESIRES(),				//defined in Enum.h
		CREATURE_TYPE(),				//defined in CreatureEnum.h
		CREATURE_ACTION(),				//defined in CreatureEnum.h
		DEVELOPMENT_PHASE(),			//defined in CreatureEnum.h
		CREATURE_ACTION_TYPE(),			//see enums.txt
		CREATURE_ACTION_SUBTYPE(),		//various enums defined in Enum.h
		AUDIO_SFX_BANK_TYPE(),			//defined in AudioSFX.h
		VILLAGER_STATES(),				//defined in GStates.h
		DETAIL_ANIM_TYPES(),			//defined in info1.txt (alias of ANIM_LIST in AllMeshes.h)
		HELP_EVENT_TYPE(),				//see enums.txt
		HAND_GLOW(),					//see enums.txt
		FIGHT_MOVE();					//defined in HitRegions.h
		
		private static final Map<String, ArgType> map = new HashMap<>();
		
		static {
			for (ArgType t : values()) {
				map.put(t.keyword, t);
			}
		}
		
		public final String keyword;
		public final boolean isInt;
		public final int stackCount;
		
		private ArgType() {
			this(null);
		}
		
		private ArgType(String keyword) {
			this(keyword, 1);
		}
		
		private ArgType(String keyword, int stackCount) {
			this.keyword = keyword != null ? keyword : this.name();
			this.isInt = this.ordinal() >= 9;	//STRPTR
			this.stackCount = stackCount;
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

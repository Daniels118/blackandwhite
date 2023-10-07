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
 * 
 * About order of parameters
 * For the sake of clarity, and to be consistent with the CALL/START instructions, parameters in the
 * signature of native functions are reported in the same order they are pushed to the stack, but for
 * various reasons that I'm not going to explain here, this is certainly the opposite.
 * For example, the function MOVE_CAMERA_FOCUS(Coord position, float time)
 * which by the truth is MOVE_CAMERA_FOCUS(float x, float y, float z, float time)
 * is certainly defined in C as MOVE_CAMERA_FOCUS(float time, float z, float y, float x).
 * 
 * */

//TODO complete functions description

public enum NativeFunction {
/*000*/	NONE(),
/*001*/	SET_CAMERA_POSITION("Coord position"),
/*002*/	SET_CAMERA_FOCUS("Coord position"),
/*003*/	MOVE_CAMERA_POSITION("Coord position, float time"),
/*004*/	MOVE_CAMERA_FOCUS("Coord position, float time"),
/*005*/	GET_CAMERA_POSITION("", "Coord"),
/*006*/	GET_CAMERA_FOCUS("", "Coord"),
/*007*/	SPIRIT_EJECT("HELP_SPIRIT_TYPE spirit"),
/*008*/	SPIRIT_HOME("HELP_SPIRIT_TYPE spirit"),
/*009*/	SPIRIT_POINT_POS("HELP_SPIRIT_TYPE spirit, Coord position, bool inWorld"),
/*010*/	SPIRIT_POINT_GAME_THING("HELP_SPIRIT_TYPE spirit, ObjectFloat target, bool inWorld"),
/*011*/	GAME_THING_FIELD_OF_VIEW("ObjectFloat object", "bool"),
/*012*/	POS_FIELD_OF_VIEW("Coord position", "bool"),
/*013*/	RUN_TEXT("bool singleLine, int textID, int withInteraction"),
/*014*/	TEMP_TEXT("bool singleLine, StrPtr string, int withInteraction"),
/*015*/	TEXT_READ("", "bool"),
/*016*/	GAME_THING_CLICKED("ObjectFloat object", "bool"),
/*017*/	SET_SCRIPT_STATE("ObjectFloat object, int"),
/*018*/	SET_SCRIPT_STATE_POS("ObjectInt object, Coord position"),
/*019*/	SET_SCRIPT_FLOAT("ObjectInt object, float"),
/*020*/	SET_SCRIPT_ULONG("Object object, int, int"),
/*021*/	GET_PROPERTY("SCRIPT_OBJECT_PROPERTY_TYPE prop, ObjectFloat object", "int|float"),	//Call with SYS or SYS2 depending on the property
/*022*/	SET_PROPERTY("SCRIPT_OBJECT_PROPERTY_TYPE prop, ObjectFloat object, float val", true),
/*023*/	GET_POSITION("ObjectFloat object", "Coord"),
/*024*/	SET_POSITION("ObjectFloat object, Coord position"),
/*025*/	GET_DISTANCE("Coord p0, Coord p1", "float"),
/*026*/	CALL("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position, bool alwaysFalse", "float"),
/*027*/	CREATE("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position", "Object"),
/*028*/	RANDOM("float min, float max", "float"),
/*029*/	DLL_GETTIME("", "float"),			//"time" statement, see section 12.13 of CHL doc.
/*030*/	START_CAMERA_CONTROL("", "bool"),
/*031*/	END_CAMERA_CONTROL(),
/*032*/	SET_WIDESCREEN("bool enabled"),
/*033*/	MOVE_GAME_THING("ObjectFloat object, Coord position, float radius"),
/*034*/	SET_FOCUS("ObjectFloat object, Coord position"),
/*035*/	HAS_CAMERA_ARRIVED("", "bool"),
/*036*/	FLOCK_CREATE("Coord position", "Object"),
/*037*/	FLOCK_ATTACH("ObjectFloat obj, ObjectFloat flock, bool", "Object"),	//Always discard return value with POPO
/*038*/	FLOCK_DETACH("ObjectFloat obj, ObjectFloat flock", "Object"),	/* Use "PUSHO 0" to refer to a random object in the flock.Returns the detached object (useful when detaching a random one).*/
/*039*/	FLOCK_DISBAND("ObjectFloat flock"),
/*040*/	ID_SIZE("ObjectFloat flock", "float"),
/*041*/	FLOCK_MEMBER("ObjectFloat obj, ObjectFloat flock", "bool"),
/*042*/	GET_HAND_POSITION("", "Coord"),
/*043*/	PLAY_SOUND_EFFECT("int sound, int soundbank, Coord position, bool withPosition"),
/*044*/	START_MUSIC("int music"),
/*045*/	STOP_MUSIC(),
/*046*/	ATTACH_MUSIC("int music, ObjectFloat target"),
/*047*/	DETACH_MUSIC("ObjectFloat object"),
/*048*/	OBJECT_DELETE("ObjectFloat obj, int withFade"),	//Use "ZERO [varname]" just after to clear the object reference.
/*049*/	FOCUS_FOLLOW("ObjectFloat target"),
/*050*/	POSITION_FOLLOW("ObjectFloat target"),	//Never found
/*051*/	CALL_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position, float radius, bool", "Object"),
/*052*/	SPECIAL_EFFECT_POSITION("int, Coord position, float", "float"),
/*053*/	SPECIAL_EFFECT_OBJECT("int, ObjectFloat obj, float", "float"),
/*054*/	DANCE_CREATE("ObjectFloat obj, int, Coord position, float", "Object"),
/*055*/	CALL_IN("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, ObjectFloat obj, bool", "Object"),
/*056*/	CHANGE_INNER_OUTER_PROPERTIES("ObjectFloat obj, float inner, float outer, float calm"),
/*057*/	SNAPSHOT("bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"),
/*058*/	GET_ALIGNMENT("int", "float", true),
/*059*/	SET_ALIGNMENT(2),					//Never found; guess (int, float)
/*060*/	INFLUENCE_OBJECT("ObjectFloat obj, float, int, int", "Object"),
/*061*/	INFLUENCE_POSITION("Coord position, float, int, int", "Object"),
/*062*/	GET_INFLUENCE("float, bool, Coord position", "float"),
/*063*/	SET_INTERFACE_INTERACTION("SCRIPT_INTERFACE_LEVEL level", true),
/*064*/	PLAYED("ObjectFloat obj", "bool"),
/*065*/	RANDOM_ULONG("int min, int max", "int", true),
/*066*/	SET_GAMESPEED("float speed", true),
/*067*/	CALL_IN_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, ObjectFloat obj, Coord pos, float radius, bool excludingScripted", "Object"),
/*068*/	OVERRIDE_STATE_ANIMATION("ObjectFloat obj, DETAIL_ANIM_TYPES animType"),
/*069*/	CREATURE_CREATE_RELATIVE_TO_CREATURE("ObjectFloat, float, Coord position, int", "Object"),
/*070*/	CREATURE_LEARN_EVERYTHING("ObjectFloat creature"),
/*071*/	CREATURE_SET_KNOWS_ACTION("ObjectFloat creature, CREATURE_ACTION_TYPE typeOfAction, CREATURE_ACTION_SUBTYPE action, SCRIPT_BOOL knows"),
/*072*/	CREATURE_SET_AGENDA_PRIORITY("ObjectFloat creature, float priority"),
/*073*/	CREATURE_TURN_OFF_ALL_DESIRES(1),	//Never found; guess (ObjectFloat creature)
/*074*/	CREATURE_LEARN_DISTINCTION_ABOUT_ACTIVITY_OBJECT(4),	//Never found
/*075*/	CREATURE_DO_ACTION("ObjectFloat creature, CREATURE_ACTION, ObjectFloat target, ObjectFloat withObject"),
/*076*/	IN_CREATURE_HAND("ObjectFloat obj, ObjectFloat creature", "bool"),
/*077*/	CREATURE_SET_DESIRE_VALUE("ObjectFloat creature, CREATURE_DESIRES desire, float value"),
/*078*/	CREATURE_SET_DESIRE_ACTIVATED3("ObjectFloat creature, CREATURE_DESIRES desire, SCRIPT_BOOL active"),
/*079*/	CREATURE_SET_DESIRE_ACTIVATED2("ObjectFloat creature, SCRIPT_BOOL active"),
/*080*/	CREATURE_SET_DESIRE_MAXIMUM("ObjectFloat creature, CREATURE_DESIRES desire, float value"),
/*081*/	CONVERT_CAMERA_POSITION("int", "Coord"),
/*082*/	CONVERT_CAMERA_FOCUS("int", "Coord"),
/*083*/	CREATURE_SET_PLAYER("ObjectFloat creature"),
/*084*/	START_COUNTDOWN_TIMER(1),			//Never found
/*085*/	CREATURE_INITIALISE_NUM_TIMES_PERFORMED_ACTION(2),	//Never found
/*086*/	CREATURE_GET_NUM_TIMES_ACTION_PERFORMED(2, 1),	//Never found
/*087*/	REMOVE_COUNTDOWN_TIMER(),			//Never found
/*088*/	GET_OBJECT_DROPPED(1, 1),			//Never found
/*089*/	CLEAR_DROPPED_BY_OBJECT(1),			//Never found; guess (ObjectFloat creature)
/*090*/	CREATE_REACTION("ObjectFloat object, REACTION reaction"),
/*091*/	REMOVE_REACTION("ObjectFloat object"),	//Never found
/*092*/	GET_COUNTDOWN_TIMER(0, 1),			//Never found
/*093*/	START_DUAL_CAMERA("ObjectFloat obj1, ObjectFloat obj2"),
/*094*/	UPDATE_DUAL_CAMERA("ObjectFloat obj1, ObjectFloat obj2"),	//Never found
/*095*/	RELEASE_DUAL_CAMERA(),
/*096*/	SET_CREATURE_HELP(1),				//Never found
/*097*/	GET_TARGET_OBJECT("ObjectFloat obj", "Object"),
/*098*/	CREATURE_DESIRE_IS(2, 1),			//Never found
/*099*/	COUNTDOWN_TIMER_EXISTS("", "bool"),	//Never found
/*100*/	LOOK_GAME_THING("HELP_SPIRIT_TYPE spirit, ObjectFloat target"),
/*101*/	GET_OBJECT_DESTINATION("ObjectFloat obj", "Coord"),	//Never found
/*102*/	CREATURE_FORCE_FINISH("ObjectFloat creature"),	//Never found
/*103*/	HIDE_COUNTDOWN_TIMER(),				//Never found
/*104*/	GET_ACTION_TEXT_FOR_OBJECT("ObjectFloat obj", "int"),	//The return value is used as second parameter in RUN_TEXT
/*105*/	CREATE_DUAL_CAMERA_WITH_POINT("ObjectFloat obj, Coord position"),	//Never found
/*106*/	SET_CAMERA_TO_FACE_OBJECT(2),		//Never found
/*107*/	MOVE_CAMERA_TO_FACE_OBJECT("ObjectFloat target, float distance, float time"),
/*108*/	GET_MOON_PERCENTAGE("", "float"),	//Never found
/*109*/	POPULATE_CONTAINER("ObjectFloat obj, float quantity, SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype"),
/*110*/	ADD_REFERENCE(1, 1),				//Never found
/*111*/	REMOVE_REFERENCE(1, 1),				//Never found
/*112*/	SET_GAME_TIME("float time"),
/*113*/	GET_GAME_TIME("", "float"),
/*114*/	GET_REAL_TIME(0, 1),				//Never found
/*115*/	GET_REAL_DAY1(0, 1),				//Never found
/*116*/	GET_REAL_DAY2(0, 1),				//Never found
/*117*/	GET_REAL_MONTH(0, 1),				//Never found
/*118*/	GET_REAL_YEAR(0, 1),				//Never found
/*119*/	RUN_CAMERA_PATH("int cameraEnum"),
/*120*/	START_DIALOGUE("", "bool"),
/*121*/	END_DIALOGUE(),
/*122*/	IS_SPIRIT_READY("", "bool"),
/*123*/	CHANGE_WEATHER_PROPERTIES("ObjectFloat storm, float temperature, float rainfall, float snowfall, float overcast, float fallspeed"),
/*124*/	CHANGE_LIGHTNING_PROPERTIES("ObjectFloat storm, float sheetmin, float sheetmax, float forkmin, float forkmax"),
/*125*/	CHANGE_TIME_FADE_PROPERTIES("ObjectFloat storm, float duration, float fadeTime"),
/*126*/	CHANGE_CLOUD_PROPERTIES("ObjectFloat storm, float numClouds, float blackness, float elevation"),
/*127*/	SET_HEADING_AND_SPEED("ObjectFloat, Coord position, float speed"),
/*128*/	START_GAME_SPEED(),
/*129*/	END_GAME_SPEED(),
/*130*/	BUILD_BUILDING("Coord position, float desire"),
/*131*/	SET_AFFECTED_BY_WIND("bool enabled, ObjectFloat object"),
/*132*/	WIDESCREEN_TRANSISTION_FINISHED("", "bool"),
/*133*/	GET_RESOURCE("int resource, ObjectFloat obj", "float"),
/*134*/	ADD_RESOURCE("int resource, float quantity, ObjectFloat obj", "float"),
/*135*/	REMOVE_RESOURCE("int resource, float quantity, ObjectFloat obj", "float"),
/*136*/	GET_TARGET_RELATIVE_POS("Coord p0, Coord p1, float, float", "Coord"),
/*137*/	STOP_POINTING("HELP_SPIRIT_TYPE spirit"),
/*138*/	STOP_LOOKING("HELP_SPIRIT_TYPE spirit"),
/*139*/	LOOK_AT_POSITION("HELP_SPIRIT_TYPE spirit, Coord position"),
/*140*/	PLAY_SPIRIT_ANIM(5),				//Never found
/*141*/	CALL_IN_NOT_NEAR("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, ObjectFloat obj, Coord pos, float radius, bool excludingScripted", "bool"),
/*142*/	SET_CAMERA_ZONE("StrPtr filename"),	//filename is relative to folder Data\Zones, eg. Land1Zone5.exc
/*143*/	GET_OBJECT_STATE("ObjectFloat obj", "float"),	//Return value must be casted to float
/*144*/	REVEAL_COUNTDOWN_TIMER(),			//Never found
/*145*/	SET_TIMER_TIME("ObjectFloat timer, float time"),
/*146*/	CREATE_TIMER("float timeout", "Object"),
/*147*/	GET_TIMER_TIME_REMAINING("ObjectFloat timer", "float"),
/*148*/	GET_TIMER_TIME_SINCE_SET("ObjectFloat timer", "float"),
/*149*/	MOVE_MUSIC(2),						//Never found
/*150*/	GET_INCLUSION_DISTANCE("", "float"),	//Never found
/*151*/	GET_LAND_HEIGHT("Coord position", "float"),
/*152*/	LOAD_MAP("StrPtr path"),			//path is relative to installation, eg. "scripts/land2.txt"
/*153*/	STOP_ALL_SCRIPTS_EXCLUDING("StrPtr scriptName"),	//Never found
/*154*/	STOP_ALL_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename"),
/*155*/	STOP_SCRIPT("StrPtr scriptName"),
/*156*/	CLEAR_CLICKED_OBJECT(),
/*157*/	CLEAR_CLICKED_POSITION(),			//Never found
/*158*/	POSITION_CLICKED(4, "bool"),		//Never found
/*159*/	RELEASE_FROM_SCRIPT("ObjectFloat obj"),
/*160*/	GET_OBJECT_HAND_IS_OVER("", "bool"),	//Never found
/*161*/	ID_POISONED_SIZE("ObjectFloat obj", "float"),
/*162*/	IS_POISONED("ObjectFloat obj", "bool"),
/*163*/	CALL_POISONED_IN(4, 1),				//Never found
/*164*/	CALL_NOT_POISONED_IN("SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, ObjectFloat obj, bool", "Object"),
/*165*/	SPIRIT_PLAYED("HELP_SPIRIT_TYPE spirit", "bool"),	//Never found
/*166*/	CLING_SPIRIT("HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
/*167*/	FLY_SPIRIT("HELP_SPIRIT_TYPE spirit, float xPercent, float yPercent"),
/*168*/	SET_ID_MOVEABLE("bool moveable, ObjectFloat obj"),
/*169*/	SET_ID_PICKUPABLE("bool pickupable, ObjectFloat obj"),
/*170*/	IS_ON_FIRE("ObjectFloat obj", "bool"),
/*171*/	IS_FIRE_NEAR("Coord position, float radius", "bool"),
/*172*/	STOP_SCRIPTS_IN_FILES("StrPtr sourceFilename"),
/*173*/	SET_POISONED("bool poisoned, ObjectFloat obj"),
/*174*/	SET_TEMPERATURE("ObjectFloat obj, float temperature"),
/*175*/	SET_ON_FIRE("bool enable, ObjectFloat object, float burnSpeed"),
/*176*/	SET_TARGET("ObjectFloat obj, Coord position, float time"),
/*177*/	WALK_PATH("ObjectFloat object, bool forward, int camera_enum, float valFrom, float valTo"),
/*178*/	FOCUS_AND_POSITION_FOLLOW(2),
/*179*/	GET_WALK_PATH_PERCENTAGE(1, 1),
/*180*/	CAMERA_PROPERTIES("float distance, float speed, float angle, bool enableBehind"),
/*181*/	ENABLE_DISABLE_MUSIC(2),			//Never found
/*182*/	GET_MUSIC_OBJ_DISTANCE(1, 1),
/*183*/	GET_MUSIC_ENUM_DISTANCE(1, 1),
/*184*/	SET_MUSIC_PLAY_POSITION(4),			//Never found
/*185*/	ATTACH_OBJECT_LEASH_TO_OBJECT(2),	//Never found; guess (ObjectFloat creature, ObjectFloat target)
/*186*/	ATTACH_OBJECT_LEASH_TO_HAND(1),		//Never found; guess (ObjectFloat creature)
/*187*/	DETACH_OBJECT_LEASH("ObjectFloat creature"),
/*188*/	SET_CREATURE_ONLY_DESIRE("ObjectFloat creature, CREATURE_DESIRES desire, float value"),	//value must be 86400 (timeout?)
/*189*/	SET_CREATURE_ONLY_DESIRE_OFF("ObjectFloat creature"),
/*190*/	RESTART_MUSIC(1),					//Never found
/*191*/	MUSIC_PLAYED1(1, 1),				//Never found
/*192*/	IS_OF_TYPE("ObjectFloat object, SCRIPT_OBJECT_TYPE type, int subtype", "bool"),
/*193*/	CLEAR_HIT_OBJECT(),
/*194*/	GAME_THING_HIT("ObjectFloat object", "bool"),
/*195*/	SPELL_AT_THING(8, 1),
/*196*/	SPELL_AT_POS(10, 1),
/*197*/	CALL_PLAYER_CREATURE(1, 1),
/*198*/	GET_SLOWEST_SPEED(1, 1),
/*199*/	GET_OBJECT_HELD1(0, 1),
/*200*/	HELP_SYSTEM_ON("", "bool"),			//Never found
/*201*/	SHAKE_CAMERA("Coord position, float radius, float amplitude, float duration"),
/*202*/	SET_ANIMATION_MODIFY("bool enable, ObjectFloat creature"),
/*203*/	SET_AVI_SEQUENCE("bool enable, int aviSequence"),
/*204*/	PLAY_GESTURE(5),
/*205*/	DEV_FUNCTION("int func"),
/*206*/	HAS_MOUSE_WHEEL("", "bool"),
/*207*/	NUM_MOUSE_BUTTONS(0, 1),
/*208*/	SET_CREATURE_DEV_STAGE("ObjectFloat creature, DEVELOPMENT_PHASE stage"),
/*209*/	SET_FIXED_CAM_ROTATION(4),			//Never found
/*210*/	SWAP_CREATURE("ObjectFloat fromCreature, ObjectFloat toCreature"),
/*211*/	GET_ARENA(5, 1),
/*212*/	GET_FOOTBALL_PITCH(1, 1),
/*213*/	STOP_ALL_GAMES(1),					//Never found; guess (ObjectFloat object)
/*214*/	ATTACH_TO_GAME(3),					//Never found
/*215*/	DETACH_FROM_GAME(3),				//Never found
/*216*/	DETACH_UNDEFINED_FROM_GAME(2),		//Never found
/*217*/	SET_ONLY_FOR_SCRIPTS(2),			//Never found
/*218*/	START_MATCH_WITH_REFEREE(2),		//Never found
/*219*/	GAME_TEAM_SIZE(2),
/*220*/	GAME_TYPE(1, 1),
/*221*/	GAME_SUB_TYPE(1, 1),
/*222*/	IS_LEASHED(1, 1),
/*223*/	SET_CREATURE_HOME("ObjectFloat creature, Coord position"),
/*224*/	GET_HIT_OBJECT(0, 1),
/*225*/	GET_OBJECT_WHICH_HIT(0, 1),
/*226*/	GET_NEAREST_TOWN_OF_PLAYER(5, 1),
/*227*/	SPELL_AT_POINT(5, 1),
/*228*/	SET_ATTACK_OWN_TOWN(2),				//Never found
/*229*/	IS_FIGHTING(1, 1),
/*230*/	SET_MAGIC_RADIUS("ObjectFloat object, float radius"),
/*231*/	TEMP_TEXT_WITH_NUMBER(4),			//Never found
/*232*/	RUN_TEXT_WITH_NUMBER("bool alwaysFalse, int string, float number, int alwaysZero"),
/*233*/	CREATURE_SPELL_REVERSION(2),		//Never found 
/*234*/	GET_DESIRE(2, 1),
/*235*/	GET_EVENTS_PER_SECOND(1, 1),
/*236*/	GET_TIME_SINCE(1, 1),
/*237*/	GET_TOTAL_EVENTS(1, 1),
/*238*/	UPDATE_SNAPSHOT("float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID"), //The challengeID is set at compile time by the previous "challenge" statement
/*239*/	CREATE_REWARD(5, 1),
/*240*/	CREATE_REWARD_IN_TOWN(6, 1),
/*241*/	SET_FADE("float red, float green, float blue, float time"),
/*242*/	SET_FADE_IN(1),
/*243*/	FADE_FINISHED(0, 1),
/*244*/	SET_PLAYER_MAGIC(3),
/*245*/	HAS_PLAYER_MAGIC(2, 1),
/*246*/	SPIRIT_SPEAKS(2, 1),
/*247*/	BELIEF_FOR_PLAYER(2, 1),
/*248*/	GET_HELP(1, 1),
/*249*/	SET_LEASH_WORKS("bool enable, ObjectFloat creature"),
/*250*/	LOAD_MY_CREATURE("Coord position"),
/*251*/	OBJECT_RELATIVE_BELIEF("ObjectFloat object, float player, float belief"),
/*252*/	CREATE_WITH_ANGLE_AND_SCALE(7, 1),
/*253*/	SET_HELP_SYSTEM("bool enable"),	//Never found
/*254*/	SET_VIRTUAL_INFLUENCE("bool enable, float player"),
/*255*/	SET_ACTIVE("bool active, ObjectFloat object"),
/*256*/	THING_VALID("Object object", "bool"),
/*257*/	VORTEX_FADE_OUT("ObjectFloat vortex"),
/*258*/	REMOVE_REACTION_OF_TYPE("ObjectFloat object, REACTION reaction"),
/*259*/	CREATURE_LEARN_EVERYTHING_EXCLUDING(2),
/*260*/	PLAYED_PERCENTAGE(1, 1),
/*261*/	OBJECT_CAST_BY_OBJECT("ObjectFloat spell, ObjectFloat caster", "bool"),	//Never found
/*262*/	IS_WIND_MAGIC_AT_POS(1, 1),
/*263*/	CREATE_MIST(9, 1),
/*264*/	SET_MIST_FADE(6),
/*265*/	GET_OBJECT_FADE(1, 1),
/*266*/	PLAY_HAND_DEMO("StrPtr string, bool withPause, bool withoutHandModify"),
/*267*/	IS_PLAYING_HAND_DEMO("", "bool"),
/*268*/	GET_ARSE_POSITION(1, "Coord"),
/*269*/	IS_LEASHED_TO_OBJECT(2, 1),
/*270*/	GET_INTERACTION_MAGNITUDE(1, 1),
/*271*/	IS_CREATURE_AVAILABLE(1, 1),
/*272*/	CREATE_HIGHLIGHT(5, 1),
/*273*/	GET_OBJECT_HELD2(1, 1),
/*274*/	GET_ACTION_COUNT(2, 1),
/*275*/	GET_OBJECT_LEASH_TYPE(1, 1),
/*276*/	SET_FOCUS_FOLLOW("ObjectFloat target"),
/*277*/	SET_POSITION_FOLLOW("ObjectFloat target"),
/*278*/	SET_FOCUS_AND_POSITION_FOLLOW("ObjectFloat target, float distance"),
/*279*/	SET_CAMERA_LENS(1),
/*280*/	MOVE_CAMERA_LENS("float lens, float time"),
/*281*/	CREATURE_REACTION(2),				//Never found
/*282*/	CREATURE_IN_DEV_SCRIPT("bool enable, ObjectFloat creature"),
/*283*/	STORE_CAMERA_DETAILS(),				//Never found
/*284*/	RESTORE_CAMERA_DETAILS(),			//Never found
/*285*/	START_ANGLE_SOUND1("bool enable"),
/*286*/	SET_CAMERA_POS_FOC_LENS(7),			//Never found
/*287*/	MOVE_CAMERA_POS_FOC_LENS(8),		//Never found
/*288*/	GAME_TIME_ON_OFF("bool enable"),
/*289*/	MOVE_GAME_TIME("float hourOfTheDay, float duration"),
/*290*/	SET_HIGH_GRAPHICS_DETAIL("bool enable, ObjectFloat object"),
/*291*/	SET_SKELETON("bool enable, ObjectFloat object"),
/*292*/	IS_SKELETON("ObjectFloat object", "bool"),	//Never found
/*293*/	PLAYER_SPELL_CAST_TIME(1, 1),
/*294*/	PLAYER_SPELL_LAST_CAST(1, 1),
/*295*/	GET_LAST_SPELL_CAST_POS(1, "Coord"),
/*296*/	ADD_SPOT_VISUAL_TARGET_POS("ObjectFloat object, Coord position"),
/*297*/	ADD_SPOT_VISUAL_TARGET_OBJECT("ObjectFloat object, ObjectFloat target"),
/*298*/	SET_INDESTRUCTABLE("bool indestructible, ObjectFloat object"),
/*299*/	SET_GRAPHICS_CLIPPING(2),			//Never found
/*300*/	SPIRIT_APPEAR("HELP_SPIRIT_TYPE spirit"),
/*301*/	SPIRIT_DISAPPEAR("HELP_SPIRIT_TYPE spirit"),
/*302*/	SET_FOCUS_ON_OBJECT("ObjectFloat object, ObjectFloat target"),
/*303*/	RELEASE_OBJECT_FOCUS(1),
/*304*/	IMMERSION_EXISTS("", "bool"),
/*305*/	SET_DRAW_LEASH("bool enable"),
/*306*/	SET_DRAW_HIGHLIGHT("bool enable"),
/*307*/	SET_OPEN_CLOSE("bool open, ObjectFloat object"),
/*308*/	SET_INTRO_BUILDING("bool enable"),	//Never found
/*309*/	CREATURE_FORCE_FRIENDS("bool enable, ObjectFloat creature, ObjectFloat targetCreature"),
/*310*/	MOVE_COMPUTER_PLAYER_POSITION("float player, Coord position, float speed, bool withFixedHeight"),
/*311*/	ENABLE_DISABLE_COMPUTER_PLAYER1("bool enable, float player"),
/*312*/	GET_COMPUTER_PLAYER_POSITION(1, "Coord"),
/*313*/	SET_COMPUTER_PLAYER_POSITION("float player, Coord position, bool alwaysFalse"),
/*314*/	GET_STORED_CAMERA_POSITION(0, "Coord"),
/*315*/	GET_STORED_CAMERA_FOCUS(0, "Coord"),
/*316*/	CALL_NEAR_IN_STATE(8, 1),
/*317*/	SET_CREATURE_SOUND("bool enable"),
/*318*/	CREATURE_INTERACTING_WITH(2, 1),
/*319*/	SET_SUN_DRAW(1),
/*320*/	OBJECT_INFO_BITS(1, 1),
/*321*/	SET_HURT_BY_FIRE("bool enable, ObjectFloat object"),
/*322*/	CONFINED_OBJECT(5),					//Never found
/*323*/	CLEAR_CONFINED_OBJECT(1),
/*324*/	GET_OBJECT_FLOCK(1, 1),
/*325*/	SET_PLAYER_BELIEF("ObjectFloat object, float player, float belief"),
/*326*/	PLAY_JC_SPECIAL("int feature"),
/*327*/	IS_PLAYING_JC_SPECIAL(1, 1),
/*328*/	VORTEX_PARAMETERS(8),
/*329*/	LOAD_CREATURE("CREATURE_TYPE type, StrPtr mindFilename, float player, Coord position"),
/*330*/	IS_SPELL_CHARGING(1, 1),
/*331*/	IS_THAT_SPELL_CHARGING(2, 1),
/*332*/	OPPOSING_CREATURE(1, 1),
/*333*/	FLOCK_WITHIN_LIMITS("ObjectFloat object", "bool"),	//Never found
/*334*/	HIGHLIGHT_PROPERTIES("ObjectFloat object, int text, int category"),
/*335*/	LAST_MUSIC_LINE(1, 1),
/*336*/	HAND_DEMO_TRIGGER(0, "bool"),
/*337*/	GET_BELLY_POSITION(1, "Coord"),
/*338*/	SET_CREATURE_CREED_PROPERTIES("ObjectFloat creature, HAND_GLOW handGlow, float scale, float power, float time"),
/*339*/	GAME_THING_CAN_VIEW_CAMERA("ObjectFloat object, float degrees", "bool"),
/*340*/	GAME_PLAY_SAY_SOUND_EFFECT(6),
/*341*/	SET_TOWN_DESIRE_BOOST("ObjectFloat object, TOWN_DESIRE_INFO desire, float boost"),
/*342*/	IS_LOCKED_INTERACTION("ObjectFloat object", "bool"),
/*343*/	SET_CREATURE_NAME("ObjectFloat creature, int textID"),
/*344*/	COMPUTER_PLAYER_READY(1, 1),
/*345*/	ENABLE_DISABLE_COMPUTER_PLAYER2("bool pause, float player"),
/*346*/	CLEAR_ACTOR_MIND(1),
/*347*/	ENTER_EXIT_CITADEL(1),				//Never found; guess (bool)
/*348*/	START_ANGLE_SOUND2("bool enable"),
/*349*/	THING_JC_SPECIAL(3),
/*350*/	MUSIC_PLAYED2(1, 1),				//Never found
/*351*/	UPDATE_SNAPSHOT_PICTURE("Coord position, Coord focus, float success, float alignment, int titleStrID, bool takingPicture, int challengeID"),
/*352*/	STOP_SCRIPTS_IN_FILES_EXCLUDING("StrPtr sourceFilename, StrPtr scriptName"),
/*353*/	CREATE_RANDOM_VILLAGER_OF_TRIBE(4, 1),
/*354*/	TOGGLE_LEASH("int player"),
/*355*/	GAME_SET_MANA("ObjectFloat object, float mana"),
/*356*/	SET_MAGIC_PROPERTIES("ObjectFloat object, MAGIC_TYPE magicType, float duration"),
/*357*/	SET_GAME_SOUND("bool enable"),
/*358*/	SEX_IS_MALE(1, 1),
/*359*/	GET_FIRST_HELP(1, 1),
/*360*/	GET_LAST_HELP(1, 1),
/*361*/	IS_ACTIVE("ObjectFloat object", "bool"),
/*362*/	SET_BOOKMARK_POSITION(4),			//Never found
/*363*/	SET_SCAFFOLD_PROPERTIES("ObjectFloat object, ABODE_NUMBER type, float size, bool destroy"),
/*364*/	SET_COMPUTER_PLAYER_PERSONALITY("float player, StrPtr aspect, float probability"),
/*365*/	SET_COMPUTER_PLAYER_SUPPRESSION(3),	//Never found; guess (float player, StrPtr aspect, float probability)
/*366*/	FORCE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, ObjectFloat obj1, ObjectFloat obj2"),
/*367*/	QUEUE_COMPUTER_PLAYER_ACTION("float player, StrPtr action, ObjectFloat obj1, ObjectFloat obj2"),
/*368*/	GET_TOWN_WITH_ID(1, 1),
/*369*/	SET_DISCIPLE("ObjectFloat object, VILLAGER_DISCIPLE discipleType, bool withSound"),
/*370*/	RELEASE_COMPUTER_PLAYER("float player"),
/*371*/	SET_COMPUTER_PLAYER_SPEED("float player, float speed"),
/*372*/	SET_FOCUS_FOLLOW_COMPUTER_PLAYER("float player"),	//Never found
/*373*/	SET_POSITION_FOLLOW_COMPUTER_PLAYER("float player"),	//Never found
/*374*/	CALL_COMPUTER_PLAYER(1, 1),
/*375*/	CALL_BUILDING_IN_TOWN(4, 1),
/*376*/	SET_CAN_BUILD_WORSHIPSITE("bool enable, ObjectFloat object"),
/*377*/	GET_FACING_CAMERA_POSITION(1, "Coord"),
/*378*/	SET_COMPUTER_PLAYER_ATTITUDE("float player1, float player2, float attitude"),	//Attitude in range [-1, 1]; -1=nice, 1=reactive
/*379*/	GET_COMPUTER_PLAYER_ATTITUDE(2, 1),
/*380*/	LOAD_COMPUTER_PLAYER_PERSONALITY(2),	//Never found; guess (float player, StrPtr filename)
/*381*/	SAVE_COMPUTER_PLAYER_PERSONALITY(2),	//Never found; guess (float player, StrPtr filename)
/*382*/	SET_PLAYER_ALLY("float player1, float player2, float percentage"),
/*383*/	CALL_FLYING(6, 1),
/*384*/	SET_OBJECT_FADE_IN("ObjectFloat object, float time"),
/*385*/	IS_AFFECTED_BY_SPELL(2, 1),
/*386*/	SET_MAGIC_IN_OBJECT("bool enable, int spell, ObjectFloat object"),
/*387*/	ID_ADULT_SIZE(1, 1),
/*388*/	OBJECT_CAPACITY(1, 1),
/*389*/	OBJECT_ADULT_CAPACITY(1, 1),
/*390*/	SET_CREATURE_AUTO_FIGHTING("bool enable, ObjectFloat creature"),
/*391*/	IS_AUTO_FIGHTING(1, 1),
/*392*/	SET_CREATURE_QUEUE_FIGHT_MOVE("ObjectFloat creature, FIGHT_MOVE move"),
/*393*/	SET_CREATURE_QUEUE_FIGHT_SPELL(2),	//Never found; guess (ObjectFloat creature, ENUM_FIGHT_SPELLS spell)
/*394*/	SET_CREATURE_QUEUE_FIGHT_STEP(2),	//Never found; guess (ObjectFloat creature, ENUM_FIGHT_STEPS step)
/*395*/	GET_CREATURE_FIGHT_ACTION(1, 1),
/*396*/	CREATURE_FIGHT_QUEUE_HITS(1, 1),
/*397*/	SQUARE_ROOT(1, 1),
/*398*/	GET_PLAYER_ALLY(2, 1),
/*399*/	SET_PLAYER_WIND_RESISTANCE(2, 1),
/*400*/	GET_PLAYER_WIND_RESISTANCE(2, 1),
/*401*/	PAUSE_UNPAUSE_CLIMATE_SYSTEM("bool enable"),
/*402*/	PAUSE_UNPAUSE_STORM_CREATION_IN_CLIMATE_SYSTEM("bool enable"),	//Never found
/*403*/	GET_MANA_FOR_SPELL(1, 1),
/*404*/	KILL_STORMS_IN_AREA("Coord position, float radius"),
/*405*/	INSIDE_TEMPLE("", "bool"),
/*406*/	RESTART_OBJECT("ObjectFloat object"),	//Never found
/*407*/	SET_GAME_TIME_PROPERTIES(3),		//Never found
/*408*/	RESET_GAME_TIME_PROPERTIES(),
/*409*/	SOUND_EXISTS("", "bool"),			//Never found
/*410*/	GET_TOWN_WORSHIP_DEATHS(1, 1),
/*411*/	GAME_CLEAR_DIALOGUE(),
/*412*/	GAME_CLOSE_DIALOGUE(),
/*413*/	GET_HAND_STATE(0, 1),
/*414*/	SET_INTERFACE_CITADEL("bool enable"),
/*415*/	MAP_SCRIPT_FUNCTION("StrPtr command"),
/*416*/	WITHIN_ROTATION(0, "bool"),
/*417*/	GET_PLAYER_TOWN_TOTAL(1, 1),
/*418*/	SPIRIT_SCREEN_POINT(3),
/*419*/	KEY_DOWN("int key", "bool"),
/*420*/	SET_FIGHT_EXIT(1),					//Never found; guess (bool enable)
/*421*/	GET_OBJECT_CLICKED(0, 1),
/*422*/	GET_MANA(1, 1),
/*423*/	CLEAR_PLAYER_SPELL_CHARGING("float player"),
/*424*/	STOP_SOUND_EFFECT(3),
/*425*/	GET_TOTEM_STATUE(1, 1),
/*426*/	SET_SET_ON_FIRE("bool enable, ObjectFloat object"),
/*427*/	SET_LAND_BALANCE(2),				//Never found
/*428*/	SET_OBJECT_BELIEF_SCALE(2),			//Never found
/*429*/	START_IMMERSION("IMMERSION_EFFECT_TYPE effect"),
/*430*/	STOP_IMMERSION("IMMERSION_EFFECT_TYPE effect"),	//Never found
/*431*/	STOP_ALL_IMMERSION(),				//Never found
/*432*/	SET_CREATURE_IN_TEMPLE("bool enable"),
/*433*/	GAME_DRAW_TEXT("int textID, float across, float down, float width, float height, float size, float fade"),
/*434*/	GAME_DRAW_TEMP_TEXT("StrPtr string, float across, float down, float width, float height, float size, float fade"),
/*435*/	FADE_ALL_DRAW_TEXT("float time"),
/*436*/	SET_DRAW_TEXT_COLOUR("float red, float green, float blue"),
/*437*/	SET_CLIPPING_WINDOW("float across, float down, float width, float height, float time"),
/*438*/	CLEAR_CLIPPING_WINDOW("float time"),
/*439*/	SAVE_GAME_IN_SLOT("int slot"),		//Never found
/*440*/	SET_OBJECT_CARRYING("ObjectFloat object, CARRIED_OBJECT carriedObj"),
/*441*/	POS_VALID_FOR_CREATURE("Coord position", "bool"),	//Never found
/*442*/	GET_TIME_SINCE_OBJECT_ATTACKED(2, 1),
/*443*/	GET_TOWN_AND_VILLAGER_HEALTH_TOTAL(1, 1),
/*444*/	GAME_ADD_FOR_BUILDING(2),			//Never found
/*445*/	ENABLE_DISABLE_ALIGNMENT_MUSIC("bool enable"),
/*446*/	GET_DEAD_LIVING(4, 1),
/*447*/	ATTACH_SOUND_TAG(4),
/*448*/	DETACH_SOUND_TAG(3),
/*449*/	GET_SACRIFICE_TOTAL(1, 1),
/*450*/	GAME_SOUND_PLAYING(2, 1),
/*451*/	GET_TEMPLE_POSITION(1, "Coord"),
/*452*/	CREATURE_AUTOSCALE(3),
/*453*/	GET_SPELL_ICON_IN_TEMPLE(2, 1),
/*454*/	GAME_CLEAR_COMPUTER_PLAYER_ACTIONS(1),
/*455*/	GET_FIRST_IN_CONTAINER(1, 1),
/*456*/	GET_NEXT_IN_CONTAINER(2, 1),
/*457*/	GET_TEMPLE_ENTRANCE_POSITION(3, "Coord"),
/*458*/	SAY_SOUND_EFFECT_PLAYING(2, 1),
/*459*/	SET_HAND_DEMO_KEYS(1),
/*460*/	CAN_SKIP_TUTORIAL("", "bool"),
/*461*/	CAN_SKIP_CREATURE_TRAINING("", "bool"),
/*462*/	IS_KEEPING_OLD_CREATURE("", "bool"),
/*463*/	CURRENT_PROFILE_HAS_CREATURE("", "bool");
	
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
		OBJECT("Object"),
		OBJECT_FLOAT("ObjectFloat"),
		OBJECT_INT("ObjectInt"),
		INT_OR_FLOAT("int|float"),
		STRPTR("StrPtr"),				//int (byte offset in data section)
		
		/* The following are enums. Be aware that many enum values defined in .h files are obsolete,
		 * .txt files are more up to date. TODO: check all required values and write our own version.
		 */
		SCRIPT_OBJECT_TYPE(),			//defined in ScriptEnums.h
		SCRIPT_OBJECT_SUBTYPE(),		//various enums defined in info2.txt Enum.h
		SCRIPT_OBJECT_PROPERTY_TYPE(),	//defined in ScriptEnums.h
		SCRIPT_BOOL(),					//defined in ScriptEnums.h
		SCRIPT_INTERFACE_LEVEL(),		//defined in ScriptEnums.h
		HELP_SPIRIT_TYPE(),				//defined in Enum.h
		VILLAGER_DISCIPLE(),			//defined in Enum.h
		CREATURE_DESIRES(),				//defined in Enum.h
		CREATURE_ACTION(),				//defined in CreatureEnum.h
		DEVELOPMENT_PHASE(),			//defined in CreatureEnum.h
		CREATURE_ACTION_TYPE(),			//see enums.txt
		CREATURE_ACTION_SUBTYPE(),		//various enums defined in Enum.h
		MAGIC_TYPE(),					//defined in Enum.h
		HAND_GLOW(),					//see enums.txt
		FIGHT_MOVE(),					//see enums.txt
		CREATURE_TYPE(),				//defined in CreatureEnum.h
		TOWN_DESIRE_INFO(),				//defined in Enum.h
		IMMERSION_EFFECT_TYPE(),		//defined in Enum.h
		CARRIED_OBJECT(),				//defined in Enum.h
		REACTION(),						//defined in Enum.h
		ABODE_NUMBER(),					//defined in Enum.h
		DETAIL_ANIM_TYPES();			//defined in info1.txt (alias of ANIM_LIST in AllMeshes.h)
		
		private static final Map<String, ArgType> map = new HashMap<>();
		
		static {
			for (ArgType t : values()) {
				map.put(t.keyword, t);
			}
		}
		
		public final String keyword;
		public final int stackCount;
		
		private ArgType() {
			this.keyword = this.name();
			this.stackCount = 1;
		}
		
		private ArgType(String keyword) {
			this.keyword = keyword;
			this.stackCount = 1;
		}
		
		private ArgType(String keyword, int stackCount) {
			this.keyword = keyword;
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

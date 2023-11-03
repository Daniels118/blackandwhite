//LHVM Challenge ASM version 7

DATA
//0x00000000
string c0 = "Compiled with CHL Compiler developed by Daniele Lombardi"
string c57 = "StandardReminder"
string c74 = "BaywatchSwimmingBoy"
string c94 = "BaywatchSwimmingLaugh"

GLOBALS
global ArkSailed
global ChooseYourCreatureFinished
global WonderBuilt
global AztecVillageRetaken
global VillagerCatchCompleted
global VillagerScore
global TheMissionariesTimeOutDetected
global SingingStoneRock1
global MyCreature
global Guide
global VortexOpen
global PlayerCreatureType
global LethysKilled
global OpenVortexL1
global CreatureDevelopment_Fight
global GlobalCurrentCreature
global GlobalOldCreature
global BreederRunning
global ChallengeHighlightEndVariable
global IsSkippingToCreatureSelect
global IsSkippingCreatureGuide
global IsKeepingOldCreature
global LeaveLandNow
global help_text_shaolin_quest_reminder_02
global help_text_first_meteorite_quest_reminder_02
global help_text_meteorite_ogre_quest_reminder_02
global help_text_meteorite_nomad_quest_reminder_02
global help_text_retake_aztec_village_quest_reminder_02
global BaywatchHomeCounter
global BaywatchSwimmingCounter
global BaywatchMurder

SCRIPTS

source test.chl

global BaywatchMurder


//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchSwimmingLaugh(SwimPos)
	Local Amount
loop_8:
	EXCEPT exception_handler_0
	POPF SwimPos
//@	Amount=0		//#test.chl:9
	PUSHF 0.0
	POPF Amount

//@start		//#test.chl:11
	FREE
//
	EXCEPT exception_handler_1
//@	while BaywatchSwimmingCounter >= 1		//#test.chl:12
loop_4:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	GEQ
	JZ skip_2
//@		Amount = 250/(BaywatchSwimmingCounter)		//#test.chl:13
	PUSHF [Amount]
	POPI
	PUSHF 250.0
	PUSHF [BaywatchSwimmingCounter]
	DIV
	POPF Amount
//@		if number from 0 to Amount == 0		//#test.chl:14
	PUSHF 0.0
	PUSHF [Amount]
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	PUSHF 0.0
	EQ
	JZ skip_3
//@			start sound constant from LH_SCRIPT_SAMPLE_CHILD_LAUGH_01 to LH_SCRIPT_SAMPLE_CHILD_LAUGH_07 AUDIO_SFX_BANK_TYPE_SCRIPT_SFX at [SwimPos]		//#test.chl:15
	PUSHI 31
	PUSHI 37
	SYS2 RANDOM_ULONG	//[2, 1] (int min, int max) returns (int)
	PUSHI 5
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHB true
	SYS PLAY_SOUND_EFFECT	//[6, 0] (int sound, AUDIO_SFX_BANK_TYPE soundbank, Coord position, bool withPosition)
skip_3:
//@	end while		//#test.chl:17
	JMP loop_4
skip_2:
	ENDEXCEPT
	JMP skip_5
exception_handler_1:
	ITEREXCEPT
skip_5:
//@end script BaywatchSwimmingLaugh		//#test.chl:18
	ENDEXCEPT
	JMP skip_6
exception_handler_0:
	ITEREXCEPT
skip_6:
	END	//BaywatchSwimmingLaugh

global BaywatchMurder



//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchSwimmingBoy(Boy, VillagerHutPos)
	Local Murdered
	Local InWater
	Local LookAt
	Local Random
	EXCEPT exception_handler_7
	POPF Boy
	POPF VillagerHutPos

//@	Murdered = 0		//#test.chl:24
	PUSHF 0.0
	POPF Murdered
//@	InWater = 1		//#test.chl:25
	PUSHF 1.0
	POPF InWater
//@	LookAt = marker at [2962.9587, 0.0000, 3079.3020]		//#test.chl:26
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2962.9587
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3079.302
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF LookAt
//@	Random = 0		//#test.chl:27
	PUSHF 0.0
	POPF Random

//@start		//#test.chl:29
	FREE

//@	AGE of Boy = 11		//#test.chl:31
	PUSHI 17
	PUSHF [Boy]
	PUSHI 17
	PUSHF [Boy]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 11.0
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)
//@	Random = number from 1 to 4		//#test.chl:32
	PUSHF [Random]
	POPI
	PUSHF 1.0
	PUSHF 4.0
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	POPF Random
//@	wait Random seconds		//#test.chl:33
	PUSHF [Random]
	SLEEP
	JZ loop_8
//@	Boy play ANM_P_SWIM2 loop -1		//#test.chl:34
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 420
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@	set Boy focus to [LookAt]		//#test.chl:35
	PUSHF [Boy]
	PUSHF [LookAt]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//
	EXCEPT exception_handler_9

//@	while [Boy] not near [VillagerHutPos] radius 5 and Murdered == 0		//#test.chl:37
loop_16:
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_DISTANCE	//[6, 1] (Coord p0, Coord p1) returns (float)
	PUSHF 5.0
	LT
	NOT
	PUSHF [Murdered]
	PUSHF 0.0
	EQ
	AND
	JZ skip_10

//@		if Boy is HELD or Boy in MyCreature hand		//#test.chl:39
	PUSHF [Boy]
	PUSHI 9
	SWAPI
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	CASTB
	PUSHF [Boy]
	PUSHF [MyCreature]
	SYS IN_CREATURE_HAND	//[2, 1] (Object obj, Object creature) returns (bool)
	OR
	JZ skip_11
//@			if InWater == 1		//#test.chl:40
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ skip_12
//@				BaywatchSwimmingCounter--		//#test.chl:41
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	SUBF
	POPF BaywatchSwimmingCounter
skip_12:
//@			InWater=0		//#test.chl:43
	PUSHF [InWater]
	POPI
	PUSHF 0.0
	POPF InWater
//@			wait until (Boy is not HELD and not Boy in MyCreature hand) and Boy is not FLYING		//#test.chl:44
	PUSHF [Boy]
	PUSHI 9
	SWAPI
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	CASTB
	NOT
	PUSHF [Boy]
	PUSHF [MyCreature]
	SYS IN_CREATURE_HAND	//[2, 1] (Object obj, Object creature) returns (bool)
	NOT
	AND
	PUSHF [Boy]
	PUSHI 5
	SWAPI
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	CASTB
	NOT
	AND
	JZ loop_8

//@			if HEALTH of Boy > 0 and (Boy is not HELD and not Boy in MyCreature hand)		//#test.chl:46
	PUSHI 1
	PUSHF [Boy]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	PUSHF [Boy]
	PUSHI 9
	SWAPI
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	CASTB
	NOT
	PUSHF [Boy]
	PUSHF [MyCreature]
	SYS IN_CREATURE_HAND	//[2, 1] (Object obj, Object creature) returns (bool)
	NOT
	AND
	AND
	JZ skip_11
//@				if land height at [Boy] > 0			//#test.chl:47
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_LAND_HEIGHT	//[3, 1] (Coord position) returns (float)
	PUSHF 0.0
	GT
	JZ skip_13
//@					Boy play ANM_P_CROWD_LOST_2 loop 1		//#test.chl:48
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 260
	PUSHF 1.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@					begin dialogue		//#test.chl:50
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
//@						say single line HELP_TEXT_BAYWATCH_25		//#test.chl:51
	PUSHB true
	PUSHI 1595
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@						wait until read		//#test.chl:52
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8
//@					end dialogue		//#test.chl:53
	SYS END_DIALOGUE	//[0, 0] ()

//@					wait until Boy played		//#test.chl:55
	PUSHF [Boy]
	SYS PLAYED	//[1, 1] (Object obj) returns (bool)
	JZ loop_8
//@					SPEED of Boy = 0.5		//#test.chl:56
	PUSHI 12
	PUSHF [Boy]
	PUSHI 12
	PUSHF [Boy]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.5
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)

//@					move Boy position to [VillagerHutPos]		//#test.chl:58
	PUSHF [Boy]
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)
					
//@				elsif land height at [Boy] <= 0		//#test.chl:60
	JMP skip_11
skip_13:
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_LAND_HEIGHT	//[3, 1] (Coord position) returns (float)
	PUSHF 0.0
	LEQ
	JZ skip_11
//@					set Boy position to [Boy]		//#test.chl:61
	PUSHF [Boy]
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					Boy play ANM_P_SWIM2 loop -1		//#test.chl:62
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 420
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					BaywatchSwimmingCounter++		//#test.chl:63
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	ADDF
	POPF BaywatchSwimmingCounter
//@					InWater=1		//#test.chl:64
	PUSHF [InWater]
	POPI
	PUSHF 1.0
	POPF InWater
skip_11:

//@		if HEALTH of Boy <= 0		//#test.chl:69
	PUSHI 1
	PUSHF [Boy]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ skip_14
//@			if BaywatchMurder == 0		//#test.chl:70
	PUSHF [BaywatchMurder]
	PUSHF 0.0
	EQ
	JZ skip_15
//@				begin dialogue		//#test.chl:71
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
//@					eject evil spirit		//#test.chl:72
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@					say HELP_TEXT_BAYWATCH_31		//#test.chl:73
	PUSHB false
	PUSHI 1601
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.chl:74
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@					eject good spirit		//#test.chl:76
	PUSHI 1
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@					say HELP_TEXT_BAYWATCH_32		//#test.chl:77
	PUSHB false
	PUSHI 1602
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.chl:78
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				end dialogue		//#test.chl:80
	SYS END_DIALOGUE	//[0, 0] ()
skip_15:

//@			BaywatchMurder ++		//#test.chl:83
	PUSHF [BaywatchMurder]
	PUSHF 1.0
	ADDF
	POPF BaywatchMurder
//@			Murdered = 1		//#test.chl:84
	PUSHF [Murdered]
	POPI
	PUSHF 1.0
	POPF Murdered
//@			if InWater == 1		//#test.chl:85
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ skip_14
//@				BaywatchSwimmingCounter--		//#test.chl:86
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	SUBF
	POPF BaywatchSwimmingCounter
skip_14:

//@	end while		//#test.chl:90
	JMP loop_16
skip_10:
	ENDEXCEPT
	JMP skip_17
exception_handler_9:
	ITEREXCEPT
skip_17:

//@	if Murdered == 0		//#test.chl:92
	PUSHF [Murdered]
	PUSHF 0.0
	EQ
	JZ skip_18
//@		BaywatchHomeCounter ++		//#test.chl:93
	PUSHF [BaywatchHomeCounter]
	PUSHF 1.0
	ADDF
	POPF BaywatchHomeCounter
skip_18:

//@end script BaywatchSwimmingBoy		//#test.chl:96
	ENDEXCEPT
	JMP skip_19
exception_handler_7:
	ITEREXCEPT
skip_19:
	END	//BaywatchSwimmingBoy

global BaywatchMurder


//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchMain
	Local VillagerHutPos
	Local SwimPos
	Local SwimPos2
	Local SwimPos3
	Local SwimPos4
	Local SwimPos5
	Local ShorePos
	Local Mother
	Local MotherDead
	Local Influence
	Local Highlight
	Local BaywatchReward
	Local BaywatchFinished
	Local SoapBox
	Local Fun
	Local Boy1
	Local Boy2
	Local Boy3
	Local Boy4
	Local Boy5
	Local RewardPos
	Local RewardCameraPos
	Local RewardCameraFoc
	EXCEPT exception_handler_20
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2781.1104
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3053.49
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF VillagerHutPos
//@	SwimPos = marker at [2964.223, 0.000, 3084.874]		//#test.chl:102
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2964.2229
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.874
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos
//@	SwimPos2 = marker at [2968.614, 0.000, 3089.957]		//#test.chl:103
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2968.614
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3089.957
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos2
//@	SwimPos3 = marker at [2969.778, 0.000, 3082.155]		//#test.chl:104
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2969.7781
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3082.155
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos3
//@	SwimPos4 = marker at [2962.422, 0.000, 3078.884]  		//#test.chl:105
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2962.4221
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3078.884
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos4
//@	SwimPos5 = marker at [2960.591, 0.000, 3084.722]		//#test.chl:106
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2960.5911
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.7219
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos5


//@	ShorePos = marker at [2943.603, 0.000, 3095.944]		//#test.chl:109
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2943.603
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3095.9441
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF ShorePos
//@	Mother = 0		//#test.chl:110
	PUSHF 0.0
	POPF Mother
//@	MotherDead = 0		//#test.chl:111
	PUSHF 0.0
	POPF MotherDead
//@	Influence = 0		//#test.chl:112
	PUSHF 0.0
	POPF Influence
//@	Highlight = 0		//#test.chl:113
	PUSHF 0.0
	POPF Highlight
//@	BaywatchReward = 0		//#test.chl:114
	PUSHF 0.0
	POPF BaywatchReward
//@	BaywatchFinished = 0		//#test.chl:115
	PUSHF 0.0
	POPF BaywatchFinished
//@	SoapBox = marker at [2786.5073, 50.9200, 3064.3250]		//#test.chl:116
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2786.5073
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3064.325
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SoapBox
//@	Fun = 0		//#test.chl:117
	PUSHF 0.0
	POPF Fun

//@	Boy1 = 0		//#test.chl:119
	PUSHF 0.0
	POPF Boy1
//@	Boy2 = 0		//#test.chl:120
	PUSHF 0.0
	POPF Boy2
//@	Boy3 = 0		//#test.chl:121
	PUSHF 0.0
	POPF Boy3
//@	Boy4 = 0		//#test.chl:122
	PUSHF 0.0
	POPF Boy4
//@	Boy5 = 0 		//#test.chl:123
	PUSHF 0.0
	POPF Boy5

//@	RewardPos = marker at [2854.7266, 50.3966, 3056.9429]		//#test.chl:125
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2854.7266
	CASTC
	PUSHF 50.396599
	CASTC
	PUSHF 3056.9429
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardPos
//@	RewardCameraPos = marker at [2825.0017, 66.7546, 3067.1838]		//#test.chl:126
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2825.0017
	CASTC
	PUSHF 66.754601
	CASTC
	PUSHF 3067.1838
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardCameraPos
//@	RewardCameraFoc = marker at [2854.1843, 50.8918, 3054.0120]		//#test.chl:127
	PUSHI [ArkSailed]
	PUSHI 0
	PUSHF 2854.1843
	CASTC
	PUSHF 50.8918
	CASTC
	PUSHF 3054.012
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardCameraFoc

//@start		//#test.chl:129
	FREE
//@	BaywatchSwimmingCounter=5		//#test.chl:130
	PUSHF [BaywatchSwimmingCounter]
	POPI
	PUSHF 5.0
	POPF BaywatchSwimmingCounter

//@	Influence=create anti influence at position [SwimPos] radius 30		//#test.chl:132
	PUSHF [Influence]
	POPI
	PUSHI 1
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 30.0
	SYS INFLUENCE_POSITION	//[6, 1] (Coord position, float radius, int zero, int anti) returns (Object)
	POPF Influence

//@	Highlight = create highlight HIGHLIGHT_CHALLENGE at [VillagerHutPos]		//#test.chl:134
	PUSHF [Highlight]
	POPI
	PUSHI 1
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE_HIGHLIGHT	//[5, 1] (HIGHLIGHT_INFO type, Coord position, int challengeID) returns (Object)
	POPF Highlight
//@	run script ChallengeHighlightNotify(Highlight, VillagerHutPos, variable EVIL_ADVISOR, variable HELP_TEXT_GENERAL_CHALLENGE_START_03)		//#test.chl:135
	PUSHF [Highlight]
	PUSHF [VillagerHutPos]
	PUSHI 2
	CASTF
	PUSHI 2700
	CASTF
	CALL BaywatchSwimmingLaugh

//@	Mother = create VILLAGER VILLAGER_INFO_INDIAN_HOUSEWIFE_FEMALE at [VillagerHutPos]		//#test.chl:137
	PUSHF [Mother]
	POPI
	PUSHI 4
	PUSHI 28
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Mother

//@	BaywatchHomeCounter = 0		//#test.chl:139
	PUSHF [BaywatchHomeCounter]
	POPI
	PUSHF 0.0
	POPF BaywatchHomeCounter

//@	Boy1 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos]		//#test.chl:141
	PUSHF [Boy1]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy1
//@	Boy2 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos2]		//#test.chl:142
	PUSHF [Boy2]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos2]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy2
//@	Boy3 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos3]		//#test.chl:143
	PUSHF [Boy3]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos3]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy3
//@	Boy4 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos4]		//#test.chl:144
	PUSHF [Boy4]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos4]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy4
//@	Boy5 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos5]		//#test.chl:145
	PUSHF [Boy5]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos5]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy5

//@	run background script BaywatchSwimmingBoy(Boy1, VillagerHutPos)		//#test.chl:147
	PUSHF [Boy1]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy2, VillagerHutPos)		//#test.chl:148
	PUSHF [Boy2]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy3, VillagerHutPos)		//#test.chl:149
	PUSHF [Boy3]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy4, VillagerHutPos)		//#test.chl:150
	PUSHF [Boy4]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy5, VillagerHutPos)		//#test.chl:151
	PUSHF [Boy5]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingLaugh(SwimPos)		//#test.chl:152
	PUSHF [SwimPos]
	START BaywatchSwimmingLaugh

//@	begin cinema		//#test.chl:154
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@		start music MUSIC_TYPE_SCRIPT_GENERIC_03		//#test.chl:155
	PUSHI 59
	SYS START_MUSIC	//[1, 0] (int music)
//@		enable Mother high gfx detail		//#test.chl:156
	PUSHB true
	PUSHF [Mother]
	SYS SET_HIGH_GRAPHICS_DETAIL	//[2, 0] (bool enable, Object object)

//@		move camera position to [2790.691, 53.522, 3069.138] time 3		//#test.chl:158
	PUSHF 2790.6909
	CASTC
	PUSHF 53.521999
	CASTC
	PUSHF 3069.1379
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2783.190, 50.229, 3058.410] time 3		//#test.chl:159
	PUSHF 2783.1899
	CASTC
	PUSHF 50.229
	CASTC
	PUSHF 3058.4099
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		SPEED of Mother = 0.5		//#test.chl:161
	PUSHI 12
	PUSHF [Mother]
	PUSHI 12
	PUSHF [Mother]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.5
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)
//@		move Mother position to [SoapBox]		//#test.chl:162
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)

//@		wait until camera ready		//#test.chl:164
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8

//@		Mother play ANM_P_LOOKING_FOR_SOMETHING loop 1		//#test.chl:166
	PUSHF [Mother]
	PUSHO [Mother]
	PUSHI 340
	PUSHF 1.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@		move camera position to [2787.182, 54.323, 3070.434] time 6		//#test.chl:168
	PUSHF 2787.1819
	CASTC
	PUSHF 54.323002
	CASTC
	PUSHF 3070.4341
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2785.620, 51.201, 3059.870] time 6		//#test.chl:169
	PUSHF 2785.6201
	CASTC
	PUSHF 51.201
	CASTC
	PUSHF 3059.8701
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		say single line HELP_TEXT_BAYWATCH_22		//#test.chl:171
	PUSHB true
	PUSHI 1592
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@		wait until read		//#test.chl:172
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@		close dialogue		//#test.chl:174
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@		wait 0.1 seconds		//#test.chl:175
	PUSHF 0.1
	SLEEP
	JZ loop_8

		// Cut to children swimming

//@		set camera position to [2960.404, 6.550, 3114.483] 		//#test.chl:179
	PUSHF 2960.4041
	CASTC
	PUSHF 6.5500002
	CASTC
	PUSHF 3114.4829
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@		set camera focus to [2958.053, 3.424, 3097.387] 		//#test.chl:180
	PUSHF 2958.053
	CASTC
	PUSHF 3.424
	CASTC
	PUSHF 3097.387
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@		snapshot challenge success 0.0 alignment 0 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:182
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 0.0
	PUSHF 0.0
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@		say single line HELP_TEXT_BAYWATCH_23		//#test.chl:184
	PUSHB true
	PUSHI 1593
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
		
//@		move camera position to [2965.097, 2.745, 3092.632] time 6		//#test.chl:186
	PUSHF 2965.0969
	CASTC
	PUSHF 2.7449999
	CASTC
	PUSHF 3092.6321
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2962.747, -0.381, 3075.535] time 6		//#test.chl:187
	PUSHF 2962.7471
	CASTC
	PUSHF 0.381
	NEG
	CASTC
	PUSHF 3075.5349
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)
//@		wait until read		//#test.chl:188
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@		wait until camera ready		//#test.chl:190
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8

		// Cut back to Mother

//@		set camera position to [2790.691, 53.522, 3069.138]		//#test.chl:194
	PUSHF 2790.6909
	CASTC
	PUSHF 53.521999
	CASTC
	PUSHF 3069.1379
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@		set camera focus to [2783.190, 50.229, 3058.410]		//#test.chl:195
	PUSHF 2783.1899
	CASTC
	PUSHF 50.229
	CASTC
	PUSHF 3058.4099
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@		SPEED of Mother = 0.2		//#test.chl:197
	PUSHI 12
	PUSHF [Mother]
	PUSHI 12
	PUSHF [Mother]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.2
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)

//@		move camera position to [2772.9670, 69.8213, 3093.4985]	time 4		//#test.chl:199
	PUSHF 2772.967
	CASTC
	PUSHF 69.821297
	CASTC
	PUSHF 3093.4985
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2787.7322, 51.4469, 3067.7637] time 3		//#test.chl:200
	PUSHF 2787.7322
	CASTC
	PUSHF 51.446899
	CASTC
	PUSHF 3067.7637
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		wait until camera ready		//#test.chl:202
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8

//@		disable Mother high gfx detail		//#test.chl:204
	PUSHB false
	PUSHF [Mother]
	SYS SET_HIGH_GRAPHICS_DETAIL	//[2, 0] (bool enable, Object object)
//@		stop music		//#test.chl:205
	SYS STOP_MUSIC	//[0, 0] ()
//@	end cinema		//#test.chl:206
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@	state Mother WANDER_AROUND		//#test.chl:208
	PUSHF [Mother]
	PUSHI 199
	PUSHI 39
//@		position [SoapBox]		//#test.chl:209
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHI 39
//@		float 6		//#test.chl:210
	PUSHF 6.0
	SYS SET_SCRIPT_FLOAT	//[2, 0] (ObjectInt object, float value)
	PUSHI 39
//@		ulong 4, 20		//#test.chl:211
	PUSHF 4.0
	CASTI
	PUSHF 20.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@	wait 5 seconds		//#test.chl:213
	PUSHF 5.0
	SLEEP
	JZ loop_8
//
	EXCEPT exception_handler_21

//@	while BaywatchFinished == 0		//#test.chl:215
loop_41:
	PUSHF [BaywatchFinished]
	PUSHF 0.0
	EQ
	JZ skip_22

//@		if BaywatchHomeCounter == 5	and HEALTH of Mother > 0		//#test.chl:217
	PUSHF [BaywatchHomeCounter]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ skip_23
//@			begin cinema		//#test.chl:218
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:219
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:220
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)
//@				say single line HELP_TEXT_BAYWATCH_27		//#test.chl:221
	PUSHB true
	PUSHI 1597
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:222
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8
//@				close dialogue		//#test.chl:223
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@				BaywatchFinished = 1		//#test.chl:224
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

				// Reward - Big Spell Dispenser
//@				run script GiveSpellDispenserReward(RewardPos, variable MAGIC_TYPE_CREATURE_SPELL_BIG, 180, 1, 0)		//#test.chl:227
	PUSHF [RewardPos]
	PUSHI 28
	CASTF
	PUSHF 180.0
	PUSHF 1.0
	PUSHF 0.0
	CALL BaywatchSwimmingLaugh
//@				move camera position to [RewardCameraPos] time 5		//#test.chl:228
	PUSHF [RewardCameraPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 5.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@				move camera focus to [RewardCameraFoc] time 2		//#test.chl:229
	PUSHF [RewardCameraFoc]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 2.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)
//@				wait until camera ready		//#test.chl:230
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8
//@				snapshot challenge success 1 alignment 1 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:231
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 1.0
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)
//@			end cinema 		//#test.chl:232
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother > 0		//#test.chl:234
	JMP skip_24
skip_23:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 0.0
	EQ
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	LT
	AND
	PUSHF [BaywatchHomeCounter]
	PUSHF 5.0
	PUSHF [BaywatchMurder]
	SUBF
	EQ
	AND
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ skip_25
//@			begin cinema		//#test.chl:235
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:236
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:237
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)
//@				update snapshot success 1 alignment -0.2 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:238
	PUSHF 1.0
	PUSHF 0.2
	NEG
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)
//@				say single line HELP_TEXT_BAYWATCH_28		//#test.chl:239
	PUSHB true
	PUSHI 1598
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:240
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				BaywatchFinished = 3		//#test.chl:242
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished
//@			end cinema		//#test.chl:243
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@		elsif BaywatchMurder == 5 and HEALTH of Mother > 0		//#test.chl:245
	JMP skip_24
skip_25:
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ skip_26

//@			begin cinema		//#test.chl:247
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:248
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:249
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@				update snapshot success 1 alignment -0.6 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:251
	PUSHF 1.0
	PUSHF 0.6
	NEG
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@				say	single line HELP_TEXT_BAYWATCH_29		//#test.chl:253
	PUSHB true
	PUSHI 1599
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:254
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				eject evil spirit		//#test.chl:256
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@				say single line HELP_TEXT_BAYWATCH_30		//#test.chl:257
	PUSHB true
	PUSHI 1600
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:258
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				BaywatchFinished = 2		//#test.chl:260
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 2.0
	POPF BaywatchFinished

//@			end cinema		//#test.chl:262
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@		elsif BaywatchMurder == 5 and HEALTH of Mother <= 0		//#test.chl:264
	JMP skip_24
skip_26:
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	AND
	JZ skip_27

//@			begin dialogue		//#test.chl:266
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8

//@				eject evil spirit		//#test.chl:268
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)

				//"A whole family slaughtered, nice going!"
//@				say single line HELP_TEXT_BAYWATCH_33		//#test.chl:271
	PUSHB true
	PUSHI 1603
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)

//@				update snapshot details success 1 alignment -1 HELP_TEXT_TITLE_01 		//#test.chl:273
	PUSHF 1.0
	PUSHF 1.0
	NEG
	PUSHI 2427
	PUSHB false
	PUSHI 9
	SYS UPDATE_SNAPSHOT_PICTURE	//[11, 0] (Coord position, Coord focus, float success, float alignment, int titleStrID, bool takingPicture, int challengeID)

//@				wait until read		//#test.chl:275
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				BaywatchFinished = 3		//#test.chl:277
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished

//@			end dialogue		//#test.chl:279
	SYS END_DIALOGUE	//[0, 0] ()

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother <= 0		//#test.chl:281
	JMP skip_24
skip_27:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 0.0
	EQ
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	LT
	AND
	PUSHF [BaywatchHomeCounter]
	PUSHF 5.0
	PUSHF [BaywatchMurder]
	SUBF
	EQ
	AND
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	AND
	JZ skip_24

//@			begin cinema		//#test.chl:283
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:285
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:286
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@				update snapshot success 1 alignment -0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:288
	PUSHF 1.0
	PUSHF 0.8
	NEG
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@				eject good spirit		//#test.chl:290
	PUSHI 1
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)

				//"Oh how terrible.. orphans."
//@				say single line HELP_TEXT_BAYWATCH_34		//#test.chl:293
	PUSHB true
	PUSHI 1604
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:294
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				BaywatchFinished = 4		//#test.chl:296
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished

//@			end cinema		//#test.chl:298
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_24:


//@		if camera position near [SwimPos] radius 20 and SwimPos viewed and Fun == 0		//#test.chl:302
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_DISTANCE	//[6, 1] (Coord p0, Coord p1) returns (float)
	PUSHF 20.0
	LT
	PUSHF [SwimPos]
	SYS GAME_THING_FIELD_OF_VIEW	//[1, 1] (Object object) returns (bool)
	AND
	PUSHF [Fun]
	PUSHF 0.0
	EQ
	AND
	JZ skip_28

//@			begin dialogue		//#test.chl:304
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8

//@				say single line HELP_TEXT_BAYWATCH_24		//#test.chl:306
	PUSHB true
	PUSHI 1594
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.chl:307
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@				Fun = 1		//#test.chl:309
	PUSHF [Fun]
	POPI
	PUSHF 1.0
	POPF Fun

//@			end dialogue		//#test.chl:311
	SYS END_DIALOGUE	//[0, 0] ()
skip_28:

//@		if BaywatchSwimmingCounter == 5 and [Mother] near [SwimPos] radius 40 and Mother is not HELD and not Mother in MyCreature hand		//#test.chl:314
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 5.0
	EQ
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_DISTANCE	//[6, 1] (Coord p0, Coord p1) returns (float)
	PUSHF 40.0
	LT
	AND
	PUSHF [Mother]
	PUSHI 9
	SWAPI
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	CASTB
	NOT
	AND
	PUSHF [Mother]
	PUSHF [MyCreature]
	SYS IN_CREATURE_HAND	//[2, 1] (Object obj, Object creature) returns (bool)
	NOT
	AND
	JZ skip_29

//@			if HEALTH of Mother > 0		//#test.chl:316
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_30

//@				begin cinema		//#test.chl:318
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.chl:320
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.chl:321
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					say single line HELP_TEXT_BAYWATCH_26		//#test.chl:323
	PUSHB true
	PUSHI 1596
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.chl:324
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@					set fade red 0 green 0 blue 0 time 2		//#test.chl:326
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)
//@					wait until fade ready		//#test.chl:327
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_8

//@					set Mother position to [SoapBox]		//#test.chl:329
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)

//@					if HEALTH of Boy1 > 0		//#test.chl:331
	PUSHI 1
	PUSHF [Boy1]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_31
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.chl:332
	PUSHF [Boy1]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						Boy1 play ANM_P_AMBIENT1 loop -1		//#test.chl:333
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHI 228
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_31:

//@					if HEALTH of Boy2 > 0		//#test.chl:336
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_32
//@						set Boy2 position to [SoapBox] + [1,0,1]		//#test.chl:337
	PUSHF [Boy2]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						Boy2 play ANM_P_AMBIENT2 loop -1		//#test.chl:338
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHI 229
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_32:

//@					if HEALTH of Boy3 > 0		//#test.chl:341
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_33
//@						set Boy3 position to [SoapBox] + [1,0,0]		//#test.chl:342
	PUSHF [Boy3]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						Boy3 play ANM_P_AMBIENT1 loop -1		//#test.chl:343
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHI 228
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_33:

//@					if HEALTH of Boy4 > 0		//#test.chl:346
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_34
//@						set Boy4 position to [SoapBox] + [-1,0,1]		//#test.chl:347
	PUSHF [Boy4]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						Boy4 play ANM_P_CROWD_LOST loop -1		//#test.chl:348
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHI 259
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_34:

//@					if HEALTH of Boy5 > 0		//#test.chl:351
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_35
//@						set Boy5 position to [SoapBox] + [-1,0,0]		//#test.chl:352
	PUSHF [Boy5]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						Boy5 play ANM_P_CROWD_UNIMPRESSED_1 loop -1		//#test.chl:353
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHI 262
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_35:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.chl:356
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.chl:357
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					set Mother focus to camera position		//#test.chl:359
	PUSHF [Mother]
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)

//@					set fade in time 2		//#test.chl:361
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)
//@					wait until fade ready		//#test.chl:362
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_8

//@					say single line HELP_TEXT_BAYWATCH_27		//#test.chl:364
	PUSHB true
	PUSHI 1597
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.chl:365
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@					close dialogue		//#test.chl:367
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@					wait 0.1 seconds		//#test.chl:368
	PUSHF 0.1
	SLEEP
	JZ loop_8

//@					snapshot challenge success 1 alignment 0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:370
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 0.8
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@					move camera position to [2803.6526, 68.4681, 3074.7102] time 4		//#test.chl:372
	PUSHF 2803.6526
	CASTC
	PUSHF 68.468102
	CASTC
	PUSHF 3074.7102
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@					move camera focus to [2784.4626, 51.4354, 3053.8333] time 4		//#test.chl:373
	PUSHF 2784.4626
	CASTC
	PUSHF 51.435398
	CASTC
	PUSHF 3053.8333
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@					wait until camera ready		//#test.chl:375
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8

//@					BaywatchFinished = 1		//#test.chl:377
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

//@				end cinema		//#test.chl:379
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@			elsif HEALTH of Mother <= 0		//#test.chl:381
	JMP skip_29
skip_30:
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ skip_29

//@				begin cinema		//#test.chl:383
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_8
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.chl:385
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.chl:386
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

					//"AAaahhh !!!!"
//@					say single line HELP_TEXT_BAYWATCH_35		//#test.chl:389
	PUSHB true
	PUSHI 1605
	PUSHB false
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.chl:390
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_8

//@					set fade red 0 green 0 blue 0 time 2		//#test.chl:392
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)
//@					wait until fade ready		//#test.chl:393
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_8

//@					set Mother position to [SoapBox]		//#test.chl:395
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					Mother play ANM_P_DEAD1 loop -1		//#test.chl:396
	PUSHF [Mother]
	PUSHO [Mother]
	PUSHI 273
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@					if HEALTH of Boy1 > 0		//#test.chl:398
	PUSHI 1
	PUSHF [Boy1]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_36
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.chl:399
	PUSHF [Boy1]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						set Boy1 focus to [Mother]		//#test.chl:400
	PUSHF [Boy1]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy1 play ANM_P_INSPECT_OBJECT_1 loop -1		//#test.chl:401
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHI 310
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_36:

//@					if HEALTH of Boy2 > 0		//#test.chl:404
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_37
//@						set Boy2 position to [SoapBox] + [0,0,-1]		//#test.chl:405
	PUSHF [Boy2]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						set Boy2 focus to [Mother]		//#test.chl:406
	PUSHF [Boy2]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy2 play ANM_P_INSPECT_OBJECT_2 loop -1		//#test.chl:407
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHI 311
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_37:

//@					if HEALTH of Boy3 > 0		//#test.chl:410
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_38
//@						set Boy3 position to [SoapBox] + [-1,0,-1]		//#test.chl:411
	PUSHF [Boy3]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						set Boy3 focus to [Mother]		//#test.chl:412
	PUSHF [Boy3]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy3 play ANM_P_MOURNING loop -1		//#test.chl:413
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHI 343
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_38:

//@					if HEALTH of Boy4 > 0		//#test.chl:416
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_39
//@						set Boy4 position to [SoapBox] + [1,0,-1]		//#test.chl:417
	PUSHF [Boy4]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						set Boy4 focus to [Mother]		//#test.chl:418
	PUSHF [Boy4]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy4 play ANM_P_OVERWORKED1 loop -1		//#test.chl:419
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHI 362
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_39:

//@					if HEALTH of Boy5 > 0		//#test.chl:422
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_40
//@						set Boy5 position to [SoapBox] + [1,0,1]		//#test.chl:423
	PUSHF [Boy5]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
	ADDC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@						set Boy5 focus to [Mother]		//#test.chl:424
	PUSHF [Boy5]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy5 play ANM_P_SCARED_STIFF loop -1		//#test.chl:425
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHI 385
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
skip_40:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.chl:428
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.chl:429
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					set fade in time 2		//#test.chl:431
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)
//@					wait until fade ready		//#test.chl:432
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_8

//@					close dialogue		//#test.chl:434
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@					wait 0.1 seconds		//#test.chl:435
	PUSHF 0.1
	SLEEP
	JZ loop_8

//@					snapshot challenge success 1 alignment -0.5 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:437
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 0.5
	NEG
	PUSHI 2427
	PUSHI 57	//"StandardReminder"
	PUSHI 1606
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@					move camera position to [2803.6526, 68.4681, 3074.7102]	time 4		//#test.chl:439
	PUSHF 2803.6526
	CASTC
	PUSHF 68.468102
	CASTC
	PUSHF 3074.7102
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@					move camera focus to [2784.4626, 51.4354, 3053.8333] time 4		//#test.chl:440
	PUSHF 2784.4626
	CASTC
	PUSHF 51.435398
	CASTC
	PUSHF 3053.8333
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@					wait until camera ready		//#test.chl:442
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_8

//@					BaywatchFinished = 4		//#test.chl:444
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished

//@				end cinema		//#test.chl:446
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_29:

//@	end while		//#test.chl:451
	JMP loop_41
skip_22:
	ENDEXCEPT
	JMP skip_42
exception_handler_21:
	ITEREXCEPT
skip_42:

//@	stop script "BaywatchSwimmingBoy"		//#test.chl:453
	PUSHI 74	//"BaywatchSwimmingBoy"
	SYS STOP_SCRIPT	//[1, 0] (StrPtr scriptName)
//@	stop script "BaywatchSwimmingLaugh"		//#test.chl:454
	PUSHI 94	//"BaywatchSwimmingLaugh"
	SYS STOP_SCRIPT	//[1, 0] (StrPtr scriptName)


//@end script BaywatchMain		//#test.chl:457
	ENDEXCEPT
	JMP skip_43
exception_handler_20:
	ITEREXCEPT
skip_43:
	END	//BaywatchMain


AUTORUN


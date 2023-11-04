//LHVM Challenge ASM version 7

DATA
//0x0000846B
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

source test.txt

global BaywatchMurder


//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchSwimmingLaugh(SwimPos)
	Local Amount
	EXCEPT exception_handler_0
	POPF SwimPos
//@	Amount=0		//#test.txt:9
	PUSHF 0.0
	POPF Amount

//@start		//#test.txt:11
	FREE
//
	EXCEPT exception_handler_1
//@	while BaywatchSwimmingCounter >= 1		//#test.txt:12
loop_4:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	GEQ
	JZ skip_2
//@		Amount = 250/(BaywatchSwimmingCounter)		//#test.txt:13
	PUSHF [Amount]
	POPI
	PUSHF 250.0
	PUSHF [BaywatchSwimmingCounter]
	DIV
	POPF Amount
//@		if number from 0 to Amount == 0		//#test.txt:14
	PUSHF 0.0
	PUSHF [Amount]
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	PUSHF 0.0
	EQ
	JZ skip_3
//@			start sound constant from LH_SCRIPT_SAMPLE_CHILD_LAUGH_01 to LH_SCRIPT_SAMPLE_CHILD_LAUGH_07 AUDIO_SFX_BANK_TYPE_SCRIPT_SFX at [SwimPos]		//#test.txt:15
	PUSHI 31
	PUSHI 37
	SYS2 RANDOM_ULONG	//[2, 1] (int min, int max) returns (int)
	PUSHI 5
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHB true
	SYS PLAY_SOUND_EFFECT	//[6, 0] (int sound, AUDIO_SFX_BANK_TYPE soundbank, Coord position, bool withPosition)
//@		end if		//#test.txt:16
	JMP skip_3
skip_3:
//@	end while		//#test.txt:17
	JMP loop_4
skip_2:
	ENDEXCEPT
	JMP skip_5
exception_handler_1:
	ITEREXCEPT
skip_5:
//@end script BaywatchSwimmingLaugh		//#test.txt:18
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

//@	Murdered = 0		//#test.txt:24
	PUSHF 0.0
	POPF Murdered
//@	InWater = 1		//#test.txt:25
	PUSHF 1.0
	POPF InWater
//@	LookAt = marker at [2962.9587, 0.0000, 3079.3020]		//#test.txt:26
	PUSHI 1
	PUSHI 0
	PUSHF 2962.9587
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3079.302
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF LookAt
//@	Random = 0		//#test.txt:27
	PUSHF 0.0
	POPF Random

//@start		//#test.txt:29
	FREE

//@	AGE of Boy = 11		//#test.txt:31
	PUSHI 16
	PUSHF [Boy]
	PUSHI 16
	PUSHF [Boy]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 11.0
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)
//@	Random = number from 1 to 4		//#test.txt:32
	PUSHF [Random]
	POPI
	PUSHF 1.0
	PUSHF 4.0
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	POPF Random
//@	wait Random seconds		//#test.txt:33
loop_8:
	PUSHF [Random]
	SLEEP
	JZ loop_8
//@	Boy play ANM_P_SWIM2 loop -1		//#test.txt:34
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 390
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@	set Boy focus to [LookAt]		//#test.txt:35
	PUSHF [Boy]
	PUSHF [LookAt]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//
	EXCEPT exception_handler_9

//@	while [Boy] not near [VillagerHutPos] radius 5 and Murdered == 0		//#test.txt:37
loop_27:
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

//@		if Boy is HELD or Boy in MyCreature hand		//#test.txt:39
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
//@			if InWater == 1		//#test.txt:40
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ skip_12
//@				BaywatchSwimmingCounter--		//#test.txt:41
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	SUBF
	POPF BaywatchSwimmingCounter
//@			end if		//#test.txt:42
	JMP skip_12
skip_12:
//@			InWater=0		//#test.txt:43
	PUSHF [InWater]
	POPI
	PUSHF 0.0
	POPF InWater
//@			wait until (Boy is not HELD and not Boy in MyCreature hand) and Boy is not FLYING		//#test.txt:44
loop_13:
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
	JZ loop_13

//@			if HEALTH of Boy > 0 and (Boy is not HELD and not Boy in MyCreature hand)		//#test.txt:46
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
	JZ skip_14
//@				if land height at [Boy] > 0			//#test.txt:47
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_LAND_HEIGHT	//[3, 1] (Coord position) returns (float)
	PUSHF 0.0
	GT
	JZ skip_15
//@					Boy play ANM_P_CROWD_LOST_2 loop 1		//#test.txt:48
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 230
	PUSHF 1.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@					begin dialogue		//#test.txt:50
loop_16:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_16
//@						say single line HELP_TEXT_BAYWATCH_25		//#test.txt:51
	PUSHB true
	PUSHI 1591
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@						wait until read		//#test.txt:52
loop_17:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_17
//@					end dialogue		//#test.txt:53
	SYS END_DIALOGUE	//[0, 0] ()

//@					wait until Boy played		//#test.txt:55
loop_18:
	PUSHF [Boy]
	SYS PLAYED	//[1, 1] (Object obj) returns (bool)
	JZ loop_18
//@					SPEED of Boy = 0.5		//#test.txt:56
	PUSHI 11
	PUSHF [Boy]
	PUSHI 11
	PUSHF [Boy]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.5
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)

//@					move Boy position to [VillagerHutPos]		//#test.txt:58
	PUSHF [Boy]
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)
					
//@				elsif land height at [Boy] <= 0		//#test.txt:60
	JMP skip_19
skip_15:
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_LAND_HEIGHT	//[3, 1] (Coord position) returns (float)
	PUSHF 0.0
	LEQ
	JZ skip_20
//@					set Boy position to [Boy]		//#test.txt:61
	PUSHF [Boy]
	PUSHF [Boy]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@					Boy play ANM_P_SWIM2 loop -1		//#test.txt:62
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHI 390
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					BaywatchSwimmingCounter++		//#test.txt:63
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	ADDF
	POPF BaywatchSwimmingCounter
//@					InWater=1		//#test.txt:64
	PUSHF [InWater]
	POPI
	PUSHF 1.0
	POPF InWater
skip_19:
//@				end if		//#test.txt:65
	JMP skip_20
skip_20:
//@			end if		//#test.txt:66
	JMP skip_14
skip_14:
//@		end if		//#test.txt:67
	JMP skip_11
skip_11:

//@		if HEALTH of Boy <= 0		//#test.txt:69
	PUSHI 1
	PUSHF [Boy]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ skip_21
//@			if BaywatchMurder == 0		//#test.txt:70
	PUSHF [BaywatchMurder]
	PUSHF 0.0
	EQ
	JZ skip_22
//@				begin dialogue		//#test.txt:71
loop_23:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_23
//@					eject evil spirit		//#test.txt:72
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@					say HELP_TEXT_BAYWATCH_31		//#test.txt:73
	PUSHB false
	PUSHI 1597
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.txt:74
loop_24:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_24

//@					eject good spirit		//#test.txt:76
	PUSHI 1
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@					say HELP_TEXT_BAYWATCH_32		//#test.txt:77
	PUSHB false
	PUSHI 1598
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.txt:78
loop_25:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_25

//@				end dialogue		//#test.txt:80
	SYS END_DIALOGUE	//[0, 0] ()
//@			end if		//#test.txt:81
	JMP skip_22
skip_22:

//@			BaywatchMurder ++		//#test.txt:83
	PUSHF [BaywatchMurder]
	PUSHF 1.0
	ADDF
	POPF BaywatchMurder
//@			Murdered = 1		//#test.txt:84
	PUSHF [Murdered]
	POPI
	PUSHF 1.0
	POPF Murdered
//@			if InWater == 1		//#test.txt:85
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ skip_26
//@				BaywatchSwimmingCounter--		//#test.txt:86
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 1.0
	SUBF
	POPF BaywatchSwimmingCounter
//@			end if		//#test.txt:87
	JMP skip_26
skip_26:
//@		end if		//#test.txt:88
	JMP skip_21
skip_21:

//@	end while		//#test.txt:90
	JMP loop_27
skip_10:
	ENDEXCEPT
	JMP skip_28
exception_handler_9:
	ITEREXCEPT
skip_28:

//@	if Murdered == 0		//#test.txt:92
	PUSHF [Murdered]
	PUSHF 0.0
	EQ
	JZ skip_29
//@		BaywatchHomeCounter ++		//#test.txt:93
	PUSHF [BaywatchHomeCounter]
	PUSHF 1.0
	ADDF
	POPF BaywatchHomeCounter
//@	end if		//#test.txt:94
	JMP skip_29
skip_29:

//@end script BaywatchSwimmingBoy		//#test.txt:96
	ENDEXCEPT
	JMP skip_30
exception_handler_7:
	ITEREXCEPT
skip_30:
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
	EXCEPT exception_handler_31
	PUSHI 1
	PUSHI 0
	PUSHF 2781.1104
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3053.49
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF VillagerHutPos
//@	SwimPos = marker at [2964.223, 0.000, 3084.874]		//#test.txt:102
	PUSHI 1
	PUSHI 0
	PUSHF 2964.2229
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.874
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos
//@	SwimPos2 = marker at [2968.614, 0.000, 3089.957]		//#test.txt:103
	PUSHI 1
	PUSHI 0
	PUSHF 2968.614
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3089.957
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos2
//@	SwimPos3 = marker at [2969.778, 0.000, 3082.155]		//#test.txt:104
	PUSHI 1
	PUSHI 0
	PUSHF 2969.7781
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3082.155
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos3
//@	SwimPos4 = marker at [2962.422, 0.000, 3078.884]  		//#test.txt:105
	PUSHI 1
	PUSHI 0
	PUSHF 2962.4221
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3078.884
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos4
//@	SwimPos5 = marker at [2960.591, 0.000, 3084.722]		//#test.txt:106
	PUSHI 1
	PUSHI 0
	PUSHF 2960.5911
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.7219
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SwimPos5


//@	ShorePos = marker at [2943.603, 0.000, 3095.944]		//#test.txt:109
	PUSHI 1
	PUSHI 0
	PUSHF 2943.603
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3095.9441
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF ShorePos
//@	Mother = 0		//#test.txt:110
	PUSHF 0.0
	POPF Mother
//@	MotherDead = 0		//#test.txt:111
	PUSHF 0.0
	POPF MotherDead
//@	Influence = 0		//#test.txt:112
	PUSHF 0.0
	POPF Influence
//@	Highlight = 0		//#test.txt:113
	PUSHF 0.0
	POPF Highlight
//@	BaywatchReward = 0		//#test.txt:114
	PUSHF 0.0
	POPF BaywatchReward
//@	BaywatchFinished = 0		//#test.txt:115
	PUSHF 0.0
	POPF BaywatchFinished
//@	SoapBox = marker at [2786.5073, 50.9200, 3064.3250]		//#test.txt:116
	PUSHI 1
	PUSHI 0
	PUSHF 2786.5073
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3064.325
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF SoapBox
//@	Fun = 0		//#test.txt:117
	PUSHF 0.0
	POPF Fun

//@	Boy1 = 0		//#test.txt:119
	PUSHF 0.0
	POPF Boy1
//@	Boy2 = 0		//#test.txt:120
	PUSHF 0.0
	POPF Boy2
//@	Boy3 = 0		//#test.txt:121
	PUSHF 0.0
	POPF Boy3
//@	Boy4 = 0		//#test.txt:122
	PUSHF 0.0
	POPF Boy4
//@	Boy5 = 0 		//#test.txt:123
	PUSHF 0.0
	POPF Boy5

//@	RewardPos = marker at [2854.7266, 50.3966, 3056.9429]		//#test.txt:125
	PUSHI 1
	PUSHI 0
	PUSHF 2854.7266
	CASTC
	PUSHF 50.396599
	CASTC
	PUSHF 3056.9429
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardPos
//@	RewardCameraPos = marker at [2825.0017, 66.7546, 3067.1838]		//#test.txt:126
	PUSHI 1
	PUSHI 0
	PUSHF 2825.0017
	CASTC
	PUSHF 66.754601
	CASTC
	PUSHF 3067.1838
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardCameraPos
//@	RewardCameraFoc = marker at [2854.1843, 50.8918, 3054.0120]		//#test.txt:127
	PUSHI 1
	PUSHI 0
	PUSHF 2854.1843
	CASTC
	PUSHF 50.8918
	CASTC
	PUSHF 3054.012
	CASTC
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF RewardCameraFoc

//@start		//#test.txt:129
	FREE
//@	BaywatchSwimmingCounter=5		//#test.txt:130
	PUSHF [BaywatchSwimmingCounter]
	POPI
	PUSHF 5.0
	POPF BaywatchSwimmingCounter

//@	Influence=create anti influence at position [SwimPos] radius 30		//#test.txt:132
	PUSHF [Influence]
	POPI
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 30.0
	PUSHI 0
	PUSHI 1
	SYS INFLUENCE_POSITION	//[6, 1] (Coord position, float radius, int zero, int anti) returns (Object)
	POPF Influence

//@	Highlight = create highlight HIGHLIGHT_CHALLENGE at [VillagerHutPos]		//#test.txt:134
	PUSHF [Highlight]
	POPI
	PUSHI 2
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHI 9
	SYS CREATE_HIGHLIGHT	//[5, 1] (HIGHLIGHT_INFO type, Coord position, int challengeID) returns (Object)
	POPF Highlight
//@	run script ChallengeHighlightNotify(Highlight, VillagerHutPos, variable EVIL_ADVISOR, variable HELP_TEXT_GENERAL_CHALLENGE_START_03)		//#test.txt:135
	PUSHF [Highlight]
	PUSHF [VillagerHutPos]
	PUSHI 2
	CASTF
	PUSHI 2696
	CASTF
	CALL 0

//@	Mother = create VILLAGER VILLAGER_INFO_INDIAN_HOUSEWIFE_FEMALE at [VillagerHutPos]		//#test.txt:137
	PUSHF [Mother]
	POPI
	PUSHI 4
	PUSHI 28
	PUSHF [VillagerHutPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Mother

//@	BaywatchHomeCounter = 0		//#test.txt:139
	PUSHF [BaywatchHomeCounter]
	POPI
	PUSHF 0.0
	POPF BaywatchHomeCounter

//@	Boy1 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos]		//#test.txt:141
	PUSHF [Boy1]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy1
//@	Boy2 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos2]		//#test.txt:142
	PUSHF [Boy2]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos2]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy2
//@	Boy3 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos3]		//#test.txt:143
	PUSHF [Boy3]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos3]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy3
//@	Boy4 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos4]		//#test.txt:144
	PUSHF [Boy4]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos4]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy4
//@	Boy5 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos5]		//#test.txt:145
	PUSHF [Boy5]
	POPI
	PUSHI 4
	PUSHI 31
	PUSHF [SwimPos5]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS CREATE	//[5, 1] (SCRIPT_OBJECT_TYPE type, SCRIPT_OBJECT_SUBTYPE subtype, Coord position) returns (Object)
	POPF Boy5

//@	run background script BaywatchSwimmingBoy(Boy1, VillagerHutPos)		//#test.txt:147
	PUSHF [Boy1]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy2, VillagerHutPos)		//#test.txt:148
	PUSHF [Boy2]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy3, VillagerHutPos)		//#test.txt:149
	PUSHF [Boy3]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy4, VillagerHutPos)		//#test.txt:150
	PUSHF [Boy4]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingBoy(Boy5, VillagerHutPos)		//#test.txt:151
	PUSHF [Boy5]
	PUSHF [VillagerHutPos]
	START BaywatchSwimmingBoy
//@	run background script BaywatchSwimmingLaugh(SwimPos)		//#test.txt:152
	PUSHF [SwimPos]
	START BaywatchSwimmingLaugh

//@	begin cinema		//#test.txt:154
loop_32:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_32
loop_33:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_33
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@		start music MUSIC_TYPE_SCRIPT_GENERIC_03		//#test.txt:155
	PUSHI 59
	SYS START_MUSIC	//[1, 0] (int music)
//@		enable Mother high gfx detail		//#test.txt:156
	PUSHB true
	PUSHF [Mother]
	SYS SET_HIGH_GRAPHICS_DETAIL	//[2, 0] (bool enable, Object object)

//@		move camera position to [2790.691, 53.522, 3069.138] time 3		//#test.txt:158
	PUSHF 2790.6909
	CASTC
	PUSHF 53.521999
	CASTC
	PUSHF 3069.1379
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2783.190, 50.229, 3058.410] time 3		//#test.txt:159
	PUSHF 2783.1899
	CASTC
	PUSHF 50.229
	CASTC
	PUSHF 3058.4099
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		SPEED of Mother = 0.5		//#test.txt:161
	PUSHI 11
	PUSHF [Mother]
	PUSHI 11
	PUSHF [Mother]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.5
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)
//@		move Mother position to [SoapBox]		//#test.txt:162
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)

//@		wait until camera ready		//#test.txt:164
loop_34:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_34

//@		Mother play ANM_P_LOOKING_FOR_SOMETHING loop 1		//#test.txt:166
	PUSHF [Mother]
	PUSHO [Mother]
	PUSHI 310
	PUSHF 1.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@		move camera position to [2787.182, 54.323, 3070.434] time 6		//#test.txt:168
	PUSHF 2787.1819
	CASTC
	PUSHF 54.323002
	CASTC
	PUSHF 3070.4341
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2785.620, 51.201, 3059.870] time 6		//#test.txt:169
	PUSHF 2785.6201
	CASTC
	PUSHF 51.201
	CASTC
	PUSHF 3059.8701
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		say single line HELP_TEXT_BAYWATCH_22		//#test.txt:171
	PUSHB true
	PUSHI 1588
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@		wait until read		//#test.txt:172
loop_35:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_35

//@		close dialogue		//#test.txt:174
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@		wait 0.1 seconds		//#test.txt:175
loop_36:
	PUSHF 0.1
	SLEEP
	JZ loop_36

		// Cut to children swimming

//@		set camera position to [2960.404, 6.550, 3114.483] 		//#test.txt:179
	PUSHF 2960.4041
	CASTC
	PUSHF 6.5500002
	CASTC
	PUSHF 3114.4829
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@		set camera focus to [2958.053, 3.424, 3097.387] 		//#test.txt:180
	PUSHF 2958.053
	CASTC
	PUSHF 3.424
	CASTC
	PUSHF 3097.387
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@		snapshot challenge success 0.0 alignment 0 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:182
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 0.0
	PUSHF 0.0
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@		say single line HELP_TEXT_BAYWATCH_23		//#test.txt:184
	PUSHB true
	PUSHI 1589
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
		
//@		move camera position to [2965.097, 2.745, 3092.632] time 6		//#test.txt:186
	PUSHF 2965.0969
	CASTC
	PUSHF 2.7449999
	CASTC
	PUSHF 3092.6321
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2962.747, -0.381, 3075.535] time 6		//#test.txt:187
	PUSHF 2962.7471
	CASTC
	PUSHF 0.381
	NEG
	CASTC
	PUSHF 3075.5349
	CASTC
	PUSHF 6.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)
//@		wait until read		//#test.txt:188
loop_37:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_37

//@		wait until camera ready		//#test.txt:190
loop_38:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_38

		// Cut back to Mother

//@		set camera position to [2790.691, 53.522, 3069.138]		//#test.txt:194
	PUSHF 2790.6909
	CASTC
	PUSHF 53.521999
	CASTC
	PUSHF 3069.1379
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@		set camera focus to [2783.190, 50.229, 3058.410]		//#test.txt:195
	PUSHF 2783.1899
	CASTC
	PUSHF 50.229
	CASTC
	PUSHF 3058.4099
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@		SPEED of Mother = 0.2		//#test.txt:197
	PUSHI 11
	PUSHF [Mother]
	PUSHI 11
	PUSHF [Mother]
	SYS2 GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	POPI
	PUSHF 0.2
	SYS2 SET_PROPERTY	//[3, 0] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object, float val)

//@		move camera position to [2772.9670, 69.8213, 3093.4985]	time 4		//#test.txt:199
	PUSHF 2772.967
	CASTC
	PUSHF 69.821297
	CASTC
	PUSHF 3093.4985
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@		move camera focus to [2787.7322, 51.4469, 3067.7637] time 3		//#test.txt:200
	PUSHF 2787.7322
	CASTC
	PUSHF 51.446899
	CASTC
	PUSHF 3067.7637
	CASTC
	PUSHF 3.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@		wait until camera ready		//#test.txt:202
loop_39:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_39

//@		disable Mother high gfx detail		//#test.txt:204
	PUSHB false
	PUSHF [Mother]
	SYS SET_HIGH_GRAPHICS_DETAIL	//[2, 0] (bool enable, Object object)
//@		stop music		//#test.txt:205
	SYS STOP_MUSIC	//[0, 0] ()
//@	end cinema		//#test.txt:206
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@	state Mother WANDER_AROUND		//#test.txt:208
	PUSHF [Mother]
	PUSHI 199
//@		position [SoapBox]		//#test.txt:209
	PUSHI [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_SCRIPT_STATE_POS	//[4, 0] (ObjectInt object, Coord position)
//@		float 6		//#test.txt:210
	PUSHI [Mother]
	PUSHF 6.0
	SYS SET_SCRIPT_FLOAT	//[2, 0] (ObjectInt object, float value)
//@		ulong 4, 20		//#test.txt:211
	PUSHI [Mother]
	PUSHF 4.0
	CASTI
	PUSHF 20.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@	wait 5 seconds		//#test.txt:213
loop_40:
	PUSHF 5.0
	SLEEP
	JZ loop_40
//
	EXCEPT exception_handler_41

//@	while BaywatchFinished == 0		//#test.txt:215
loop_100:
	PUSHF [BaywatchFinished]
	PUSHF 0.0
	EQ
	JZ skip_42

//@		if BaywatchHomeCounter == 5	and HEALTH of Mother > 0		//#test.txt:217
	PUSHF [BaywatchHomeCounter]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ skip_43
//@			begin cinema		//#test.txt:218
loop_44:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_44
loop_45:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_45
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.txt:219
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.txt:220
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)
//@				say single line HELP_TEXT_BAYWATCH_27		//#test.txt:221
	PUSHB true
	PUSHI 1593
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:222
loop_46:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_46
//@				close dialogue		//#test.txt:223
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@				BaywatchFinished = 1		//#test.txt:224
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

				// Reward - Big Spell Dispenser
//@				run script GiveSpellDispenserReward(RewardPos, variable MAGIC_TYPE_CREATURE_SPELL_BIG, 180, 1, 0)		//#test.txt:227
	PUSHF [RewardPos]
	PUSHI 28
	CASTF
	PUSHF 180.0
	PUSHF 1.0
	PUSHF 0.0
	CALL 0
//@				move camera position to [RewardCameraPos] time 5		//#test.txt:228
	PUSHF [RewardCameraPos]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 5.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@				move camera focus to [RewardCameraFoc] time 2		//#test.txt:229
	PUSHF [RewardCameraFoc]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 2.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)
//@				wait until camera ready		//#test.txt:230
loop_47:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_47
//@				snapshot challenge success 1 alignment 1 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:231
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 1.0
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)
//@			end cinema 		//#test.txt:232
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother > 0		//#test.txt:234
	JMP skip_48
skip_43:
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
	JZ skip_49
//@			begin cinema		//#test.txt:235
loop_50:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_50
loop_51:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_51
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.txt:236
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.txt:237
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)
//@				update snapshot success 1 alignment -0.2 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:238
	PUSHF 1.0
	PUSHF 0.2
	NEG
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)
//@				say single line HELP_TEXT_BAYWATCH_28		//#test.txt:239
	PUSHB true
	PUSHI 1594
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:240
loop_52:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_52

//@				BaywatchFinished = 3		//#test.txt:242
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished
//@			end cinema		//#test.txt:243
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_48:

//@		elsif BaywatchMurder == 5 and HEALTH of Mother > 0		//#test.txt:245
	JMP skip_53
skip_49:
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ skip_54

//@			begin cinema		//#test.txt:247
loop_55:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_55
loop_56:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_56
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.txt:248
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.txt:249
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@				update snapshot success 1 alignment -0.6 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:251
	PUSHF 1.0
	PUSHF 0.6
	NEG
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@				say	single line HELP_TEXT_BAYWATCH_29		//#test.txt:253
	PUSHB true
	PUSHI 1595
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:254
loop_57:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_57

//@				eject evil spirit		//#test.txt:256
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)
//@				say single line HELP_TEXT_BAYWATCH_30		//#test.txt:257
	PUSHB true
	PUSHI 1596
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:258
loop_58:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_58

//@				BaywatchFinished = 2		//#test.txt:260
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 2.0
	POPF BaywatchFinished

//@			end cinema		//#test.txt:262
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_53:

//@		elsif BaywatchMurder == 5 and HEALTH of Mother <= 0		//#test.txt:264
	JMP skip_59
skip_54:
	PUSHF [BaywatchMurder]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	AND
	JZ skip_60

//@			begin dialogue		//#test.txt:266
loop_61:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_61

//@				eject evil spirit		//#test.txt:268
	PUSHI 2
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)

				//"A whole family slaughtered, nice going!"
//@				say single line HELP_TEXT_BAYWATCH_33		//#test.txt:271
	PUSHB true
	PUSHI 1599
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)

//@				update snapshot details success 1 alignment -1 HELP_TEXT_TITLE_01 		//#test.txt:273
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 1.0
	NEG
	PUSHI 2423
	PUSHB false
	PUSHI 9
	SYS UPDATE_SNAPSHOT_PICTURE	//[11, 0] (Coord position, Coord focus, float success, float alignment, int titleStrID, bool takingPicture, int challengeID)

//@				wait until read		//#test.txt:275
loop_62:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_62

//@				BaywatchFinished = 3		//#test.txt:277
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished

//@			end dialogue		//#test.txt:279
	SYS END_DIALOGUE	//[0, 0] ()
skip_59:

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother <= 0		//#test.txt:281
	JMP skip_63
skip_60:
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
	JZ skip_64

//@			begin cinema		//#test.txt:283
loop_65:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_65
loop_66:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_66
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.txt:285
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.txt:286
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@				update snapshot success 1 alignment -0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:288
	PUSHF 1.0
	PUSHF 0.8
	NEG
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS UPDATE_SNAPSHOT	//[6+, 0] (float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@				eject good spirit		//#test.txt:290
	PUSHI 1
	SYS SPIRIT_EJECT	//[1, 0] (HELP_SPIRIT_TYPE spirit)

				//"Oh how terrible.. orphans."
//@				say single line HELP_TEXT_BAYWATCH_34		//#test.txt:293
	PUSHB true
	PUSHI 1600
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:294
loop_67:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_67

//@				BaywatchFinished = 4		//#test.txt:296
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished

//@			end cinema		//#test.txt:298
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_63:
//@		end if		//#test.txt:299
	JMP skip_64
skip_64:


//@		if camera position near [SwimPos] radius 20 and SwimPos viewed and Fun == 0		//#test.txt:302
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
	JZ skip_68

//@			begin dialogue		//#test.txt:304
loop_69:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_69

//@				say single line HELP_TEXT_BAYWATCH_24		//#test.txt:306
	PUSHB true
	PUSHI 1590
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@				wait until read		//#test.txt:307
loop_70:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_70

//@				Fun = 1		//#test.txt:309
	PUSHF [Fun]
	POPI
	PUSHF 1.0
	POPF Fun

//@			end dialogue		//#test.txt:311
	SYS END_DIALOGUE	//[0, 0] ()
//@		end if		//#test.txt:312
	JMP skip_68
skip_68:

//@		if BaywatchSwimmingCounter == 5 and [Mother] near [SwimPos] radius 40 and Mother is not HELD and not Mother in MyCreature hand		//#test.txt:314
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
	JZ skip_71

//@			if HEALTH of Mother > 0		//#test.txt:316
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_72

//@				begin cinema		//#test.txt:318
loop_73:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_73
loop_74:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_74
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.txt:320
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.txt:321
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					say single line HELP_TEXT_BAYWATCH_26		//#test.txt:323
	PUSHB true
	PUSHI 1592
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.txt:324
loop_75:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_75

//@					set fade red 0 green 0 blue 0 time 2		//#test.txt:326
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)
//@					wait until fade ready		//#test.txt:327
loop_76:
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_76

//@					set Mother position to [SoapBox]		//#test.txt:329
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)

//@					if HEALTH of Boy1 > 0		//#test.txt:331
	PUSHI 1
	PUSHF [Boy1]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_77
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.txt:332
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						Boy1 play ANM_P_AMBIENT1 loop -1		//#test.txt:333
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHI 203
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:334
	JMP skip_77
skip_77:

//@					if HEALTH of Boy2 > 0		//#test.txt:336
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_78
//@						set Boy2 position to [SoapBox] + [1,0,1]		//#test.txt:337
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						Boy2 play ANM_P_AMBIENT2 loop -1		//#test.txt:338
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHI 204
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:339
	JMP skip_78
skip_78:

//@					if HEALTH of Boy3 > 0		//#test.txt:341
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_79
//@						set Boy3 position to [SoapBox] + [1,0,0]		//#test.txt:342
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						Boy3 play ANM_P_AMBIENT1 loop -1		//#test.txt:343
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHI 203
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:344
	JMP skip_79
skip_79:

//@					if HEALTH of Boy4 > 0		//#test.txt:346
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_80
//@						set Boy4 position to [SoapBox] + [-1,0,1]		//#test.txt:347
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						Boy4 play ANM_P_CROWD_LOST loop -1		//#test.txt:348
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHI 229
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:349
	JMP skip_80
skip_80:

//@					if HEALTH of Boy5 > 0		//#test.txt:351
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_81
//@						set Boy5 position to [SoapBox] + [-1,0,0]		//#test.txt:352
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						Boy5 play ANM_P_CROWD_UNIMPRESSED_1 loop -1		//#test.txt:353
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHI 232
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:354
	JMP skip_81
skip_81:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.txt:356
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.txt:357
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					set Mother focus to camera position		//#test.txt:359
	PUSHF [Mother]
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)

//@					set fade in time 2		//#test.txt:361
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)
//@					wait until fade ready		//#test.txt:362
loop_82:
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_82

//@					say single line HELP_TEXT_BAYWATCH_27		//#test.txt:364
	PUSHB true
	PUSHI 1593
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.txt:365
loop_83:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_83

//@					close dialogue		//#test.txt:367
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@					wait 0.1 seconds		//#test.txt:368
loop_84:
	PUSHF 0.1
	SLEEP
	JZ loop_84

//@					snapshot challenge success 1 alignment 0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:370
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 0.8
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@					move camera position to [2803.6526, 68.4681, 3074.7102] time 4		//#test.txt:372
	PUSHF 2803.6526
	CASTC
	PUSHF 68.468102
	CASTC
	PUSHF 3074.7102
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@					move camera focus to [2784.4626, 51.4354, 3053.8333] time 4		//#test.txt:373
	PUSHF 2784.4626
	CASTC
	PUSHF 51.435398
	CASTC
	PUSHF 3053.8333
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@					wait until camera ready		//#test.txt:375
loop_85:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_85

//@					BaywatchFinished = 1		//#test.txt:377
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

//@				end cinema		//#test.txt:379
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()

//@			elsif HEALTH of Mother <= 0		//#test.txt:381
	JMP skip_86
skip_72:
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ skip_87

//@				begin cinema		//#test.txt:383
loop_88:
	SYS START_CAMERA_CONTROL	//[0, 1] () returns (bool)
	JZ loop_88
loop_89:
	SYS START_DIALOGUE	//[0, 1] () returns (bool)
	JZ loop_89
	SYS START_GAME_SPEED	//[0, 0] ()
	PUSHB true
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.txt:385
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.txt:386
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

					//"AAaahhh !!!!"
//@					say single line HELP_TEXT_BAYWATCH_35		//#test.txt:389
	PUSHB true
	PUSHI 1601
	PUSHI 0
	SYS RUN_TEXT	//[3, 0] (bool singleLine, int textID, int withInteraction)
//@					wait until read		//#test.txt:390
loop_90:
	SYS TEXT_READ	//[0, 1] () returns (bool)
	JZ loop_90

//@					set fade red 0 green 0 blue 0 time 2		//#test.txt:392
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)
//@					wait until fade ready		//#test.txt:393
loop_91:
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_91

//@					set Mother position to [SoapBox]		//#test.txt:395
	PUSHF [Mother]
	PUSHF [SoapBox]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@					Mother play ANM_P_DEAD1 loop -1		//#test.txt:396
	PUSHF [Mother]
	PUSHO [Mother]
	PUSHI 243
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)

//@					if HEALTH of Boy1 > 0		//#test.txt:398
	PUSHI 1
	PUSHF [Boy1]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_92
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.txt:399
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						set Boy1 focus to [Mother]		//#test.txt:400
	PUSHF [Boy1]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy1 play ANM_P_INSPECT_OBJECT_1 loop -1		//#test.txt:401
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHI 280
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:402
	JMP skip_92
skip_92:

//@					if HEALTH of Boy2 > 0		//#test.txt:404
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_93
//@						set Boy2 position to [SoapBox] + [0,0,-1]		//#test.txt:405
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						set Boy2 focus to [Mother]		//#test.txt:406
	PUSHF [Boy2]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy2 play ANM_P_INSPECT_OBJECT_2 loop -1		//#test.txt:407
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHI 281
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:408
	JMP skip_93
skip_93:

//@					if HEALTH of Boy3 > 0		//#test.txt:410
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_94
//@						set Boy3 position to [SoapBox] + [-1,0,-1]		//#test.txt:411
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						set Boy3 focus to [Mother]		//#test.txt:412
	PUSHF [Boy3]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy3 play ANM_P_MOURNING loop -1		//#test.txt:413
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHI 313
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:414
	JMP skip_94
skip_94:

//@					if HEALTH of Boy4 > 0		//#test.txt:416
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_95
//@						set Boy4 position to [SoapBox] + [1,0,-1]		//#test.txt:417
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						set Boy4 focus to [Mother]		//#test.txt:418
	PUSHF [Boy4]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy4 play ANM_P_OVERWORKED1 loop -1		//#test.txt:419
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHI 332
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:420
	JMP skip_95
skip_95:

//@					if HEALTH of Boy5 > 0		//#test.txt:422
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ skip_96
//@						set Boy5 position to [SoapBox] + [1,0,1]		//#test.txt:423
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
	SYS SET_POSITION	//[4, 0] (Object object, Coord position)
//@						set Boy5 focus to [Mother]		//#test.txt:424
	PUSHF [Boy5]
	PUSHF [Mother]
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS SET_FOCUS	//[4, 0] (Object object, Coord position)
//@						Boy5 play ANM_P_SCARED_STIFF loop -1		//#test.txt:425
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHI 355
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					end if		//#test.txt:426
	JMP skip_96
skip_96:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.txt:428
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
	SYS SET_CAMERA_POSITION	//[3, 0] (Coord position)
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.txt:429
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC
	SYS SET_CAMERA_FOCUS	//[3, 0] (Coord position)

//@					set fade in time 2		//#test.txt:431
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)
//@					wait until fade ready		//#test.txt:432
loop_97:
	SYS FADE_FINISHED	//[0, 1] () returns (bool)
	JZ loop_97

//@					close dialogue		//#test.txt:434
	SYS GAME_CLOSE_DIALOGUE	//[0, 0] ()
//@					wait 0.1 seconds		//#test.txt:435
loop_98:
	PUSHF 0.1
	SLEEP
	JZ loop_98

//@					snapshot challenge success 1 alignment -0.5 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.txt:437
	PUSHB false
	SYS GET_CAMERA_POSITION	//[0, 3] () returns (Coord)
	SYS GET_CAMERA_FOCUS	//[0, 3] () returns (Coord)
	PUSHF 1.0
	PUSHF 0.5
	NEG
	PUSHI 2423
	PUSHI 57	//"StandardReminder"
	PUSHI 1602
	CASTF
	PUSHI 1
	PUSHI 9
	SYS SNAPSHOT	//[13+, 0] (bool quest, Coord position, Coord focus, float success, float alignment, int titleStrID, StrPtr reminderScript, float... args, int argc, int challengeID)

//@					move camera position to [2803.6526, 68.4681, 3074.7102]	time 4		//#test.txt:439
	PUSHF 2803.6526
	CASTC
	PUSHF 68.468102
	CASTC
	PUSHF 3074.7102
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@					move camera focus to [2784.4626, 51.4354, 3053.8333] time 4		//#test.txt:440
	PUSHF 2784.4626
	CASTC
	PUSHF 51.435398
	CASTC
	PUSHF 3053.8333
	CASTC
	PUSHF 4.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)

//@					wait until camera ready		//#test.txt:442
loop_99:
	SYS HAS_CAMERA_ARRIVED	//[0, 1] () returns (bool)
	JZ loop_99

//@					BaywatchFinished = 4		//#test.txt:444
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished

//@				end cinema		//#test.txt:446
	PUSHB false
	SYS SET_WIDESCREEN	//[1, 0] (bool enabled)
	SYS END_GAME_SPEED	//[0, 0] ()
	SYS END_CAMERA_CONTROL	//[0, 0] ()
	SYS END_DIALOGUE	//[0, 0] ()
skip_86:
//@			end if		//#test.txt:447
	JMP skip_87
skip_87:

//@		end if		//#test.txt:449
	JMP skip_71
skip_71:

//@	end while		//#test.txt:451
	JMP loop_100
skip_42:
	ENDEXCEPT
	JMP skip_101
exception_handler_41:
	ITEREXCEPT
skip_101:

//@	stop script "BaywatchSwimmingBoy"		//#test.txt:453
	PUSHI 74	//"BaywatchSwimmingBoy"
	SYS STOP_SCRIPT	//[1, 0] (StrPtr scriptName)
//@	stop script "BaywatchSwimmingLaugh"		//#test.txt:454
	PUSHI 94	//"BaywatchSwimmingLaugh"
	SYS STOP_SCRIPT	//[1, 0] (StrPtr scriptName)


//@end script BaywatchMain		//#test.txt:457
	ENDEXCEPT
	JMP skip_102
exception_handler_31:
	ITEREXCEPT
skip_102:
	END	//BaywatchMain


AUTORUN


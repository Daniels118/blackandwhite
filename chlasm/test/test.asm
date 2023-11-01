//LHVM Challenge ASM version 7

DATA
//0x00000000
string c0 = "Compiled with CHL Compiler developed by Daniele Lombardi"
string c57 = "BaywatchSwimmingBoy"
string c77 = "BaywatchSwimmingLaugh"

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
//0x00000000

source test.chl

global BaywatchSwimmingCounter


//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchSwimmingLaugh(SwimPos)
	Local Amount
	EXCEPT lbl0
	POPF SwimPos
//@	Amount=0		//#test.chl:9
	PUSHF 0.0
	POPF Amount

//@start		//#test.chl:11
	FREE
//
	EXCEPT lbl1
//@	while BaywatchSwimmingCounter >= 1		//#test.chl:12
lbl4:
	PUSHF [BaywatchHomeCounter]
	PUSHF 1.0
	GEQ
	JZ lbl2
//@		Amount = 250/(BaywatchSwimmingCounter)		//#test.chl:13
	PUSHF [Amount]
	POPI
	PUSHF 250.0
	PUSHF [BaywatchHomeCounter]
	DIV
	POPF Amount
//@		if number from 0 to Amount == 0		//#test.chl:14
	PUSHF 0.0
	PUSHF [Amount]
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	PUSHF 0.0
	EQ
	JZ lbl3
//@			start sound constant from LH_SCRIPT_SAMPLE_CHILD_LAUGH_01 to LH_SCRIPT_SAMPLE_CHILD_LAUGH_07 AUDIO_SFX_BANK_TYPE_SCRIPT_SFX at [SwimPos]		//#test.chl:15
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
lbl3:
//@	end while		//#test.chl:17
	JMP lbl4
lbl2:
	ENDEXCEPT
	JMP lbl5
lbl1:
	ITEREXCEPT
lbl5:
//@end script BaywatchSwimmingLaugh		//#test.chl:18
	ENDEXCEPT
	JMP lbl6
lbl0:
	ITEREXCEPT
lbl6:
	END

global BaywatchSwimmingCounter



//------------------------------------------------------------------------------------------------------------------------
begin script BaywatchSwimmingBoy(Boy, VillagerHutPos)
	Local Murdered
	Local InWater
	Local LookAt
	Local Random
	EXCEPT lbl7
	POPF Boy
	POPF VillagerHutPos

//@	Murdered = 0		//#test.chl:24
	PUSHF 0.0
	POPF Murdered
//@	InWater = 1		//#test.chl:25
	PUSHF 1.0
	POPF InWater
//@	LookAt = marker at [2962.9587, 0.0000, 3079.3020]		//#test.chl:26
	PUSHF 2962.9587
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3079.302
	CASTC
	POPF LookAt
//@	Random = 0		//#test.chl:27
	PUSHF 0.0
	POPF Random

//@start		//#test.chl:29
	FREE

//@	AGE of Boy = 11		//#test.chl:31
	PUSHF 11.0
//@	Random = number from 1 to 4		//#test.chl:32
	PUSHF [Random]
	POPI
	PUSHF 1.0
	PUSHF 4.0
	SYS RANDOM	//[2, 1] (float min, float max) returns (float)
	POPF Random
//@	wait Random seconds		//#test.chl:33
	PUSHF [Random]
//@	Boy play ANM_P_SWIM2 loop -1		//#test.chl:34
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@	set Boy focus to [LookAt]		//#test.chl:35
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//
	EXCEPT lbl8

//@	while [Boy] not near [VillagerHutPos] radius 5 and Murdered == 0		//#test.chl:37
lblF:
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 5.0
	PUSHF [Murdered]
	PUSHF 0.0
	EQ
	AND
	JZ lbl9

//@		if Boy is HELD or Boy in MyCreature hand		//#test.chl:39
	OR
	JZ lblA
//@			if InWater == 1		//#test.chl:40
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ lblB
lblB:
//@			InWater=0		//#test.chl:43
	PUSHF [InWater]
	POPI
	PUSHF 0.0
	POPF InWater
//@			wait until (Boy is not HELD and not Boy in MyCreature hand) and Boy is not FLYING		//#test.chl:44
	AND
	AND

//@			if HEALTH of Boy > 0 and (Boy is not HELD and not Boy in MyCreature hand)		//#test.chl:46
	PUSHI 1
	PUSHF [Boy]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	AND
	JZ lblA
//@				if land height at [Boy] > 0			//#test.chl:47
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	GT
	JZ lblC
//@					Boy play ANM_P_CROWD_LOST_2 loop 1		//#test.chl:48
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHF 1.0
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@						say single line HELP_TEXT_BAYWATCH_25		//#test.chl:51
	PUSHB true
	PUSHB false
//@					SPEED of Boy = 0.5		//#test.chl:56
	PUSHF 0.5

//@					move Boy position to [VillagerHutPos]		//#test.chl:58
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)
					
//@				elsif land height at [Boy] <= 0		//#test.chl:60
	JMP lblA
lblC:
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	LEQ
	JZ lblA
//@					set Boy position to [Boy]		//#test.chl:61
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@					Boy play ANM_P_SWIM2 loop -1		//#test.chl:62
	PUSHF [Boy]
	PUSHO [Boy]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
//@					InWater=1		//#test.chl:64
	PUSHF [InWater]
	POPI
	PUSHF 1.0
	POPF InWater
lblA:

//@		if HEALTH of Boy <= 0		//#test.chl:69
	PUSHI 1
	PUSHF [Boy]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ lblD
//@			if BaywatchMurder == 0		//#test.chl:70
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 0.0
	EQ
	JZ lblE
//@					eject evil spirit		//#test.chl:72
	PUSHI 2
//@					say HELP_TEXT_BAYWATCH_31		//#test.chl:73
	PUSHB false

//@					eject good spirit		//#test.chl:76
	PUSHI 1
//@					say HELP_TEXT_BAYWATCH_32		//#test.chl:77
	PUSHB false
lblE:
//@			Murdered = 1		//#test.chl:84
	PUSHF [Murdered]
	POPI
	PUSHF 1.0
	POPF Murdered
//@			if InWater == 1		//#test.chl:85
	PUSHF [InWater]
	PUSHF 1.0
	EQ
	JZ lblD
lblD:

//@	end while		//#test.chl:90
	JMP lblF
lbl9:
	ENDEXCEPT
	JMP lbl10
lbl8:
	ITEREXCEPT
lbl10:

//@	if Murdered == 0		//#test.chl:92
	PUSHF [Murdered]
	PUSHF 0.0
	EQ
	JZ lbl11
lbl11:

//@end script BaywatchSwimmingBoy		//#test.chl:96
	ENDEXCEPT
	JMP lbl12
lbl7:
	ITEREXCEPT
lbl12:
	END

global BaywatchSwimmingCounter


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
	EXCEPT lbl13
	PUSHF 2781.1104
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3053.49
	CASTC
	POPF VillagerHutPos
//@	SwimPos = marker at [2964.223, 0.000, 3084.874]		//#test.chl:102
	PUSHF 2964.2229
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.874
	CASTC
	POPF SwimPos
//@	SwimPos2 = marker at [2968.614, 0.000, 3089.957]		//#test.chl:103
	PUSHF 2968.614
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3089.957
	CASTC
	POPF SwimPos2
//@	SwimPos3 = marker at [2969.778, 0.000, 3082.155]		//#test.chl:104
	PUSHF 2969.7781
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3082.155
	CASTC
	POPF SwimPos3
//@	SwimPos4 = marker at [2962.422, 0.000, 3078.884]  		//#test.chl:105
	PUSHF 2962.4221
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3078.884
	CASTC
	POPF SwimPos4
//@	SwimPos5 = marker at [2960.591, 0.000, 3084.722]		//#test.chl:106
	PUSHF 2960.5911
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3084.7219
	CASTC
	POPF SwimPos5


//@	ShorePos = marker at [2943.603, 0.000, 3095.944]		//#test.chl:109
	PUSHF 2943.603
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 3095.9441
	CASTC
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
	PUSHF 2786.5073
	CASTC
	PUSHF 50.919998
	CASTC
	PUSHF 3064.325
	CASTC
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
	PUSHF 2854.7266
	CASTC
	PUSHF 50.396599
	CASTC
	PUSHF 3056.9429
	CASTC
	POPF RewardPos
//@	RewardCameraPos = marker at [2825.0017, 66.7546, 3067.1838]		//#test.chl:126
	PUSHF 2825.0017
	CASTC
	PUSHF 66.754601
	CASTC
	PUSHF 3067.1838
	CASTC
	POPF RewardCameraPos
//@	RewardCameraFoc = marker at [2854.1843, 50.8918, 3054.0120]		//#test.chl:127
	PUSHF 2854.1843
	CASTC
	PUSHF 50.8918
	CASTC
	PUSHF 3054.012
	CASTC
	POPF RewardCameraFoc

//@start		//#test.chl:129
	FREE
//@	BaywatchSwimmingCounter=5		//#test.chl:130
	PUSHF [BaywatchHomeCounter]
	POPI
	PUSHF 5.0
	POPF BaywatchHomeCounter

//@	Influence=create anti influence at position [SwimPos] radius 30		//#test.chl:132
	PUSHF [Influence]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 30.0
	POPF Influence

//@	Highlight = create highlight HIGHLIGHT_CHALLENGE at [VillagerHutPos]		//#test.chl:134
	PUSHF [Highlight]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Highlight
//@	run script ChallengeHighlightNotify(Highlight, VillagerHutPos, variable EVIL_ADVISOR, variable HELP_TEXT_GENERAL_CHALLENGE_START_03)		//#test.chl:135
	PUSHF [Highlight]
	PUSHF [VillagerHutPos]
	CASTF
	CASTF

//@	Mother = create VILLAGER VILLAGER_INFO_INDIAN_HOUSEWIFE_FEMALE at [VillagerHutPos]		//#test.chl:137
	PUSHF [Mother]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Mother

//@	BaywatchHomeCounter = 0		//#test.chl:139
	PUSHF [help_text_retake_aztec_village_quest_reminder_02]
	POPI
	PUSHF 0.0
	POPF help_text_retake_aztec_village_quest_reminder_02

//@	Boy1 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos]		//#test.chl:141
	PUSHF [Boy1]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Boy1
//@	Boy2 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos2]		//#test.chl:142
	PUSHF [Boy2]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Boy2
//@	Boy3 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos3]		//#test.chl:143
	PUSHF [Boy3]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Boy3
//@	Boy4 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos4]		//#test.chl:144
	PUSHF [Boy4]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Boy4
//@	Boy5 = create VILLAGER VILLAGER_INFO_INDIAN_FARMER_MALE at [SwimPos5]		//#test.chl:145
	PUSHF [Boy5]
	POPI
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	POPF Boy5

//@	run background script BaywatchSwimmingBoy(Boy1, VillagerHutPos)		//#test.chl:147
	PUSHF [Boy1]
	PUSHF [VillagerHutPos]
//@	run background script BaywatchSwimmingBoy(Boy2, VillagerHutPos)		//#test.chl:148
	PUSHF [Boy2]
	PUSHF [VillagerHutPos]
//@	run background script BaywatchSwimmingBoy(Boy3, VillagerHutPos)		//#test.chl:149
	PUSHF [Boy3]
	PUSHF [VillagerHutPos]
//@	run background script BaywatchSwimmingBoy(Boy4, VillagerHutPos)		//#test.chl:150
	PUSHF [Boy4]
	PUSHF [VillagerHutPos]
//@	run background script BaywatchSwimmingBoy(Boy5, VillagerHutPos)		//#test.chl:151
	PUSHF [Boy5]
	PUSHF [VillagerHutPos]
//@	run background script BaywatchSwimmingLaugh(SwimPos)		//#test.chl:152
	PUSHF [SwimPos]
//@		enable Mother high gfx detail		//#test.chl:156
	PUSHB true

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
	PUSHF 0.5
//@		move Mother position to [SoapBox]		//#test.chl:162
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	SYS MOVE_GAME_THING	//[5, 0] (Object object, Coord position, float radius)

//@		Mother play ANM_P_LOOKING_FOR_SOMETHING loop 1		//#test.chl:166
	PUSHF [Mother]
	PUSHO [Mother]
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
	PUSHB false
//@		wait 0.1 seconds		//#test.chl:175
	PUSHF 0.1

		// Cut to children swimming

//@		set camera position to [2960.404, 6.550, 3114.483] 		//#test.chl:179
	PUSHF 2960.4041
	CASTC
	PUSHF 6.5500002
	CASTC
	PUSHF 3114.4829
	CASTC
//@		set camera focus to [2958.053, 3.424, 3097.387] 		//#test.chl:180
	PUSHF 2958.053
	CASTC
	PUSHF 3.424
	CASTC
	PUSHF 3097.387
	CASTC

//@		snapshot challenge success 0.0 alignment 0 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:182
	PUSHB false
	PUSHF 0.0
	PUSHF 0.0
	CASTF

//@		say single line HELP_TEXT_BAYWATCH_23		//#test.chl:184
	PUSHB true
	PUSHB false
		
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

		// Cut back to Mother

//@		set camera position to [2790.691, 53.522, 3069.138]		//#test.chl:194
	PUSHF 2790.6909
	CASTC
	PUSHF 53.521999
	CASTC
	PUSHF 3069.1379
	CASTC
//@		set camera focus to [2783.190, 50.229, 3058.410]		//#test.chl:195
	PUSHF 2783.1899
	CASTC
	PUSHF 50.229
	CASTC
	PUSHF 3058.4099
	CASTC

//@		SPEED of Mother = 0.2		//#test.chl:197
	PUSHF 0.2

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

//@		disable Mother high gfx detail		//#test.chl:204
	PUSHB false
//@		position [SoapBox]		//#test.chl:209
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@		float 6		//#test.chl:210
	PUSHF 6.0
//@		ulong 4, 20		//#test.chl:211
	PUSHF 4.0
	PUSHF 20.0

//@	wait 5 seconds		//#test.chl:213
	PUSHF 5.0
//
	EXCEPT lbl14

//@	while BaywatchFinished == 0		//#test.chl:215
lbl28:
	PUSHF [BaywatchFinished]
	PUSHF 0.0
	EQ
	JZ lbl15

//@		if BaywatchHomeCounter == 5	and HEALTH of Mother > 0		//#test.chl:217
	PUSHF [help_text_retake_aztec_village_quest_reminder_02]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ lbl16
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:219
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:220
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
//@				say single line HELP_TEXT_BAYWATCH_27		//#test.chl:221
	PUSHB true
	PUSHB false
//@				BaywatchFinished = 1		//#test.chl:224
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

				// Reward - Big Spell Dispenser
//@				run script GiveSpellDispenserReward(RewardPos, variable MAGIC_TYPE_CREATURE_SPELL_BIG, 180, 1, 0)		//#test.chl:227
	PUSHF [RewardPos]
	CASTF
	PUSHF 180.0
	PUSHF 1.0
	PUSHF 0.0
//@				move camera position to [RewardCameraPos] time 5		//#test.chl:228
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 5.0
	SYS MOVE_CAMERA_POSITION	//[4, 0] (Coord position, float time)
//@				move camera focus to [RewardCameraFoc] time 2		//#test.chl:229
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 2.0
	SYS MOVE_CAMERA_FOCUS	//[4, 0] (Coord position, float time)
//@				snapshot challenge success 1 alignment 1 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:231
	PUSHB false
	PUSHF 1.0
	PUSHF 1.0
	CASTF

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother > 0		//#test.chl:234
	JMP lbl17
lbl16:
	PUSHF [BaywatchHomeCounter]
	PUSHF 0.0
	EQ
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 5.0
	LT
	AND
	PUSHF [help_text_retake_aztec_village_quest_reminder_02]
	PUSHF 5.0
	PUSHF [BaywatchSwimmingCounter]
	SUBF
	EQ
	AND
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ lbl18
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:236
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:237
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC
//@				update snapshot success 1 alignment -0.2 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:238
	PUSHF 1.0
	PUSHF 0.2
	NEG
	CASTF
//@				say single line HELP_TEXT_BAYWATCH_28		//#test.chl:239
	PUSHB true
	PUSHB false

//@				BaywatchFinished = 3		//#test.chl:242
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished

//@		elsif BaywatchMurder == 5 and HEALTH of Mother > 0		//#test.chl:245
	JMP lbl17
lbl18:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	AND
	JZ lbl19
//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:248
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:249
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC

//@				update snapshot success 1 alignment -0.6 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:251
	PUSHF 1.0
	PUSHF 0.6
	NEG
	CASTF

//@				say	single line HELP_TEXT_BAYWATCH_29		//#test.chl:253
	PUSHB true
	PUSHB false

//@				eject evil spirit		//#test.chl:256
	PUSHI 2
//@				say single line HELP_TEXT_BAYWATCH_30		//#test.chl:257
	PUSHB true
	PUSHB false

//@				BaywatchFinished = 2		//#test.chl:260
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 2.0
	POPF BaywatchFinished

//@		elsif BaywatchMurder == 5 and HEALTH of Mother <= 0		//#test.chl:264
	JMP lbl17
lbl19:
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 5.0
	EQ
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	AND
	JZ lbl1A

//@				eject evil spirit		//#test.chl:268
	PUSHI 2

				//"A whole family slaughtered, nice going!"
//@				say single line HELP_TEXT_BAYWATCH_33		//#test.chl:271
	PUSHB true
	PUSHB false

//@				update snapshot details success 1 alignment -1 HELP_TEXT_TITLE_01 		//#test.chl:273
	PUSHF 1.0
	PUSHF 1.0
	NEG
	PUSHB false

//@				BaywatchFinished = 3		//#test.chl:277
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 3.0
	POPF BaywatchFinished

//@		elsif BaywatchSwimmingCounter == 0 and BaywatchMurder < 5 and BaywatchHomeCounter == 5 - BaywatchMurder and HEALTH of Mother <= 0		//#test.chl:281
	JMP lbl17
lbl1A:
	PUSHF [BaywatchHomeCounter]
	PUSHF 0.0
	EQ
	PUSHF [BaywatchSwimmingCounter]
	PUSHF 5.0
	LT
	AND
	PUSHF [help_text_retake_aztec_village_quest_reminder_02]
	PUSHF 5.0
	PUSHF [BaywatchSwimmingCounter]
	SUBF
	EQ
	AND
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	AND
	JZ lbl17

//@				set camera position to [2794.7925, 58.5139, 3067.1104]		//#test.chl:285
	PUSHF 2794.7925
	CASTC
	PUSHF 58.513901
	CASTC
	PUSHF 3067.1104
	CASTC
//@				set camera focus to [2784.4382, 51.3200, 3054.3350]		//#test.chl:286
	PUSHF 2784.4382
	CASTC
	PUSHF 51.32
	CASTC
	PUSHF 3054.335
	CASTC

//@				update snapshot success 1 alignment -0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:288
	PUSHF 1.0
	PUSHF 0.8
	NEG
	CASTF

//@				eject good spirit		//#test.chl:290
	PUSHI 1

				//"Oh how terrible.. orphans."
//@				say single line HELP_TEXT_BAYWATCH_34		//#test.chl:293
	PUSHB true
	PUSHB false

//@				BaywatchFinished = 4		//#test.chl:296
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished
lbl17:


//@		if camera position near [SwimPos] radius 20 and SwimPos viewed and Fun == 0		//#test.chl:302
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 20.0
	AND
	PUSHF [Fun]
	PUSHF 0.0
	EQ
	AND
	JZ lbl1B

//@				say single line HELP_TEXT_BAYWATCH_24		//#test.chl:306
	PUSHB true
	PUSHB false

//@				Fun = 1		//#test.chl:309
	PUSHF [Fun]
	POPI
	PUSHF 1.0
	POPF Fun
lbl1B:

//@		if BaywatchSwimmingCounter == 5 and [Mother] near [SwimPos] radius 40 and Mother is not HELD and not Mother in MyCreature hand		//#test.chl:314
	PUSHF [BaywatchHomeCounter]
	PUSHF 5.0
	EQ
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 40.0
	AND
	AND
	AND
	JZ lbl1C

//@			if HEALTH of Mother > 0		//#test.chl:316
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl1D

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.chl:320
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.chl:321
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC

//@					say single line HELP_TEXT_BAYWATCH_26		//#test.chl:323
	PUSHB true
	PUSHB false

//@					set fade red 0 green 0 blue 0 time 2		//#test.chl:326
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)

//@					set Mother position to [SoapBox]		//#test.chl:329
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)

//@					if HEALTH of Boy1 > 0		//#test.chl:331
	PUSHI 1
	PUSHF [Boy1]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl1E
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.chl:332
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
//@						Boy1 play ANM_P_AMBIENT1 loop -1		//#test.chl:333
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl1E:

//@					if HEALTH of Boy2 > 0		//#test.chl:336
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl1F
//@						set Boy2 position to [SoapBox] + [1,0,1]		//#test.chl:337
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
//@						Boy2 play ANM_P_AMBIENT2 loop -1		//#test.chl:338
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl1F:

//@					if HEALTH of Boy3 > 0		//#test.chl:341
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl20
//@						set Boy3 position to [SoapBox] + [1,0,0]		//#test.chl:342
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
//@						Boy3 play ANM_P_AMBIENT1 loop -1		//#test.chl:343
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl20:

//@					if HEALTH of Boy4 > 0		//#test.chl:346
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl21
//@						set Boy4 position to [SoapBox] + [-1,0,1]		//#test.chl:347
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
//@						Boy4 play ANM_P_CROWD_LOST loop -1		//#test.chl:348
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl21:

//@					if HEALTH of Boy5 > 0		//#test.chl:351
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl22
//@						set Boy5 position to [SoapBox] + [-1,0,0]		//#test.chl:352
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
//@						Boy5 play ANM_P_CROWD_UNIMPRESSED_1 loop -1		//#test.chl:353
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl22:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.chl:356
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.chl:357
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC

//@					set fade in time 2		//#test.chl:361
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)

//@					say single line HELP_TEXT_BAYWATCH_27		//#test.chl:364
	PUSHB true
	PUSHB false
//@					wait 0.1 seconds		//#test.chl:368
	PUSHF 0.1

//@					snapshot challenge success 1 alignment 0.8 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:370
	PUSHB false
	PUSHF 1.0
	PUSHF 0.8
	CASTF

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

//@					BaywatchFinished = 1		//#test.chl:377
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 1.0
	POPF BaywatchFinished

//@			elsif HEALTH of Mother <= 0		//#test.chl:381
	JMP lbl1C
lbl1D:
	PUSHI 1
	PUSHF [Mother]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	LEQ
	JZ lbl1C

//@					set camera position to [2963.1555, 11.8798, 3055.2534]		//#test.chl:385
	PUSHF 2963.1555
	CASTC
	PUSHF 11.8798
	CASTC
	PUSHF 3055.2534
	CASTC
//@					set camera focus to [2962.0737, 0.8419, 3081.0688]		//#test.chl:386
	PUSHF 2962.0737
	CASTC
	PUSHF 0.8419
	CASTC
	PUSHF 3081.0688
	CASTC

					//"AAaahhh !!!!"
//@					say single line HELP_TEXT_BAYWATCH_35		//#test.chl:389
	PUSHB true
	PUSHB false

//@					set fade red 0 green 0 blue 0 time 2		//#test.chl:392
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 0.0
	PUSHF 2.0
	SYS SET_FADE	//[4, 0] (float red, float green, float blue, float time)

//@					set Mother position to [SoapBox]		//#test.chl:395
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@					Mother play ANM_P_DEAD1 loop -1		//#test.chl:396
	PUSHF [Mother]
	PUSHO [Mother]
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
	JZ lbl23
//@						set Boy1 position to [SoapBox] + [0,0,1]		//#test.chl:399
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
//@						set Boy1 focus to [Mother]		//#test.chl:400
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@						Boy1 play ANM_P_INSPECT_OBJECT_1 loop -1		//#test.chl:401
	PUSHF [Boy1]
	PUSHO [Boy1]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl23:

//@					if HEALTH of Boy2 > 0		//#test.chl:404
	PUSHI 1
	PUSHF [Boy2]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl24
//@						set Boy2 position to [SoapBox] + [0,0,-1]		//#test.chl:405
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 0.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
//@						set Boy2 focus to [Mother]		//#test.chl:406
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@						Boy2 play ANM_P_INSPECT_OBJECT_2 loop -1		//#test.chl:407
	PUSHF [Boy2]
	PUSHO [Boy2]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl24:

//@					if HEALTH of Boy3 > 0		//#test.chl:410
	PUSHI 1
	PUSHF [Boy3]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl25
//@						set Boy3 position to [SoapBox] + [-1,0,-1]		//#test.chl:411
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	NEG
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
//@						set Boy3 focus to [Mother]		//#test.chl:412
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@						Boy3 play ANM_P_MOURNING loop -1		//#test.chl:413
	PUSHF [Boy3]
	PUSHO [Boy3]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl25:

//@					if HEALTH of Boy4 > 0		//#test.chl:416
	PUSHI 1
	PUSHF [Boy4]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl26
//@						set Boy4 position to [SoapBox] + [1,0,-1]		//#test.chl:417
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	NEG
	CASTC
//@						set Boy4 focus to [Mother]		//#test.chl:418
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@						Boy4 play ANM_P_OVERWORKED1 loop -1		//#test.chl:419
	PUSHF [Boy4]
	PUSHO [Boy4]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl26:

//@					if HEALTH of Boy5 > 0		//#test.chl:422
	PUSHI 1
	PUSHF [Boy5]
	SYS GET_PROPERTY	//[2, 1] (SCRIPT_OBJECT_PROPERTY_TYPE prop, Object object) returns (int|float)
	PUSHF 0.0
	GT
	JZ lbl27
//@						set Boy5 position to [SoapBox] + [1,0,1]		//#test.chl:423
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
	PUSHF 1.0
	CASTC
	PUSHF 0.0
	CASTC
	PUSHF 1.0
	CASTC
//@						set Boy5 focus to [Mother]		//#test.chl:424
	SYS GET_POSITION	//[1, 3] (Object object) returns (Coord)
//@						Boy5 play ANM_P_SCARED_STIFF loop -1		//#test.chl:425
	PUSHF [Boy5]
	PUSHO [Boy5]
	PUSHF 1.0
	NEG
	CASTI
	SYS SET_SCRIPT_ULONG	//[3, 0] (ObjectObj object, int animation, int loop)
	PUSHI 200
	SYS SET_SCRIPT_STATE	//[2, 0] (Object object, VILLAGER_STATES state)
lbl27:

//@					set camera position to [2789.8372, 54.1367, 3066.6670]		//#test.chl:428
	PUSHF 2789.8372
	CASTC
	PUSHF 54.1367
	CASTC
	PUSHF 3066.667
	CASTC
//@					set camera focus to [2786.7651, 51.4744, 3064.1111]		//#test.chl:429
	PUSHF 2786.7651
	CASTC
	PUSHF 51.4744
	CASTC
	PUSHF 3064.1111
	CASTC

//@					set fade in time 2		//#test.chl:431
	PUSHF 2.0
	SYS SET_FADE_IN	//[1, 0] (float duration)
//@					wait 0.1 seconds		//#test.chl:435
	PUSHF 0.1

//@					snapshot challenge success 1 alignment -0.5 HELP_TEXT_TITLE_01 StandardReminder(variable HELP_TEXT_BAYWATCH_36)		//#test.chl:437
	PUSHB false
	PUSHF 1.0
	PUSHF 0.5
	NEG
	CASTF

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

//@					BaywatchFinished = 4		//#test.chl:444
	PUSHF [BaywatchFinished]
	POPI
	PUSHF 4.0
	POPF BaywatchFinished
lbl1C:

//@	end while		//#test.chl:451
	JMP lbl28
lbl15:
	ENDEXCEPT
	JMP lbl29
lbl14:
	ITEREXCEPT
lbl29:

//@	stop script "BaywatchSwimmingBoy"		//#test.chl:453
	PUSHI 57	//"BaywatchSwimmingBoy"
//@	stop script "BaywatchSwimmingLaugh"		//#test.chl:454
	PUSHI 77	//"BaywatchSwimmingLaugh"


//@end script BaywatchMain		//#test.chl:457
	ENDEXCEPT
	JMP lbl2A
lbl13:
	ITEREXCEPT
lbl2A:
	END


AUTORUN


// Creature Animation Header

#ifndef __CREATURE_SPEC_H__
#define __CREATURE_SPEC_H__

enum CREATURE_ANIMATIONS
{
	C_MOVE_STAND,                     // 0
	C_MOVE_WALK,                      // 1
	C_MOVE_RUN,                       // 2
	C_MOVE_R_SPIN_0,                  // 3
	C_MOVE_R_SPIN_90,                 // 4
	C_MOVE_R_SPIN_180,                // 5
	C_MOVE_L_SPIN_0,                  // 6
	C_MOVE_L_SPIN_90,                 // 7
	C_MOVE_L_SPIN_180,                // 8
	C_MOVE_R_JUMP_L,                  // 9
	C_MOVE_R_STEP_0,                  // 10
	C_MOVE_R_STEP_90,                 // 11
	C_MOVE_R_STEP_180,                // 12
	C_MOVE_L_STEP_0,                  // 13
	C_MOVE_L_STEP_90,                 // 14
	C_MOVE_L_STEP_180,                // 15
	C_FACE_SMILE,                     // 16
	C_FACE_GRIMACE,                   // 17
	C_FACE_GROWL,                     // 18
	C_FACE_SCARED,                    // 19
	C_FACE_SAD,                       // 20
	C_FACE_AMAZED,                    // 21
	C_FACE_PUZZLED,                   // 22
	C_FACE_LAUGH,                     // 23
	C_FACE_OOH,                       // 24
	C_FACE_AAH,                       // 25
	C_FACE_SPARE1,                    // 26
	C_FACE_SPARE2,                    // 27
	C_STATIC_START_SLEEP,             // 28
	C_STATIC_SLEEP,                   // 29
	C_STATIC_END_SLEEP,               // 30
	C_STATIC_START_POO,               // 31
	C_STATIC_POO,                     // 32
	C_STATIC_END_POO,                 // 33
	C_STATIC_START_PUKE,              // 34
	C_STATIC_PUKE,                    // 35
	C_STATIC_END_PUKE,                // 36
	C_STATIC_START_SIT,               // 37
	C_STATIC_SIT,                     // 38
	C_STATIC_END_SIT,                 // 39
	C_STATIC_START_CAST,              // 40
	C_STATIC_CAST,                    // 41
	C_STATIC_END_CAST,                // 42
	C_STATIC_START_CAST_UP,           // 43
	C_STATIC_CAST_UP,                 // 44
	C_STATIC_END_CAST_UP,             // 45
	C_STATIC_START_SCATTER,           // 46
	C_STATIC_SCATTER,                 // 47
	C_STATIC_END_SCATTER,             // 48
	C_STATIC_START_SPARE,             // 49
	C_STATIC_SPARE,                   // 50
	C_STATIC_END_SPARE,               // 51
	C_INDIVIDUAL_SUMMON,              // 52
	C_INDIVIDUAL_ANGRY,               // 53
	C_INDIVIDUAL_HUNGRY,              // 54
	C_INDIVIDUAL_HAPPY,               // 55
	C_INDIVIDUAL_SAD,                 // 56
	C_INDIVIDUAL_TIRED,               // 57
	C_INDIVIDUAL_HOT,                 // 58
	C_INDIVIDUAL_COLD,                // 59
	C_INDIVIDUAL_SCRATCH,             // 60
	C_INDIVIDUAL_FRIGHTENED,          // 61
	C_INDIVIDUAL_SNEEZE,              // 62
	C_INDIVIDUAL_CONFUSED,            // 63
	C_INDIVIDUAL_FEELING_NICE,        // 64
	C_INDIVIDUAL_IMPRESS,             // 65
	C_INDIVIDUAL_NEED_A_POO,          // 66
	C_INDIVIDUAL_FEEL_PLAYFUL,        // 67
	C_INDIVIDUAL_PLAY_ACTION,         // 68
	C_INDIVIDUAL_LOOK_AT_ME,          // 69
	C_INDIVIDUAL_TAUNT,               // 70
	C_INDIVIDUAL_DRINK,               // 71
	C_INDIVIDUAL_FRIENDLY_WAVE,       // 72
	C_INDIVIDUAL_EMBARRASSED,         // 73
	C_INDIVIDUAL_PICK_ME,             // 74
	C_LOOK_RIGHT_LEFT,                // 75
	C_LOOK_DOWN_UP,                   // 76
	C_LOOK_SIT_R_L,                   // 77
	C_LOOK_SIT_D_U,                   // 78
	C_LOOK_HATE_LOVE,                 // 79
	C_LOOK_SEEK_ARM,                  // 80
	C_PICKUP_FRONT_RIGHT,             // 81
	C_PICKUP_FRONT_LEFT,              // 82
	C_PICKUP_BACK_RIGHT,              // 83
	C_PICKUP_BACK_LEFT,               // 84
	C_PICKUP_HOLD,                    // 85
	C_PICKUP_FROM_HAND,               // 86
	C_PICKUP_SPARE0,                  // 87
	C_PICKUP_SPARE1,                  // 88
	C_PICKUP_SPARE2,                  // 89
	C_PICKUP_SPARE3,                  // 90
	C_THROW_HURL_FLAT,                // 91
	C_THROW_HURL_HIGH,                // 92
	C_THROW_BOWL,                     // 93
	C_THROW_SPARE2,                   // 94
	C_OBJECT_REMOVE_DISCARD,          // 95
	C_OBJECT_REMOVE_EAT,              // 96
	C_OBJECT_REMOVE_PUT_DOWN,         // 97
	C_OBJECT_REMOVE_GENTLE_LOB,       // 98
	C_OBJECT_REMOVE_SPARE,            // 99
	C_OBJECT_KEEP_STROKE,             // 100
	C_OBJECT_KEEP_SHAKE,              // 101
	C_OBJECT_KEEP_SMELL,              // 102
	C_OBJECT_KEEP_EXAMINE,            // 103
	C_OBJECT_KEEP_SPARE1,             // 104
	C_OBJECT_KEEP_SPARE2,             // 105
	C_FIGHT_EXTRA_FAINT,              // 106
	C_FIGHT_EXTRA_GET_UP,             // 107
	C_FIGHT_EXTRA_SPARE0,			  // 108
	C_FIGHT_EXTRA_CREATION,           // 109
	C_FIGHT_EXTRA_START_CAST,         // 110
	C_FIGHT_EXTRA_CAST,               // 111
	C_FIGHT_EXTRA_END_CAST,           // 112
	C_DESTROY_FRONT_RIGHT,            // 113
	C_DESTROY_FRONT_LEFT,             // 114
	C_DESTROY_BACK_RIGHT,             // 115
	C_DESTROY_BACK_LEFT,              // 116
	C_DESTROY_KICK_LOW,               // 117
	C_DESTROY_SPARE0,                 // 118
	C_EXTRA_STANDS_RELAXED,           // 119
	C_EXTRA_STANDS_SPARE1,            // 120
	C_EXTRA_STANDS_SPARE2,            // 121
	C_FIGHT_START,                    // 122
	C_FIGHT_STANCE,                   // 123
	C_FIGHT_FINISH,                   // 124
	C_FIGHT_ATTACK_HIGH,              // 125
	C_FIGHT_ATTACK_EYE,               // 126
	C_FIGHT_ATTACK_BELLY,             // 127
	C_FIGHT_ATTACK_PELVIS,            // 128
	C_FIGHT_ATTACK_KNEE,              // 129
	C_FIGHT_ATTACK_SPECIAL,           // 130
	C_FIGHT_ATTACK_HIGH2,             // 131
	C_FIGHT_ATTACK_EYE2,              // 132
	C_FIGHT_ATTACK_BELLY2,            // 133
	C_FIGHT_ATTACK_PELVIS2,           // 134
	C_FIGHT_ATTACK_KNEE2,             // 135
	C_FIGHT_ATTACK_SPECIAL2,          // 136
	C_FIGHT_STEP_FORWARD,             // 137
	C_FIGHT_STEP_BACK,                // 138
	C_FIGHT_STEP_RIGHT,               // 139
	C_FIGHT_STEP_LEFT,                // 140
	C_FIGHT_SPARE3,                   // 141
	C_FIGHT_START_BLOCK,              // 142
	C_FIGHT_BLOCK,                    // 143
	C_FIGHT_END_BLOCK,                // 144
	C_DANCE_START,                    // 145
	C_DANCE_FINISH,                   // 146
	C_DANCE_A,                        // 147
	C_DANCE_B,                        // 148
	C_DANCE_C,                        // 149
	C_DANCE_D,                        // 150
	C_DANCE_E,                        // 151
	C_DANCE_SPARE1,                   // 152
	C_DANCE_SPARE2,                   // 153
	C_REWARD_HEAD,                    // 154
	C_REWARD_ARMPIT_R,                // 155
	C_REWARD_NO_1,                    // 156
	C_REWARD_BELLY,                   // 157
	C_REWARD_GROIN,                   // 158
	C_REWARD_FOOT_R,                  // 159
	C_REWARD_NO_2,                    // 160
	C_REWARD_HAND_R,                  // 161
	C_REWARD_NO_3,                    // 162
	C_REWARD_SPARE1,                  // 163
	C_REWARD_SPARE2,                  // 164
	C_PUNISH_HEAD_SLAP_R,             // 165
	C_PUNISH_NO_1,                    // 166
	C_PUNISH_WAIST_SLAP_R,            // 167
	C_PUNISH_NO_2,                    // 168
	C_PUNISH_FEET_SLAP_R,             // 169
	C_PUNISH_NO_3,                    // 170
	C_PUNISH_HEAD_GENTLE_R,           // 171
	C_PUNISH_NO_4,                    // 172
	C_PUNISH_WAIST_GENTLE_R,          // 173
	C_PUNISH_NO_5,                    // 174
	C_PUNISH_FEET_GENTLE_R,           // 175
	C_PUNISH_NO_6,                    // 176
	C_PUNISH_SPARE1,                  // 177
	C_PUNISH_SPARE2,                  // 178
	C_RECOIL_HI,                      // 179
	C_RECOIL_HI_R,                    // 180
	C_RECOIL_HI_L,                    // 181
	C_RECOIL_HI_T,                    // 182
	C_RECOIL_HI_B,                    // 183
	C_RECOIL_MI,                      // 184
	C_RECOIL_MI_R,                    // 185
	C_RECOIL_MI_L,                    // 186
	C_RECOIL_MI_T,                    // 187
	C_RECOIL_MI_B,                    // 188
	C_RECOIL_LO,                      // 189
	C_RECOIL_LO_R,                    // 190
	C_RECOIL_LO_L,                    // 191
	C_RECOIL_LO_T,                    // 192
	C_RECOIL_LO_B,                    // 193
	C_RECOIL_HI_R_L,                  // 194
	C_RECOIL_HI_F_B,                  // 195
	C_RECOIL_LO_R_L,                  // 196
	C_RECOIL_LO_F_B,                  // 197
	C_RECOIL_BLOCK,                   // 198
	C_RECOIL_SPARE,                   // 199
	C_CONCURRENT_NOD,                 // 200
	C_CONCURRENT_SHAKE,               // 201
	C_CONCURRENT_YAWN,                // 202
	C_CONCURRENT_THIRSTY,             // 203
	C_CONCURRENT_SQUIRT_WATER,        // 204
	C_CONCURRENT_TALK,                // 205
	C_CONCURRENT_SPARE1,              // 206
	C_CONCURRENT_SPARE2,              // 207
	C_POINT_LO_LEFT,                  // 208
	C_POINT_LO_RIGHT,                 // 209
	C_POINT_HI_LEFT,                  // 210
	C_POINT_HI_RIGHT,                 // 211
	C_MISC_KISS_HI,                   // 212
	C_MISC_KISS_LO,                   // 213
	C_MISC_SCRIPT1,                   // 214
	C_MISC_SCRIPT2,                   // 215
	C_MISC_SCRIPT3,                   // 216
	C_MISC_HOWL,                      // 217
	C_MISC_LAUGH,                     // 218
	C_MISC_CRY,                       // 219
	C_MISC_AHA,                       // 220
	C_MISC_PRAY,                      // 221
	C_MISC_FUCK_YOU,                  // 222
	C_MISC_SPARE1,                    // 223
	C_MISC_SPARE2,                    // 224
	C_CATCH_STEP_RIGHT,               // 225
	C_CATCH_STEP_BACK,                // 226
	C_CATCH_STEP_FRONT,               // 227
	C_CATCH_HI_R,                     // 228
	C_CATCH_HI_L,                     // 229
	C_CATCH_LO_R,                     // 230
	C_CATCH_LO_L,                     // 231
};

#endif

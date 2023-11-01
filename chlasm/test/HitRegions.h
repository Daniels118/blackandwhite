#ifndef __HITREGIONS_H__
#define __HITREGIONS_H__

enum
{
	CTR_HIT_HIGH,
	CTR_HIT_MIDDLE,
	CTR_HIT_LOW,
	CTR_HIT_BLOCK,
	CTR_HIT_SPECIAL
};

enum
{
	CTR_FIGHT_NOTHING,
	CTR_FIGHT_BLOCKING,
	CTR_FIGHT_STEPPING,
	CTR_FIGHT_HITTING,
	CTR_FIGHT_CASTING,
	CTR_FIGHT_SPECIAL_MOVE,
	CTR_FIGHT_RECOIL_HIGH,
	CTR_FIGHT_RECOIL_MIDDLE,
	CTR_FIGHT_RECOIL_LOW,
	CTR_FIGHT_FAINTING,
	CTR_FIGHT_UNKNOWN_ACTION
};

enum
{
	FIGHT_CONTROL_AI_NEVER,
	FIGHT_CONTROL_AI_NOT_FOR_A_FEW_SECONDS,
	FIGHT_CONTROL_AI_FULL_ON
};

#endif //__HITREGIONS_H__
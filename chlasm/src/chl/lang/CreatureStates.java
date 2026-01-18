package chl.lang;

public enum CreatureStates implements LHEnum {
	ERROR,
	MOVE_TO_POS,
	FLEEING_FROM_OBJECT,
	LOOKING_AT_OBJECT,
	FOLLOWING_OBJECT,
	INSPECT_OBJECT,
	FLYING,
	LANDED,
	LOOK_AT_HAND,
	DEAD,
	SEARCH_FOR,
	PLAY_ANIM,
	FREE_LIFE,
	SLEEPING,
	ENTER_PEN,
	LEAVE_PEN,
	EATING,
	ATTACK,
	IN_MAGIC_HAND,
	IN_PEN;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

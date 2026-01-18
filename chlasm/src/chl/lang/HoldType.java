package chl.lang;

public enum HoldType implements LHEnum {
	NONE,
	/**eg. rock
	 */
	ABOVE,
	/**eg. fireball
	 */
	MAGIC,
	GRAIN,
	/**eg. small rock
	 */
	FINGERS,
	TREE,
	/**eg. pot
	 */
	SIDE,
	VILLAGER;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

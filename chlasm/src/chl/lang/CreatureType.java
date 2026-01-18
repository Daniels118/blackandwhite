package chl.lang;

public enum CreatureType implements LHEnum {
	APE,
	COW,
	TIGER,
	LEOPARD,
	WOLF,
	LION,
	HORSE,
	TORTOISE,
	ZEBRA,
	BEAR,
	POLAR_BEAR,
	SHEEP,
	CHIMP,
	OGRE,
	MANDRILL,
	RHINO,
	GORILLA,
	CHICKEN,
	CROCODILE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

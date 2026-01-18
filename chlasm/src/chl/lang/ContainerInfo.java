package chl.lang;

public enum ContainerInfo implements LHEnum {
	TOWN,
	PRAYER,
	CITADEL,
	FOREST;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

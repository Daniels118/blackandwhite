package chl.lang;

public enum JobActivity implements LHEnum {
	NONE,
	NURTURE,
	HARVEST,
	PLAN,
	EXECUTE;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

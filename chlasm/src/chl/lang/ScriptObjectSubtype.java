package chl.lang;

public enum ScriptObjectSubtype implements LHEnum {
	DEFAULT;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

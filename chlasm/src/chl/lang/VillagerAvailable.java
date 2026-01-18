package chl.lang;

public enum VillagerAvailable implements LHEnum {
	FREE(1),
	AT_HOME(3),
	NOT_FREE(4);
	
	private final int code;
	
	private VillagerAvailable(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

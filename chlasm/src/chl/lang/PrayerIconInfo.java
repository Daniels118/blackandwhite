package chl.lang;

public enum PrayerIconInfo implements LHEnum {
	NONE(-1),
	NORMAL(0);
	
	public final int code;
	
	private PrayerIconInfo(int code) {
		this.code = code;
	}

	@Override
	public int value() {
		return code;
	}
}

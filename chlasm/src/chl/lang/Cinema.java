package chl.lang;

public interface Cinema extends Camera, Dialogue {
	@Action("@null()")
	public static Cinema begin() {return null;}
}

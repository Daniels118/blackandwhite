package chl.lang;

public interface Camera extends AutoCloseable {
	@Action("@null()")
	public static Camera begin() {return null;}
	
	@Action("SET_CAMERA_FOCUS(position)")
	public void setFocus(Coord position);
	@Action("SET_CAMERA_FOCUS(x, y, z)")
	public void setFocus(float x, float y, float z);
	@Action("SET_CAMERA_POSITION(position)")
	public void setPosition(Coord position);
	@Action("SET_CAMERA_POSITION(x, y, z)")
	public void setPosition(float x, float y, float z);
	
	@Action("SET_FOCUS_AND_POSITION_FOLLOW(target, distance)")
	public void follow(LHObject target, float distance);
	
	@Action("SET_GAMESPEED(speed")
	public void setGameSpeed(float speed);
}

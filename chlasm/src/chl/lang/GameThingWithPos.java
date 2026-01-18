package chl.lang;

public interface GameThingWithPos extends GameThing {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_XPOS, this)")
	public float getX();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_XPOS, this, v)")
	public void  setX(float v);
	
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_YPOS, this)")
	public float getY();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_YPOS, this, v)")
	public void  setY(float v);
	
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ZPOS, this)")
	public float getZ();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ZPOS, this, v)")
	public void  setZ(float v);
	
	@Action("GET_POSITION(this)")
	public Coord getPosition();
	
	@Action("SET_POSITION(this, pos)")
	public void setPosition(Coord pos);
	@Action("SET_POSITION(this, x, y, z)")
	public void setPosition(float x, float y, float z);
	@Action("SET_POSITION(this, x, 0f, z)")
	public void setPosition(float x, float z);
}

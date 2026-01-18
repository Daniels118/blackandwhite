package chl.lang;

public interface Living extends LHObject {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_DEATH, this)")
	public boolean isDeath();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_AGE, this)")
	public float   getAge();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_AGE, this, v)")
	public void    setAge(float v);
	
	@Action("MOVE_GAME_THING(this, position, radius)")
	public void moveTo(Coord position, float radius);
	@Action("MOVE_GAME_THING(this, position, 0f)")
	public void moveTo(Coord position);
	@Action("MOVE_GAME_THING(this, x, 0f, z, 0f)")
	public void moveTo(float x, float z);
}

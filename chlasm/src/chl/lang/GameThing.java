package chl.lang;

public interface GameThing {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_THING_TYPE, this)")
	public int getThingType();
	
	@Action("GET_OBJECT_STATE(this)")
	public int getState();
	
	@Action("OBJECT_DELETE(this, mode)")
	public void delete(DeleteMode mode);
	@Action("OBJECT_DELETE(this, 0)")
	public void delete();
	
	@Action("THING_VALID(this)")
	public boolean exists();
	
	@Action("GAME_THING_CLICKED(this)")
	public boolean isClicked();
	
	@Action("GAME_THING_HIT(this)")
	public boolean isHit();
}

package chl.lang;

public interface ComputerPlayer extends LHObject {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ALIGNMENT, this)")
	public float getAlignment();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ALIGNMENT, this, v)")
	public void setAlignment(float v);
}

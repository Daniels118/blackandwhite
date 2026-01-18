package chl.lang;

public interface Abode extends LHObject {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_BUILT_PERCENTAGE, this)")
	public float   getBuiltPercentage();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_BUILT_PERCENTAGE, this, v)")
	public void    setBuiltPercentage(float v);
}

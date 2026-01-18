package chl.lang;

public interface Town extends Container {
	@Action("ID_ADULT_SIZE(this)")
	public float getAdultsCount();
	@Action("OBJECT_CAPACITY(this)")
	public float getCapacity();
	@Action("OBJECT_ADULT_CAPACITY(this)")
	public float getAdultCapacity();
	
	@Action("GET_TOWN_WITH_ID(id)")
	public static Town getById(float id) {return null;}
}

package chl.lang;

public interface Flock extends Container {
	@Action("CHANGE_INNER_OUTER_PROPERTIES(this, innerRadius, outerRadius, 0.0)")
	public void setProperties(float innerRadius, float outerRadius);
	
	@Action("FLOCK_ATTACH(object, this, asLeader)")
	public void attach(Living object, boolean asLeader);
	@Action("FLOCK_ATTACH(object, this, false)")
	public void attach(Living object);
	
	@Action("FLOCK_DETACH(object, this)")
	public void detach(LHObject object);
	
	@Action("GET_FIRST_IN_CONTAINER(this)")
	public Living getFirst();
	@Action("GET_NEXT_IN_CONTAINER(this, after)")
	public Living getNext(LHObject after);
	
	@Action("FLOCK_DISBAND(this)")
	public void disband();
	
	@Action("FLOCK_CREATE(position)")
	public static Flock create(Coord position) {return null;}
	@Action("FLOCK_CREATE(x, 0.0, z)")
	public static Flock create(float x, float z) {return null;}
}

package chl.lang;

public interface LHObject extends GameThingWithPos {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_HEALTH, this)")
	public float   getHealth();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_HEALTH, this, v)")
	public void    setHealth(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ANGLE, this)")
	public float   getAngle();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ANGLE, this, v)")
	public void    setAngle(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_XANGLE, this)")
	public float   getXAngle();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_XANGLE, this, v)")
	public void    setXAngle(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ZANGLE, this)")
	public float   getZAngle();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ZANGLE, this, v)")
	public void    setZAngle(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_FLYING, this)")
	public boolean isFlying();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_DROWNING, this)")
	public boolean isDrowning();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_MOVING, this)")
	public boolean isMoving();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_SCALE, this)")
	public float   getScale();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_SCALE, this, v)")
	public void    setScale(float v);
	/**Checks if the object is in hand. This returns a float value.
	 * @return 1.0 if the object is in hand, 0.0 otherwise.
	 */
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_IN_HAND, this)")
	public float   isInHand();
	
	/**Never used in story, not sure what this means, seems to always return false.
	 * @return always false?
	 */
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_IN_HAND_GRAB, this)")
	public boolean isInHandGrab();
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_SPEED, this)")
	public float   getSpeed();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_SPEED, this, v)")
	public void    setSpeed(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_STRENGTH, this)")
	public float   getStrength();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_STRENGTH, this, v)")
	public void    setStrength(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_HEIGHT, this)")
	public float   getHeight();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_HEIGHT, this, v)")
	public void    setHeight(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_MAX_HEIGHT, this)")
	public float   getMaxHeight();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_MAX_HEIGHT, this, v)")
	public void    setMaxHeight(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_PLAYER, this)")
	public float   getPlayer();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_PLAYER, this, v)")
	public void    setPlayer(float v);
	
	@Action("FLOCK_ATTACH(this, flock, asLeader)")
	public void attachTo(Flock flock, boolean asLeader);
	@Action("FLOCK_ATTACH(this, flock, false)")
	public void attachTo(Flock flock);
	@Action("FLOCK_DETACH(this, flock)")
	public void detachFrom(Flock flock);
	@Action("GET_OBJECT_FLOCK(this)")
	public Flock getFlock();
	
	@Action("GET_TARGET_OBJECT(this)")
	public LHObject getTarget();
}

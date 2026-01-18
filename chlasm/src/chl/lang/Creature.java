package chl.lang;

public interface Creature extends Living {
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ALIGNMENT, this)")
	public float   getAlignment();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_ALIGNMENT, this, v)")
	public void    setAlignment(float v);
	
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_RUNNING_SPEED, this)")
	public float   getRunningSpeed();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_RUNNING_SPEED, this, v)")
	public void    setRunningSpeed(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_DEFAULT_SPEED, this)")
	public float   getDefaultSpeed();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_DEFAULT_SPEED, this, v)")
	public void    setDefaultSpeed(float v);
	
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_WARMTH, this)")
	public float   getWarmth();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_WARMTH, this, v)")
	public void    setWarmth(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FATNESS, this)")
	public float   getFatness();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FATNESS, this, v)")
	public void    setFatness(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_ENERGY, this)")
	public float   getEnergy();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_ENERGY, this, v)")
	public void    setEnergy(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_ITCHINESS, this)")
	public float   getItchiness();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_ITCHINESS, this, v)")
	public void    setItchiness(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_AMOUNT_OF_POO, this)")
	public void    getAmountOfPoo(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_EXHAUSTION, this)")
	public float   getExhaustion();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_EXHAUSTION, this, v)")
	public void    setExhaustion(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_DEHYDRATION, this)")
	public float   getDehydration();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_DEHYDRATION, this, v)")
	public void    setDehydration(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FIGHT_HEALTH, this)")
	public float   getFightHealth();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FIGHT_HEALTH, this, v)")
	public void    setFightHealth(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_MIN_SIZE, this)")
	public float   getMinSize();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_MIN_SIZE, this, v)")
	public void    setMinSize(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_MAX_SIZE, this)")
	public float   getMaxSize();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_MAX_SIZE, this, v)")
	public void    setMaxSize(float v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_CAN_GO_THROUGH_VORTEX, this)")
	public boolean canGoThroughVortex();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_CAN_GO_THROUGH_VORTEX, this, v)")
	public void    setCanGoThroughVortex(boolean v);
	@Action("GET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FIGHT_POWER, this)")
	public float   getFightPower();
	@Action("SET_PROPERTY(SCRIPT_OBJECT_PROPERTY_TYPE_CREATURE_FIGHT_POWER, this, v)")
	public void    setFightPower(float v);
}

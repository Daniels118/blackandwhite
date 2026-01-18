package chl.lang;

public interface Timer extends GameThingWithPos {
	@Action("SET_TIMER_TIME(this, time)")
	public void setTime(float time);
	@Action("GET_TIMER_TIME_REMAINING(this)")
	public float getTimeRemaining();
	@Action("GET_TIMER_TIME_SINCE_SET(this)")
	public float getTimeSinceSet();
	
	@Action("CREATE_TIMER(timeout)")
	public static Timer create(float timeout) {return null;}
}

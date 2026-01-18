package chl.lang;

public final class Math {
	public static final float E = 2.718281828f;
	public static final float PI = 3.141592653f;
	
	private Math() {}
	
	@Action("@Math.abs(value)")
	public static float abs(float value) {return 0;}
	
	@Action("@Math.ceil(value)")
	public static float ceil(float value) {return 0;}
	
	@Action("@Math.copySign(magnitude, sign)")
	public static float copySign(float magnitude, float sign) {return 0;}
	
	@Action("@Math.cos(value)")
	public static float cos(float value) {return 0;}
	
	@Action("@Math.floor(value)")
	public static float floor(float value) {return 0;}
	
	@Action("@Math.hypot(x, y)")
	public static float hypot(float x, float y) {return 0;}
	
	@Action("@Math.max(a, b)")
	public static float max(float a, float b) {return 0;}
	
	@Action("@Math.min(a, b)")
	public static float min(float a, float b) {return 0;}
	
	@Action("RANDOM(0.0, 0.99999993)")
	public static float random() {return 0;}
	
	@Action("RANDOM(from, to)")
	public static float random(float min, float max) {return 0;}
	
	@Action("RANDOM_ULONG(from, to)")
	public static int random(int from, int to) {return 0;}
	
	@Action("@Math.reduceRadians(value)")
	public static float reduceRadians(float value) {return 0;}
	
	@Action("@Math.round(value)")
	public static float round(float value) {return 0;}
	
	@Action("@Math.signum(value)")
	public static float signum(float value) {return 0;}
	
	@Action("@Math.sin(value)")
	public static float sin(float value) {return 0;}
	
	@Action("SQUARE_ROOT(value)")
	public static float sqrt(float value) {return 0;}
	
	@Action("@Math.toDegrees(value)")
	public static float toDegrees(float value) {return 0;}
	
	@Action("@Math.toRadians(value)")
	public static float toRadians(float value) {return 0;}
}

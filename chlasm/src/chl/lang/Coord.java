package chl.lang;

/**This is a virtual class which represents a triplet of float values.
 * Instances of this class cannot be assigned to variables, they exist only on the stack.
 */
public final class Coord {
	@Action("@Coord(x, y, z)")
	public Coord(float x, float y, float z) {}
	@Action("@Coord(x, 0f, z)")
	public Coord(float x, float z) {}
	
	@Action("@Coord.add(this, coord)")
	public Coord add(Coord coord) {return null;}
	@Action("@Coord.add(this, x, y, z)")
	public Coord add(float x, float y, float z) {return null;}
	@Action("@Coord.add(this, x, 0f, z)")
	public Coord add(float x, float z) {return null;}
	@Action("@Coord.sub(this, coord)")
	public Coord sub(Coord coord) {return null;}
	@Action("@Coord.sub(this, x, y, z)")
	public Coord sub(float x, float y, float z) {return null;}
	@Action("@Coord.sub(this, x, 0f, z)")
	public Coord sub(float x, float z) {return null;}
	@Action("@Coord.mul(this, scale)")
	public Coord mul(float scale) {return null;}
	@Action("@Coord.div(this, scale)")
	public Coord div(float scale) {return null;}
	@Action("@Coord.neg(this)")
	public Coord neg() {return null;}
	
	@Action("GET_DISTANCE(this, position)")
	public float getDistance(Coord position) {return 0;}
	@Action("GET_DISTANCE(this, x, y, z)")
	public float getDistance(float x, float y, float z) {return 0;}
	@Action("GET_DISTANCE(this, x, 0f, z)")
	public float getDistance(float x, float z) {return 0;}
	
	@Action("@Coord.getX(this)")
	public float getX() {return 0;}
	@Action("@Coord.getY(this)")
	public float getY() {return 0;}
	@Action("@Coord.getZ(this)")
	public float getZ() {return 0;}
	
	@Action("@Coord.equals(this, position)")
	public boolean equals(Coord position) {return false;}
	@Action("@Coord.equals(this, x, y, z)")
	public boolean equals(float x, float y, float z) {return false;}
	@Action("@Coord.equals(this, x, 0f, z)")
	public boolean equals(float x, float z) {return false;}
}

package chl.lang;

public interface Container extends GameThingWithPos, Iterable<LHObject> {
	@Action("ID_SIZE(this)")
	public float getSize();
	@Action("ID_POISONED_SIZE(this)")
	public float getPoisonedSize();
}

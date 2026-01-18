package chl.lang;

public interface Mist extends GameThingWithPos {
	@Action("SET_MIST_FADE(this, float startScale, float endScale, float startTransparency, float endTransparency, float duration)")
	public void fade(float startScale, float endScale, float startTransparency, float endTransparency, float duration);
}

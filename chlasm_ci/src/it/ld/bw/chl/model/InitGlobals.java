package it.ld.bw.chl.model;

public class InitGlobals extends StructArray<InitGlobal> {
	@Override
	public Class<InitGlobal> getItemClass() {
		return InitGlobal.class;
	}
	
	@Override
	public String toString() {
		return items.toString();
	}
}

package chl.lang;

public enum MagicTreeType implements LHEnum {
	TREE_0,
	TREE_1,
	TREE_2,
	TREE_3;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

package chl.lang;

public enum TreeType implements LHEnum {
	TREE_1,
	TREE_2,
	TREE_3,
	TREE_4,
	TREE_5;
	
	@Override
	public int value() {
		return this.ordinal();
	}
}

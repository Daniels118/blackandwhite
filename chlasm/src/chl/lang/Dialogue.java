package chl.lang;

public interface Dialogue extends AutoCloseable {
	@Action("@null()")
	public static Dialogue begin() {return null;}
	
	@Action("TEMP_TEXT(singleLine, text, withInteraction)")
	public void say(String text, boolean withInteraction, boolean singleLine);
	@Action("TEMP_TEXT(false, text, withInteraction)")
	public void say(String text, boolean withInteraction);
	@Action("TEMP_TEXT(false, text, false)")
	public void say(String text);
	
	@Action("TEMP_TEXT_WITH_NUMBER(singleLine, format, number, withInteraction)")
	public void sayWithNumber(String format, float number, boolean withInteraction, boolean singleLine);
	@Action("TEMP_TEXT_WITH_NUMBER(false, format, number, withInteraction)")
	public void sayWithNumber(String format, float number, boolean withInteraction);
	@Action("TEMP_TEXT_WITH_NUMBER(false, format, number, false)")
	public void sayWithNumber(String format, float number);
	
	@Action("TEXT_READ()")
	public boolean hasBeenRead();
}

package chl.lang;

public final class Task {
	private Task() {}
	
	/**Start the given method in a new task running in the background.
	 * @param script the name of the method to execute in the new task.
	 * @param va_arg
	 */
	@Action("@Task.start(va_arg)")	//The script name will be converted to an instruction address and embedded in the instruction
	public static void start(String script, java.lang.Object...va_arg) {}
	
	/**Call the given method and wait until it ends. This is useful to call external scripts written in other languages, which
	 * cannot be referenced directly in java code.
	 * @param script
	 * @param va_arg
	 */
	@Action("@Task.call(va_arg)")	//The script name will be converted to an instruction address and embedded in the instruction
	public static void call(String script, java.lang.Object...va_arg) {}
	
	@Action("@Task.sleep(seconds)")
	public static void sleep(float seconds) {}
}

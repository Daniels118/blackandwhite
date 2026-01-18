package it.ld.bw.chl.lang.java;

import java.util.ArrayList;
import java.util.List;

import it.ld.bw.chl.model.NativeFunction;

/**
 * This class represents a way to call a native function mapping its parameters to a different position or with an implicit constant.
 */
public class FunctionMapping {
	public final NativeFunction nativeFunction;
	public final String customAction;
	public boolean isStatic = true;
	
	/**
	 * The parameters mapping ordered as they are expected by the native function.
	 */
	public final List<ParameterMapping> parameters = new ArrayList<>();
	
	public FunctionMapping(String action) {
		if (action.startsWith("@")) {
			this.nativeFunction = null;
			this.customAction = action;
		} else {
			this.nativeFunction = NativeFunction.valueOf(action);
			this.customAction = null;
		}
	}
	
	@Override
	public String toString() {
		return nativeFunction != null ? (nativeFunction.toString() + parameters) : customAction;
	}
	
	
	public static class ParameterMapping {
		/**
		 * The type of parameter. See {@linkplain Type}.
		 */
		public final Type type;
		
		/**
		 * The index of the parameter from the caller perspective.
		 */
		public final int source;
		/**
		 * The name of a constant for this parameter.
		 */
		public final String constant;
		public final int intval;
		public final float floatval;
		public final boolean boolval;
		public final boolean isVarArgs;
		
		public ParameterMapping(int source, boolean isVarArgs) {
			this.source = source;
			this.type = Type.CODE;
			this.constant = null;
			this.intval = 0;
			this.floatval = 0f;
			this.boolval = false;
			this.isVarArgs = isVarArgs;
		}
		
		public ParameterMapping(String constant) {
			this.source = -1;
			this.constant = constant;
			this.isVarArgs = false;
			if ("this".equals(constant)) {
				this.type = Type.THIS;
				this.intval = 0;
				this.floatval = 0f;
				this.boolval = false;
			} else if ("false".equals(constant)) {
				this.type = Type.BOOL;
				this.intval = 0;
				this.floatval = 0f;
				this.boolval = false;
			} else if ("true".equals(constant)) {
				this.type = Type.BOOL;
				this.intval = 1;
				this.floatval = 0f;
				this.boolval = true;
			} else if (constant.matches("[+\\-]?[0-9]+")) {
				this.type = Type.INT;
				this.intval = Integer.parseInt(constant);
				this.floatval = 0f;
				this.boolval = false;
			} else if (constant.matches("[+\\-]?[0-9]+f") || constant.matches("[+\\-]?[0-9]+\\.[0-9]+f?")) {
				this.type = Type.FLOAT;
				this.intval = 0;
				this.floatval = Float.parseFloat(constant);
				this.boolval = false;
			} else {
				this.type = Type.CONST;
				this.intval = 0;
				this.floatval = 0;
				this.boolval = false;
			}
		}
		
		@Override
		public String toString() {
			if (constant != null) {
				return constant;
			} else {
				return "arg" + source;
			}
		}
	}
	
	
	public enum Type {
		/**
		 * The parameter is already on the stack.
		 */
		THIS,
		/**
		 * The parameter is a sequence of instructions. 
		 */
		CODE,
		/**
		 * The parameter is the name of a constant which must be pushed on the stack.
		 */
		CONST,
		/**
		 * The parameter is a fixed integer value which must be pushed on the stack.
		 */
		INT,
		/**
		 * The parameter is a fixed float value which must be pushed on the stack.
		 */
		FLOAT,
		/**
		 * The parameter is a fixed boolean value which must be pushed on the stack.
		 */
		BOOL
	}
}

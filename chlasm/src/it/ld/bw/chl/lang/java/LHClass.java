/* Copyright (c) 2025-2026 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.bw.chl.lang.java;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import chl.lang.Action;
import it.ld.bw.chl.lang.java.FunctionMapping.ParameterMapping;

/**
 * A representation of the custom Java API for a game class.
 */
public class LHClass {
	private final static Map<String, LHClass> classes = new HashMap<>();
	
	private final String name;
	private final String simpleName;
	private final boolean isenum;
	
	private final List<String> parentNames = new ArrayList<>();
	
	private final Map<String, Object> constants = new HashMap<>();
	
	private final Map<String, Map<List<String>, LHMethod>> methods = new HashMap<>();
	
	static {
		String objName = "";
		try {
			List<Class<?>> jClasses = getClassesForPackage("chl.lang");
			for (Class<?> jClass : jClasses) {
				final String className = jClass.getName();
				final String simpleName = jClass.getSimpleName();
				objName = "class " + jClass;
				final LHClass newClass = new LHClass(className, jClass.isEnum());
				if (jClass.isEnum()) {
					Object[] constants = jClass.getEnumConstants();
					for (Object constant : constants) {
						objName = className + " " + constant;
						newClass.constants.put(constant.toString(), constant);
					}
				} else if (!jClass.isAnnotation()) {
					//Add interfaces
					for (Class<?> intf : jClass.getInterfaces()) {
						newClass.parentNames.add(intf.getName());
					}
					//Add static final fields as constants
					for (Field field : jClass.getFields()) {
						if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
							objName = className + " " + field;
							newClass.constants.put(field.getName(), field.get(null));
						}
					}
					//Add public constructors
					Map<List<String>, LHMethod> overrides = new HashMap<>();
					newClass.methods.put(simpleName, overrides);
					for (Constructor<?> constructor : jClass.getConstructors()) {
						if (Modifier.isPublic(constructor.getModifiers())) {
							objName = className + " " + constructor;
							String strAction = constructor.getAnnotation(Action.class).value();
							String[] parts = strAction.split("\\(|\\)");
							String action = parts[0];
							String[] sArgs = parts[1].isEmpty() ? new String[0] : parts[1].trim().split(", *");
							//
							Map<String, Integer> parameterNames = new HashMap<>();
							List<String> parameterTypes = new ArrayList<>(sArgs.length);
							Parameter[] parameters = constructor.getParameters();
							for (Parameter parameter : parameters) {
								parameterTypes.add(parameter.getType().getName());
								parameterNames.put(parameter.getName(), parameterNames.size());
							}
							//
							FunctionMapping functionMapping = new FunctionMapping(action);
							for (String arg : sArgs) {
								Integer src = parameterNames.get(arg);
								if (src != null) {
									ParameterMapping parameterMapping = new ParameterMapping(src, false);
									functionMapping.parameters.add(parameterMapping);
								} else {
									ParameterMapping parameterMapping = new ParameterMapping(arg);
									functionMapping.parameters.add(parameterMapping);
								}
							}
							//
							LHMethod override = new LHMethod(true, className, simpleName, parameters, functionMapping);
							overrides.put(parameterTypes, override);
						}
					}
					//Add public methods
					Method[] methods = jClass.getMethods();
					for (Method method : methods) {
						if (Modifier.isPublic(method.getModifiers())) {
							objName = className + " " + method;
							String methodName = method.getName();
							String returnType = method.getReturnType().getName();
							Action aAction = method.getAnnotation(Action.class);
							if (aAction != null) {
								String strAction = aAction.value();
								String[] parts = strAction.split("\\(|\\)");
								String action = parts[0];
								String[] sArgs = (parts.length == 1 || parts[1].isEmpty()) ? new String[0] : parts[1].trim().split(", *");
								//
								overrides = newClass.methods.get(methodName);
								if (overrides == null) {
									overrides = new HashMap<>();
									newClass.methods.put(methodName, overrides);
								}
								//
								Map<String, Integer> parameterNames = new HashMap<>();
								List<String> parameterTypes = new ArrayList<>(sArgs.length);
								Parameter[] parameters = method.getParameters();
								String varargs = null;
								for (Parameter parameter : parameters) {
									if (varargs != null) {
										throw new Exception("A single varargs parameter is allowed as last parameter");
									}
									parameterTypes.add(parameter.getType().getName());
									parameterNames.put(parameter.getName(), parameterNames.size());
									if (parameter.isVarArgs()) {
										varargs = parameter.getName();
									}
								}
								//
								FunctionMapping functionMapping = new FunctionMapping(action);
								for (int i = 0; i < sArgs.length; i++) {
									String arg = sArgs[i];
									if ("this".equals(arg)) {
										functionMapping.isStatic = false;
									}
									Integer src = parameterNames.get(arg);
									if (src != null) {
										boolean isVarArgs = arg.equals(varargs);
										if (isVarArgs && i < sArgs.length - 1) {
											throw new Exception("The varargs parameter must be mapped to the last parameter");
										}
										ParameterMapping parameterMapping = new ParameterMapping(src, isVarArgs);
										functionMapping.parameters.add(parameterMapping);
									} else {
										ParameterMapping parameterMapping = new ParameterMapping(arg);
										functionMapping.parameters.add(parameterMapping);
									}
								}
								//
								boolean isStatic = Modifier.isStatic(method.getModifiers());
								if (isStatic && !functionMapping.isStatic) {
									throw new Exception("Static method mapped to an instance method");
								}
								LHMethod override = new LHMethod(isStatic, returnType, methodName, parameters, functionMapping);
								overrides.put(parameterTypes, override);
							}
						}
					}
				}
				classes.put(className, newClass);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + ", processing " + objName, e);
		}
	}
	
	private LHClass(String name, boolean isenum) {
		this.name = name;
		this.isenum = isenum;
		this.simpleName = name.substring(name.lastIndexOf('.') + 1);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSimpleName() {
		return this.simpleName;
	}
	
	public boolean isEnum() {
		return this.isenum;
	}
	
	public boolean hasConstant(String name) {
		return this.constants.containsKey(name);
	}
	
	public Object getConstant(String name) {
		return this.constants.get(name);
	}
	
	public LHMethod findConstructor(List<String> parameterTypes) {
		return this.findMethod(this.simpleName, parameterTypes);
	}
	
	public boolean hasMethod(String methodname) {
		return this.methods.containsKey(methodname);
	}
	
	public LHMethod findMethod(String methodName, List<String> parameterTypes) {
		Map<List<String>, LHMethod> overrides = this.methods.get(methodName);
		if (overrides == null) return null;
		LHMethod method = overrides.get(parameterTypes);
		if (method != null) return method;
		for (Entry<List<String>, LHMethod> e : overrides.entrySet()) {
			List<String> argTypes = e.getKey();
			method = e.getValue();
			if (method.isVarArgs && parameterTypes.size() >= argTypes.size() - 1) {
				boolean matched = true;
				for (int i = 0; i < argTypes.size() - 1; i++) {
					String argType = argTypes.get(i);
					String parameterType = parameterTypes.get(i);
					if (!isAssignableTo(parameterType, argType)) {
						matched = false;
						break;
					}
				}
				if (matched) {
					return method;
				}
			} else if (argTypes.size() == parameterTypes.size()) {
				boolean matched = true;
				for (int i = 0; i < argTypes.size(); i++) {
					String argType = argTypes.get(i);
					String parameterType = parameterTypes.get(i);
					if (!isAssignableTo(parameterType, argType)) {
						matched = false;
						break;
					}
				}
				if (matched) {
					return method;
				}
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	
	public static boolean isAssignableTo(String classname, String container) {
		if (classname.equals(container)
			|| ("int".equals(classname) && "float".equals(container))
			|| "java.lang.Object".equals(container)
			|| "null".equals(classname) && forName(container) != null) {
			return true;
		}
		LHClass srcClass = forName(classname);
		if (srcClass == null) {
			return false;
		}
		for (String parent : srcClass.parentNames) {
			if (isAssignableTo(parent, container)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<LHClass> getClasses() {
		return new ArrayList<>(classes.values());
	}
	
	public static LHClass forName(String name) {
		return classes.get(name);
	}
	
	public static boolean isEnum(String name) {
		LHClass lhClass = forName(name);
		if (lhClass == null) return false;
		return lhClass.isEnum();
	}
	
	public static boolean hasConstant(String classname, String name) {
		LHClass lhClass = forName(classname);
		if (lhClass == null) return false;
		return lhClass.hasConstant(name);
	}
	
	public static Object getConstant(String classname, String name) {
		LHClass lhClass = forName(classname);
		if (lhClass == null) return null;
		return lhClass.getConstant(name);
	}
	
	public static LHMethod findConstructor(String className, List<String> parameterTypes) {
		LHClass lhClass = forName(className);
		if (lhClass == null) return null;
		return lhClass.findConstructor(parameterTypes);
	}
	
	public static boolean hasMethod(String classname, String methodname) {
		LHClass lhClass = forName(classname);
		if (lhClass == null) return false;
		return lhClass.hasMethod(methodname);
	}
	
	public static LHMethod findMethod(String className, String methodName, List<String> parameterTypes) {
		LHClass lhClass = forName(className);
		if (lhClass == null) return null;
		return lhClass.findMethod(methodName, parameterTypes);
	}
	
	
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname, ArrayList<Class<?>> classes) throws ClassNotFoundException {
	    File tmpDirectory;
	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();
	        for (final String file : files) {
	            if (file.endsWith(".class")) {
	                try {
	                    classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
	                } catch (final NoClassDefFoundError e) {
	                    // do nothing. this class hasn't been found by the
	                    // loader, and we don't care.
	                }
	            } else if ((tmpDirectory = new File(directory, file)).isDirectory()) {
	                checkDirectory(tmpDirectory, pckgname + "." + file, classes);
	            }
	        }
	    }
	}

	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private static void checkJarFile(JarURLConnection connection, String pckgname, ArrayList<Class<?>> classes) throws ClassNotFoundException, IOException {
	    final JarFile jarFile = connection.getJarFile();
	    final Enumeration<JarEntry> entries = jarFile.entries();
	    String name;
	    for (JarEntry jarEntry = null; entries.hasMoreElements() && ((jarEntry = entries.nextElement()) != null);) {
	        name = jarEntry.getName();
	        if (name.contains(".class")) {
	            name = name.substring(0, name.length() - 6).replace('/', '.');
	            if (name.contains(pckgname)) {
	                classes.add(Class.forName(name));
	            }
	        }
	    }
	}

	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param pckgname
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	public static ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
	    final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    try {
	        final ClassLoader cld = Thread.currentThread().getContextClassLoader();

	        if (cld == null) {
	            throw new ClassNotFoundException("Can't get class loader.");
	        }
	        final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
	        URLConnection connection;
	        for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
	            try {
	                connection = url.openConnection();

	                if (connection instanceof JarURLConnection) {
	                    checkJarFile((JarURLConnection) connection, pckgname, classes);
	                } else /*if (connection instanceof FileURLConnection)*/ {
	                    try {
	                        checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
	                    } catch (final UnsupportedEncodingException ex) {
	                        throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)", ex);
	                    }
	                }/* else {
	                    throw new ClassNotFoundException(pckgname + " (" + url.getPath() + ") does not appear to be a valid package");
	                }*/
	            } catch (final IOException ioex) {
	                throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex);
	            }
	        }
	    } catch (final NullPointerException ex) {
	        throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)", ex);
	    } catch (final IOException ioex) {
	        throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname, ioex);
	    }
	    return classes;
	}
}

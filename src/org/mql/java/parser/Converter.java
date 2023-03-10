package org.mql.java.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.mql.java.enums.EModifiers;
import org.mql.java.enums.Visibility;
import org.mql.java.models.Attribute;
import org.mql.java.models.ClassModel;
import org.mql.java.models.ConstructorModel;
import org.mql.java.models.EnumModel;
import org.mql.java.models.InterfaceModel;
import org.mql.java.models.MethodModel;
import org.mql.java.models.PackageModel;

@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class Converter {
	
	
		
		public static ClassModel toClassModel(Class c) {
		
			
			return new ClassModel(
					idGenerator(c.getCanonicalName()),
					c.getCanonicalName(),
					c.getSimpleName(),
					getVisibility(c) , 
					getModificator(c),
					getFields(c.getDeclaredFields()),
					getConstuctors(c.getConstructors()),
					getMethods(c.getMethods()),
					getInterfaces(c),
					getExtends(c)
					);
			
		}
		
		public static InterfaceModel toInterfaceModel(Class c){
			return 	new InterfaceModel(
						idGenerator(c.getCanonicalName()),
						c.getCanonicalName(),
						c.getSimpleName(),
						getVisibility(c),
						getModificator(c),
						getMethods(c.getMethods())
						);
		}
		
		public static EnumModel toEnumModel(Class c) {
			return new EnumModel( idGenerator(c.getCanonicalName()),
					 c.getCanonicalName(),
					 c.getSimpleName(),
					 getVisibility(c.getModifiers()) , 
					 getModificator(c.getModifiers()),
					 getInterfaces(c),
					 (List<String>)getFields(c.getDeclaredFields(),true)
					 );
		}
		
		
		public static PackageModel toPackageModel(Package p) {
			
			return new PackageModel(idGenerator(p.getName()), p.getName());
		}
		
		private static Attribute toAttribute(Field f){
			
			return new Attribute(
						idGenerator(f),
						f.getName(),
						getGenericType(f),
						getModificator(f),
						getVisibility(f),
						isList(f),
						idGenerator(getGenericType(f))
						);		
			
		}
		
		private static MethodModel toMethodModel(Method m){
			return new MethodModel(
						idGenerator(m),/*id not important*/
						m.getName(),
						getVisibility(m),
						getModificator(m),
						getParameter(m.getParameters()),
						m.getReturnType().getCanonicalName()
						);
		}
		
		private static ConstructorModel toConstructorModel(Constructor c){
			return new ConstructorModel(
						positiveNumber(UUID.randomUUID().getMostSignificantBits()),/*id not important*/
						getVisibility(c.getModifiers()),
						getModificator(c.getModifiers()),
						getParameter(c.getParameters())
						);
		}
		
		
	
		private static EModifiers getModificator(int mod) {
			if(Modifier.isFinal(mod)) return EModifiers.FINAL;
			else if(Modifier.isStatic(mod)) return EModifiers.STATIC;
			else if(Modifier.isAbstract(mod)) return EModifiers.ABSTRACT;
			else return null;
		}
		
		private static Visibility getVisibility(int mod) {
			if(Modifier.isPrivate(mod)) return Visibility.PRIVATE;
			else if(Modifier.isPublic(mod)) return Visibility.PUBLIC;
			else if(Modifier.isProtected(mod)) return Visibility.PROTECTED;
			else return Visibility.DEFAULT;
		}
		
		private static Visibility getVisibility(Member member) {
			return getVisibility(member.getModifiers());
		}
		
		private static EModifiers getModificator(Class c) {
			return getModificator(c.getModifiers());
		}
		
		private static EModifiers getModificator(Member member) {
			return getModificator(member.getModifiers());
		}
		private static Visibility getVisibility(Class c) {
			return getVisibility(c.getModifiers());
		}

	
		private static boolean isList(Field f) {
			String type = f.getGenericType().getTypeName();	
			if((type.contains("<") && type.contains(">")) || (type.toLowerCase().contains("list"))) {
				return true;
			}
			else if(type.contains("[]")) {
				return true;
			}
			return false;
		}
		
		public static String getGenericType(Field f) {
			String type = f.getGenericType().getTypeName();	
			if(type.contains("<") && type.contains(">")){				
				type = type.substring(type.indexOf("<")+1, type.indexOf(">"));
			}
			type = type.replace("[", "").replace("]", "");
			return type;
		}
		public static String getGenericType(Parameter p) {
			String type = p.getType().getCanonicalName();
			if(type.contains("<") && type.contains(">")){				
				type = type.substring(type.indexOf("<")+1, type.indexOf(">"));
			}
			type = type.replace("[", "").replace("]", "");
			return type;
		}
		
		public static EClassifier nativeType(Class p) {
			if(p.isPrimitive()) {
				if(p.getCanonicalName().equals("int"))
					return EcorePackage.Literals.EINT;
				if(p.getCanonicalName().equals("long"))
					return EcorePackage.Literals.ELONG;
				if(p.getCanonicalName().equals("double"))
					return EcorePackage.Literals.EDOUBLE;
				if(p.getCanonicalName().equals("short"))
					return EcorePackage.Literals.ESHORT;
				if(p.getCanonicalName().equals("boolean"))
					return EcorePackage.Literals.EBOOLEAN;
				if(p.getCanonicalName().equals("float"))
					return EcorePackage.Literals.EFLOAT;
				
			}
			else {
				if(p.getCanonicalName().equals("java.lang.String"))
					return EcorePackage.Literals.ESTRING;
				if(p.getCanonicalName().equals("java.lang.Date"))
					return EcorePackage.Literals.EDATE;
			}
			//throw
			return null;
		}
		private static List<Attribute> getFields(Field[] fields){
			List<Attribute> EAttributes = new Vector<Attribute>();
			for(Field f : fields) {
				EAttributes.add(toAttribute(f));		
			}
			return EAttributes;
		}
		
		private static List<?> getFields(Field[] fields,boolean justString){
			if(justString) {
				List<String> EAttributes = new Vector<String>();
				for(Field f : fields) {
					EAttributes.add(f.getName().toUpperCase());		
				}
				return EAttributes;
			}
			else {
				return getFields(fields);
			}
			
		}
	
		private static List<ConstructorModel> getConstuctors(Constructor[] constuctors){
			List<ConstructorModel> ECons = new Vector<ConstructorModel>();
			for(Constructor c : constuctors) {
				ECons.add(toConstructorModel(c));
			}		
			return ECons;
		}
		
		private static List<MethodModel> getMethods(Method[] methods){
			List<MethodModel> EMethods = new Vector<MethodModel>();
			for(Method m : methods) {
				EMethods.add(toMethodModel(m));
			}		
			return EMethods;
		}
		
		private static List<String> getParameter(Parameter[] parameterz){
			List<String> params = new Vector<String>(); 
			for(Parameter p : parameterz) {
				params.add(p.getType().getCanonicalName());
			}
			return params;
		}
		
		private static List<InterfaceModel> getInterfaces(Class c){
			List<InterfaceModel> interfaces = new Vector<InterfaceModel>();
			for(Class intrF  : c.getInterfaces()) {
				interfaces.add(toInterfaceModel(intrF));		
			}
			return interfaces;
		}
		
		private static ClassModel getExtends(Class c) {
			c = c.getSuperclass();
			if(c.getSuperclass() == null) {
				return null;
			}
			if(c.getCanonicalName().equals("java.lang.Object")) {
				return null;
			}
			return toClassModel(c);
		}
		
		public static long idGenerator(Object o) {	
			return positiveNumber(o.hashCode());
		}
		
		private static long positiveNumber(long n) {
			//compare binary of (hashcode of object) with (0b11111111111)
			return n &  0xfffffff;
		}

}

// Wilmer Henao

// Most of the class was learned from the website 
// http://java.sun.com/docs/books/tutorial/reflect/class/classModifiers.html

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import static java.lang.System.out;

enum ClassMember { CONSTRUCTOR, FIELD, METHOD, CLASS, ALL }

public class whenaoSignatures {
    public static void main(String... args) {
	try {
	    for(int i = 0; i< args.length; i = i + 1){
		out.format("========================== CLASS SIGNATURE # %d ==========================%n", i+1);
		out.format("++++++ Corresponding to %s ", args[i]);
	    	Class<?> c = Class.forName(args[i]);
	    	out.format("Class:%n  %s%n%n", c.getCanonicalName());
	    	out.format("Modifiers:%n  %s%n%n",
		       Modifier.toString(c.getModifiers()));

		Package p = c.getPackage();
	    	out.format("Package:%n  %s%n%n",
		       (p != null ? p.getName() : "-- No Package --"));

	    	out.format("Type Parameters:%n");
	    	TypeVariable[] tv = c.getTypeParameters();
	    	if (tv.length != 0) {
			out.format("  ");
			for (TypeVariable t : tv)
			    out.format("%s ", t.getName().replaceFirst(p.getName(), ""));
			out.format("%n%n");
	    	} else {
			out.format("  -- No Type Parameters --%n%n");
	 	}

	    	out.format("Implemented Interfaces:%n");
	    	Type[] intfs = c.getGenericInterfaces();
	    	if (intfs.length != 0) {
			for (Type intf : intfs)
			    out.format("  %s%n", intf.toString().replaceFirst(p.getName(), ""));
			out.format("%n");
	    	} else {
			out.format("  -- No Implemented Interfaces --%n%n");
	    	}

	    	out.format("Inheritance Path:%n");
	    	List<Class> l = new ArrayList<Class>();
	    	printAncestor(c, l, p);
	    	if (l.size() != 0) {
			for (Class<?> cl : l)
			    out.format("  %s%n", cl.getCanonicalName().replaceFirst(p.getName(), ""));
			out.format("%n");
	    	} else {
			out.format("  -- No Super Classes --%n%n");
	    	}

		printMembers(c.getConstructors(), "Constructor", c, p);
		printMembers(c.getFields(), "Fields", c, p);
		printMembers(c.getMethods(), "Methods", c, p);  //NOTICE: Regarding questions in e-mails.  Documentation says that getMethods "Returns an array containing Method objects reflecting all the public member methods of the class or interface represented by this Class object, including those declared by the class or interface and and those inherited from superclasses and superinterfaces"
		printClasses(c, p);

	    	out.format("Annotations:%n");
	    	Annotation[] ann = c.getAnnotations();
	    	if (ann.length != 0) {
			for (Annotation a : ann)
			    out.format("  %s%n", a.toString());
			out.format("%n");
	    	} else {
			out.format("  -- No Annotations --%n%n");
	    	}

	    	// Implemented interfaces
	    	Class[] intfaces = c.getInterfaces();
	    	if (intfaces.length != 0) {
		    for (Class intface : intfaces){
			    out.format("Methods declared by interface %s%n", intface.getName().replaceFirst(p.getName(), ""));
			    Method[] myms = intface.getMethods();
			    if(myms.length == 0){
				out.format("  --> none%n");
			    } else {
				for(Method mym : myms){
				    out.format("  --> %s%n", mym.getName().replaceFirst(p.getName(), ""));
				}
			    }
		    }
		}
	    } 
	}
	catch (ClassNotFoundException x) {
	    x.printStackTrace();
	}
    }

    private static void printifAncestorDefinestoo(Class<?> c, Method mbr, Package p) {
    /*You enter here only if the object is static;  it escalates up into the
    Superclasses to see if the methods were declared there too*/
	try{
	Class<?> ancestor = c.getSuperclass();
 	if (ancestor != null) {
	    Method methlist[] = ancestor.getDeclaredMethods();
	    for(int iotro = 0; iotro < methlist.length; iotro++){
		Method m = methlist[iotro];
		String mname = m.getName();
		String mbrname = mbr.toGenericString();
		if(mbrname.indexOf(mname)>0){
		    String tstr = Modifier.toString(m.getModifiers());
		    if(tstr.indexOf("abstract")>0){
			out.format("  // Declared by %s%n", m.getDeclaringClass().getName().replaceFirst(p.getName(), ""));
		    } else {
			out.format("  // Defined by %s%n", m.getDeclaringClass().getName().replaceFirst(p.getName(), ""));
		    }
		}
	    }
	    printifAncestorDefinestoo(ancestor, mbr, p);
 	}
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }

    private static void printAncestor(Class<?> c, List<Class> l, Package p) {
	try{
	Class<?> ancestor = c.getSuperclass();
 	if (ancestor != null) {
	    l.add(ancestor);
	    out.format("Methods inherited from ancestor %s%n", ancestor.getName().replaceFirst(p.getName(), ""));
	    Method[] meths = ancestor.getMethods();
	    if(meths.length == 0){
		out.format("       --> none%n");
	    } else {
		for(Method meth : meths){
		    out.format("       --> %s%n", meth.getName().replaceFirst(p.getName(), ""));
		}
	    }
	    printAncestor(ancestor, l, p);
 	}
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }

    private static void printMembers(Member[] mbrs, String s, Class<?> madre, Package p) {
	try{
	out.format("%s:%n", s);
	for (Member mbr : mbrs) {
	    if (mbr instanceof Field){
		out.format("  %s; ", ((Field)mbr).toGenericString());
		out.format("// Defined by %s%n", ((Field)mbr).getDeclaringClass().getName().replaceFirst(p.getName(), ""));
	    }
	    else if (mbr instanceof Constructor)
		out.format("  %s%n", ((Constructor)mbr).toGenericString());
	    else if (mbr instanceof Method){
		out.format("  %s%n", ((Method)mbr).toGenericString().replaceFirst(p.getName(), ""));
		int haystatic = 0;
		String tstr = Modifier.toString(((Method)mbr).getModifiers());
		//int checkser = tstr.indexOf("static"); //checking if the method contains static
		if(tstr.indexOf("static")>0){
                // Output tests if defined or declared
		    if(tstr.indexOf("abstract")>0){
			out.format("  // Declared by %s%n", ((Method)mbr).getDeclaringClass().getName().replaceFirst(p.getName(), ""));
		    } else {
			out.format("  // Defined by %s%n", ((Method)mbr).getDeclaringClass().getName().replaceFirst(p.getName(), ""));
		    }
		    printifAncestorDefinestoo(madre, ((Method)mbr), p);
		}
	    }
	}
	if (mbrs.length == 0)
	    out.format("  -- No %s --%n", s);
	out.format("%n");
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }

    private static void printClasses(Class<?> c, Package p) {
	try{
	out.format("Classes:%n");
	Class<?>[] clss = c.getClasses();
	for (Class<?> cls : clss)
	    out.format("  %s%n", cls.getCanonicalName());
	if (clss.length == 0)
	    out.format("  -- No member interfaces, classes, or enums --%n");
	out.format("%n");
	}
	catch(Exception e){
	    e.printStackTrace();
	}
    }
}

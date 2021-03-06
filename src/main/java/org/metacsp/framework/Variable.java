/*******************************************************************************
 * Copyright (c) 2010-2013 Federico Pecora <federico.pecora@oru.se>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.metacsp.framework;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Logger;

import org.metacsp.framework.multi.MultiVariable;
import org.metacsp.utility.logging.MetaCSPLogging;

/**
 * Class representing the decision variables in a Constraint Problem.
 * This class is extended by abstract class {@link MultiVariable}
 * to accommodate variables which are themselves constraint networks
 * (essential for the Meta-CSP approach).
 * 
 * @author Federico Pecora
 *
 */
public abstract class Variable implements Comparable<Variable>, Serializable {
	
	public static HashMap<FieldOfObject,Object> backupForSerialization = new HashMap<FieldOfObject,Object>();
	private class FieldOfObject {
		private Field field;
		private int ID;
		private FieldOfObject(Field field) {
			this.ID = getID();
			this.field = field;
		}
		public boolean equals(Object o) {
			FieldOfObject foo = (FieldOfObject)o;
			return (foo.ID == this.ID && foo.field.getName().equals(this.field.getName()));
		}
		public int hashCode() {
			return this.toString().hashCode();
		}
		public String toString() {
			return "FieldOfObject <" + ID + "," + field.getName() + ">";
		}
	}
	
	protected transient Logger logger = MetaCSPLogging.getLogger(this.getClass());
	
	/**
	 * ID generation is up to the implementing class.  No automatic generation provided.
	 */
	protected int id;

	/**
	 * A field to mark variables if needed by the implementer of new constraint solvers
	 * (not used by ConstraintSolver/MultiConstraintSolver/MetaConstraintSolver)
	 */
	private Object marking;

	/**
	 * A field to set the entity that creates this variable
	 */
	private transient Object owner;
	
	/**
	 * A field to set generic attributes  
	 */
	protected transient Object attributes;

	/**
	 * A field to set the visualization color. If not modified, the variable will be visualized in red
	 */
	private Paint color;
	
	private static final long serialVersionUID = 7L;

	
	/**
	 * Set the marking of this {@link Variable}.
	 * @param marking an {@link Object} representing the marking of this {@link Variable}.
	 */
	public void setMarking(Object marking) { 
		this.marking = marking;
		logger.finest("Set marking of variable " + this.getID() + " to " + marking);
	}
	
	/**
	 * Get the marking of this {@link Variable}.
	 * @return An {@link Object} representing the marking of this {@link Variable}.
	 */
	public Object getMarking() { return this.marking; }
	
	/**
	 * Every variable has a constraint solver.
	 */
	protected ConstraintSolver solver;

	//This is so that extending classes must invoke 2-arg constructor of Variable (below) 
	@SuppressWarnings("unused")
	private Variable() {}
	
	/**
	 * A variable should not be instantiated directly, rather only a ConstraintSolver
	 * should create variables (through the createVariable() methods).  This constructor requires
	 * the specification of the constraint solver and of the Variable's ID.
	 * @param cs The constraint solver to which this variable refers to.
	 * @param id The ID of the variable (should be maintained by the ConstraintSolver in some way).
	 */
	protected Variable(ConstraintSolver cs, int id) {
		this.solver = cs;
		this.id = id;
		this.color= Color.RED;
	}
	
	/**
	 * Get the constraint solver of this Variable.
	 * @return The constraint solver of this variable.
	 */
	public ConstraintSolver getConstraintSolver() { return this.solver; }
	
	/**
	 * Get the domain of this Variable.  Must be implemented by the ConstraintSolver developer,
	 * since getting the domain of a variable may be a non-trivial operation.
	 * @return The domain of the variable.
	 */
	public abstract Domain getDomain();
	
	/**
	 * Set the domain of this variable.  Must be implemented by the ConstraintSolver developer,
	 * since setting the domain of a variable may be a non-trivial operation. 
	 * @param d The domain of the variable.
	 */
	public abstract void setDomain(Domain d);
	
	/**
	 * Get the ID of this variable.
	 * @return the ID of this variable.
	 */
	public int getID() { return this.id; }

	/**
	 * Get a {@link String} representation of this {@link Variable}.
	 * @return A {@link String} representation of this {@link Variable}.
	 */
	public abstract String toString();
	
	/**
	 * Get this {@link Variable}'s component.
	 * @return This {@link Variable}'s component.
	 */
	public String getComponent() {
		return this.solver.getComponent(this);
	}

	/**
	 * Ascertain whether this variable is equal to another.
	 * Variables are equal iff they have the same ID.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			return (((Variable)o).getID() == this.getID() && o.getClass().equals(this.getClass()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	/**
	 * Get the description of this {@link Variable}'s type.
	 * @return The description of this {@link Variable}'s type.
	 */
	public String getDescription() {
		return this.getClass().getSimpleName();
	}
	
	public Object getOwner() {
		return owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}
	
	public Paint getColor(){return this.color;}
	
	public void setColor(Paint c){this.color=c;}

	public Object getAttributes() {
		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	
//	public static Vector<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {
//		Vector<Field> currentClassFields = new Vector<Field>();
//		for (Field f : startClass.getDeclaredFields()) currentClassFields.add(f);
//		Class<?> parentClass = startClass.getSuperclass();
//		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
//			Vector<Field> parentClassFields = (Vector<Field>) getFieldsUpTo(parentClass, exclusiveParent);
//			currentClassFields.addAll(parentClassFields);
//		}
//		return currentClassFields;
//	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		for (Field f : Variable.class.getDeclaredFields()) {
			if (Modifier.isTransient(f.getModifiers())) {
				try { backupForSerialization.put(new FieldOfObject(f), f.get(this)); }
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				catch (IllegalAccessException e) { e.printStackTrace(); }
			}
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		for (Field f : Variable.class.getDeclaredFields()) {
			if (Modifier.isTransient(f.getModifiers())) {
				Object foo = backupForSerialization.get(new FieldOfObject(f));
				try { f.set(this, foo); }
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				catch (IllegalAccessException e) { e.printStackTrace(); }
			}
		}
	}
	
}

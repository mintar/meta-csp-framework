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
package org.metacsp.time;

import java.util.Arrays;

import org.metacsp.framework.Domain;
import org.metacsp.framework.Variable;

/**
 * This class provides the building block of the {@link APSPSolver} temporal reasoner, namely time points.
 * Note that as all variables, {@link TimePoint}s should be created with a solver factory method
 * (in this case, {@link APSPSolver}). 
 * @version 1.0
 */
public final class TimePoint extends Variable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8140136869378931079L;

	//out edges
	private SimpleDistanceConstraint[] out;
	//private int MAX_TPS;

	//whether this TP is used or can be overwritten
	private boolean used = false;

	//Bounds
	private long lowerBound;
	private long upperBound;


	//	public static HashMap<Constraint,SimpleDistanceConstraint> sdcMap = new HashMap<Constraint, SimpleDistanceConstraint>();

	/**
	 * Instantiate a new {@link TimePoint} with a given identifier and maximum number of time points that can be
	 * added to the temporal network.  This constructor should NOT be used.  As the {@link APSPSolver} is static,
	 * this method is used in the {@link APSPSolver}'s constructor to instantiate the array of time points
	 * representing the temporal network.
	 * @param id  The identifier of this time point.
	 * @param MAX_TPS  The maximum number of time points supported by the {@link APSPSolver}.
	 * @param sol the {@link APSPSolver} managing this {@link TimePoint}.
	 */
	public TimePoint(int id, int MAX_TPS, APSPSolver sol) {
		//must invoke 2-arg superconstructor, lest compilation error
		super(sol,id);
		//this.MAX_TPS = MAX_TPS;
		out = new SimpleDistanceConstraint[MAX_TPS];
	}

	//	public TimePoint(int id, int MAX_TPS, APSPSolverIncrementelPPC sol) {
	//		//must invoke 2-arg superconstructor, lest compilation error
	//		super(sol,id);
	//		//this.MAX_TPS = MAX_TPS;
	//		out = new SimpleDistanceConstraint[MAX_TPS];
	//	}

	/**
	 * Instantiate a new {@link TimePoint} with a given identifier, upper bound and lower bound that can be
	 * added to the temporal network.  This constructor should NOT be used (it is used within the {@link APSPSolver}, 
	 * a static STN implementation.
	 * @param id  The identifier of this time point.
	 * @param lb The lower bound of this time point.
	 * @param ub The upper bound of this time point.
	 * @param sol the {@link APSPSolver} managing this {@link TimePoint}.
	 */
	public TimePoint(int id, int MAX_TPS, APSPSolver sol, long lb, long ub) {
		//must invoke 2-arg superconstructor, lest compilation error		
		this(id,MAX_TPS,sol);
		this.lowerBound = lb;
		this.upperBound = ub;
	}


	//	Utility methods

	/**
	 * Compare this time point with a reference time point.
	 * @return True iff the two time points have the same identifier.
	 */
	public boolean equals (Object obj)
	{return (obj instanceof TimePoint)&&
			(((TimePoint) obj).id == id); 
	//&&
	//(((TimePoint) obj).lowerBound == lowerBound) &&
	//(((TimePoint) obj).out.equals(out));

	}


	//	Print methods

	/**
	 * Get a String representation of this {@link TimePoint}.
	 * @return A String describing this {@link TimePoint}.
	 */
	public String toString() {
		String ret="";//\n((((((((((((";

		ret+= this.id + ":{" + APSPSolver.printLong(this.lowerBound) + "," + APSPSolver.printLong(this.upperBound) + "}";
		//		ret+="))))))))))))))))\n";
		return ret;
	}
	/*
	public String toString() 
    {String s = new String();
	 s = s + "TimePoint    : " + id + "(" + used + ")\n";
	 s = s + "lb : " + lowerBound + "\n";
	 s = s + "ub : " + upperBound + "\n";
	 s = s + "out edges: \n";
	 for (int i = 0; i < MAX_TPS; i++) if (out[i] != null) s = s + "\t" + out[i] + "\n";
	 return s;
    } */

	//	Access methods
	/**
	 * Get this time point's lower bound.
	 * @return This time point's lower bound.
	 */
	public long getLowerBound(){
		return lowerBound;
	}

	/**
	 * Set this time point's lower bound.
	 * @param newVal The new lower bound to set.
	 */
	public void setLowerBound(long newVal){
		lowerBound = newVal;
	}

	/**
	 * Get this time point's upper bound.
	 * @return This time point's upper bound.
	 */
	public long getUpperBound(){
		return upperBound;
	}

	/**
	 * Set this time point's upper bound.
	 * @param newVal The new upper bound to set.
	 */
	public void setUpperBound(long newVal){
		upperBound = newVal;
	}

	/**
	 * Reports whether this time point is used in the underlying temporal network. 
	 * @return True iff this time point is used in the underlying temporal netowrk.
	 */
	public boolean isUsed(){
		return used;
	}

	/**
	 * Set whether this time point as used in the underlying temporal network.
	 * @param newVal Set to true iff the time point should be included in the underlying temporal network.
	 */
	public void setUsed(boolean newVal){

		if(isUsed() && newVal == false) {
			Arrays.fill(out, null);
		}

		used = newVal;
	}

	/**
	 * Get the i-th outgoing edge of this time point.
	 * @param i The index of the edge to get.
	 * @return The {@link SimpleDistanceConstraint} correspionding to the i-th outgoing edge.
	 */
	public SimpleDistanceConstraint getOut(int i){
		return out[i];
	}

	public SimpleDistanceConstraint[] getOut(){
		return out;
	}

	/**
	 * Set the i-th outgoing edge of this time point.
	 * @param i The index of the edge to set.
	 * @param newVal The {@link SimpleDistanceConstraint} correspionding to set as i-th outgoing edge.
	 */
	public void setOut(int i, SimpleDistanceConstraint newVal){
		out[i] = newVal;
	}

	@Override
	public Domain getDomain() {
		return new Interval(this, this.lowerBound, this.upperBound);
	}

	@Override
	public void setDomain(Domain d) {
		if (d instanceof Interval) {
			this.lowerBound = ((Interval)d).getLowerBound();
			this.upperBound = ((Interval)d).getUpperBound();
		}
	}

	@Override
	public int compareTo(Variable o) {
		// TODO Auto-generated method stub
		return this.id - o.getID();
	}

	@Override
	public TimePoint clone( ) {

		TimePoint c = new TimePoint(id, this.out.length, (APSPSolver) this.solver);
		c.setLowerBound(this.getLowerBound());
		c.setUpperBound(this.getUpperBound());
		c.setUsed(this.used);

		for ( int i = 0 ; i < this.out.length ; i++ ) {
			if ( this.out[i] != null ) {
				SimpleDistanceConstraint sdc = this.out[i].clone();					
				c.out[i] = sdc;
			} else {
				c.out[i] = null;
			}
		}
		return c;
	}

}

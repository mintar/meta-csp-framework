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
package multi.TCSP;

import time.APSPSolver;
import framework.ConstraintNetwork;
import framework.ConstraintSolver;
import framework.Variable;
import framework.multi.MultiConstraintSolver;

public class DistanceConstraintSolver extends MultiConstraintSolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8474292073131422005L;
	private int IDs = 0;
	private MultiTimePoint source = null;
	private MultiTimePoint sink = null;
	
	public DistanceConstraintSolver(long origin, long horizon) {
		super(new Class[]{DistanceConstraint.class}, new Class[]{MultiTimePoint.class}, createConstraintSolvers(origin, horizon));	
		//Create source and sink as wrappers of APSPSolver's source and sink
		APSPSolver internalSolver = (APSPSolver)this.constraintSolvers[0];
		source = new MultiTimePoint(this, IDs++, constraintSolvers, new Variable[] {internalSolver.getSource()});
		sink = new MultiTimePoint(this, IDs++, constraintSolvers, new Variable[] {internalSolver.getSink()});
		this.theNetwork.addVariable(source);
		this.theNetwork.addVariable(sink);
		this.setOptions(OPTIONS.ALLOW_INCONSISTENCIES);		
	}

	private static ConstraintSolver[] createConstraintSolvers(long origin, long horizon) {
		APSPSolver stpSolver = new APSPSolver(origin, horizon);
		return new ConstraintSolver[] {stpSolver};
	}
	
	@Override
	protected ConstraintNetwork createConstraintNetwork() {
		return new DistanceConstraintNetwork(this);
	}

	@Override
	protected Variable[] createVariablesSub(int num) {
		int[] ingredients = new int[] {1};
		return super.createVariablesSub(ingredients, num);
	}
	
//	@Override
//	protected Variable[] createVariablesSub(int num) {
//		MultiTimePoint[] ret = new MultiTimePoint[num];
//		for (int i = 0; i < num; i++) {
//			ret[i] = new MultiTimePoint(this, IDs++, this.constraintSolvers);
//		}
//		return ret;
//	}
	
	@Override
	public boolean propagate() {
		// APSPSolver will propagate what it can...
		// this solver does not know how to propagate (yet)
		return true;
	}

	public MultiTimePoint getSource() {
		return source;
	}

	public MultiTimePoint getSink() {
		return sink;
	}

	
}

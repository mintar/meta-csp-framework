package org.metacsp.examples;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.metacsp.booleanSAT.BooleanConstraint;
import org.metacsp.booleanSAT.BooleanSatisfiabilitySolver;
import org.metacsp.booleanSAT.BooleanVariable;
import org.metacsp.framework.ConstraintNetwork;
import org.metacsp.utility.logging.MetaCSPLogging;

public class TestBooleanSatisfiabilitySolverSATNonCNFSimple {
	
	public static void main(String[] args) {
		
		BooleanSatisfiabilitySolver solver = new BooleanSatisfiabilitySolver(10, 10);
		MetaCSPLogging.setLevel(BooleanSatisfiabilitySolver.class, Level.FINEST);
		MetaCSPLogging.setLevel(BooleanConstraint.class, Level.FINEST);
		Logger logger = MetaCSPLogging.getLogger(TestBooleanSatisfiabilitySolverSATNonCNFSimple.class);
		
		ConstraintNetwork.draw(solver.getConstraintNetwork());

		BooleanVariable[] vars = (BooleanVariable[])solver.createVariables(4);
		//NOTE: All parentheses need to be explicit (every binary connective must be parenthesized)
		//... therefore the following is not OK:
		//String wff = "(x1 ^ x2) ^ (x2 v ~x3 ^ x4) ^ (~x1 v x3) ^ (x2 v ~x3 ^ ~x4)";
		//... but the following is OK:
		//String wff = "((((x1 ^ x2) ^ (x2 v (~x3 ^ x4))) ^ (~x1 v x3)) ^ (x2 v (~x3 ^ ~x4)))";
		//... as well as the following:
		String wff = "(((x1 ^ x2) ^ (x2 v (~x3 ^ x4))) ^ ((~x1 v x3) ^ (x2 v (~x3 ^ ~x4))))";
		BooleanConstraint[] cons = BooleanConstraint.createBooleanConstraints(vars, wff);

		logger.info("SAT? "+solver.addConstraints(cons));
		logger.info(Arrays.toString(vars));
		
	}

}

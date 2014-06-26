package de.dwslab.riskmanagement.abduction.model.expressions;

import java.util.HashSet;

import de.dwslab.riskmanagement.abduction.model.variables.VariableAbstract;


public interface IfExpression {
	public HashSet<VariableAbstract> getAllVariables();
	
}

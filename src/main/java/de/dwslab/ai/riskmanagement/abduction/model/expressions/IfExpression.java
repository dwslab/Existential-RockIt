package de.dwslab.ai.riskmanagement.abduction.model.expressions;

import java.util.HashSet;

import de.dwslab.ai.riskmanagement.abduction.model.variables.VariableAbstract;

public interface IfExpression {

    public HashSet<VariableAbstract> getAllVariables();

}

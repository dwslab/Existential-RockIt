package de.dwslab.ai.riskmanagement.abduction.model.formulas;

import java.util.ArrayList;
import java.util.HashSet;

import de.dwslab.ai.riskmanagement.abduction.exceptions.ParseException;
import de.dwslab.ai.riskmanagement.abduction.model.expressions.IfExpression;
import de.dwslab.ai.riskmanagement.abduction.model.expressions.PredicateExpression;
import de.dwslab.ai.riskmanagement.abduction.model.variables.VariableType;

/**
 * Formular to model Cardinalities.
 *
 * It is possible to model n to m correspondencies with this formular.
 *
 * For example 1 to 1 restrictions, etc.
 *
 * @author jan
 *
 */

public class FormulaCardinality extends FormulaHard {

    private final HashSet<VariableType> overVariables = new HashSet<VariableType>();
    private int cardinality = 1;
    private boolean lessEqual = true;

    public FormulaCardinality(String name, HashSet<VariableType> forVariables,
            ArrayList<IfExpression> ifExpressions,
            HashSet<VariableType> overVariables, ArrayList<PredicateExpression> restrictions,
            int cardinality, boolean lessEqual) throws ParseException {
        this.setForVariables(forVariables);
        this.setName(name);
        this.setIfExpressions(ifExpressions);
        this.setRestrictions(restrictions);
        this.setCardinality(cardinality);
        this.setOverVariables(overVariables);
        this.setLessEqual(lessEqual);

    }

    public FormulaCardinality() {
    }

    public void setOverVariables(VariableType... overVariables) throws ParseException {
        for (VariableType overVariable : overVariables) {
            this.addOverVariable(overVariable);
        }
    }

    public void setOverVariables(HashSet<VariableType> overVariables) throws ParseException {
        for (VariableType var : overVariables) {
            this.addOverVariable(var);
        }
    }

    public void addOverVariable(VariableType overVariable) throws ParseException {
        if (!this.getForVariables().contains(overVariable)) {
            throw new ParseException(
                    "Every variable in the over part of a cardinality must also occur in the for part of a formular. This is not the case for variable "
                            + overVariable.toString());
        } else {
            this.overVariables.add(overVariable);
        }
    }

    public HashSet<VariableType> getOverVariables() {
        return overVariables;
    }

    public void setCardinality(int cardinality) throws ParseException {
        if (cardinality <= 0) {
            throw new ParseException(
                    "The cardinalities must be greater than 0. The actual value is " + cardinality);
        } else {
            this.cardinality = cardinality;
        }
    }

    public int getCardinality() {
        return cardinality;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        int i = 0;
        for (VariableType var : this.overVariables) {
            sb.append(var.toString());
            if (i < this.overVariables.size() - 1) {
                sb.append(", ");
            }
            i++;
        }
        sb.append("| ");

        sb.append(super.toSuperString());

        i = 0;
        for (PredicateExpression pe : this.getRestrictions()) {
            sb.append(pe.toString());
            if (i < this.getRestrictions().size() - 1) {
                sb.append(" n ");
            }
            i++;
        }
        if (this.lessEqual) {
            sb.append("<=");
        } else {
            sb.append(">=");
        }
        sb.append(this.cardinality).append(".\n");
        return sb.toString();
    }

    public boolean isLessEqual() {
        return lessEqual;
    }

    public void setLessEqual(boolean lessEqual) {
        this.lessEqual = lessEqual;
        if (!lessEqual) {
            this.useCuttingPlaneInference(false);
        }
    }

}
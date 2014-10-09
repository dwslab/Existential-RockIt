package de.dwslab.ai.riskmanagement.abduction.model.formulas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import de.dwslab.ai.riskmanagement.abduction.exceptions.ParseException;
import de.dwslab.ai.riskmanagement.abduction.model.expressions.IfExpression;
import de.dwslab.ai.riskmanagement.abduction.model.expressions.PredicateExpression;
import de.dwslab.ai.riskmanagement.abduction.model.predicates.PredicateAbstract;
import de.dwslab.ai.riskmanagement.abduction.model.variables.VariableAbstract;
import de.dwslab.ai.riskmanagement.abduction.model.variables.VariableType;

public class FormulaHard extends FormulaAbstract {

    private ArrayList<PredicateExpression> restrictions = new ArrayList<PredicateExpression>();
    private boolean conjunction = false;

    // private final RestrictionBuilder restrictionBuilder = null;

    public void setAllAsForVariables() {
        TreeSet<VariableType> forVar = new TreeSet<VariableType>();
        for (PredicateExpression expr : this.restrictions) {
            for (VariableAbstract var : expr.getVariables()) {
                if (var instanceof VariableType) {
                    forVar.add((VariableType) var);
                }
            }
        }
        for (IfExpression ifexpr : this.getIfExpressions()) {
            for (VariableAbstract var : ifexpr.getAllVariables()) {
                if (var instanceof VariableType) {
                    forVar.add((VariableType) var);
                }
            }
        }
        HashSet<VariableType> setset = new HashSet<VariableType>();
        for (VariableType fv : forVar) {
            setset.add(fv);
        }
        this.setForVariables(setset);
    }

    public FormulaHard(String name, HashSet<VariableType> forVariables,
            ArrayList<IfExpression> ifExpressions,
            ArrayList<PredicateExpression> restrictions, boolean usesConjunctions)
            throws ParseException {
        this.setForVariables(forVariables);
        this.setName(name);
        this.setIfExpressions(ifExpressions);
        this.setRestrictions(restrictions);
        this.conjunction = usesConjunctions;
    }

    public FormulaHard() {

    }

    public void setRestrictions(ArrayList<PredicateExpression> restrictions) throws ParseException {
        for (PredicateExpression res : restrictions) {
            this.checkRestrictionExpression(res);
        }
        this.restrictions = restrictions;
    }

    public void setRestrictions(PredicateExpression... restrictions) throws ParseException {
        for (PredicateExpression restriction : restrictions) {
            this.checkRestrictionExpression(restriction);
            this.restrictions.add(restriction);
        }
    }

    public ArrayList<PredicateExpression> getRestrictions() {
        return restrictions;
    }

    private void checkRestrictionExpression(PredicateExpression ex) throws ParseException {
        if (ex.getClass().equals(PredicateExpression.class)) {
            PredicateExpression exPred = ex;
            if (exPred.getPredicate().isObserved()) {
                throw new ParseException(
                        "Error in the restriction part of the formular. The predicate "
                                + exPred.getPredicate().getName()
                                + " is set to observed. But only hidden predicates are allowed in the restriction part.");
            }
        }
    }

    public void addRestriction(PredicateExpression expression) throws ParseException {
        this.checkRestrictionExpression(expression);
        this.restrictions.add(expression);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        // sb.append("  hard ");
        if (this.conjunction) {
            sb.append("(");
        }
        int i = 0;
        for (PredicateExpression pe : this.restrictions) {
            sb.append(pe.toString());
            if (i < this.restrictions.size() - 1) {
                if (this.conjunction) {
                    sb.append(" n ");
                } else {
                    sb.append(" v ");
                }
            }
            i++;
        }
        if (this.conjunction) {
            sb.append(")");
        }
        sb.append(".\n");

        return sb.toString();
    }

    protected String toSuperString() {
        return super.toString();
    }

    /**
     * Does the formula consists only of conjunction operators (and).
     */
    public void setConjunction() {
        this.conjunction = true;
    }

    /**
     * Does the formula consists only of disjunction operators (or).
     */
    public void setDisjunction() {
        this.conjunction = false;
    }

    /**
     * Does the formula consists only of conjunction operators (and).
     */
    public boolean isConjunction() {
        return conjunction;
    }

    /**
     * Does the formula consists only of disjunction operators (or).
     */

    public boolean isDisjunction() {
        return !conjunction;
    }

    @Override
    public HashSet<PredicateAbstract> getAllHiddenPredicatesSet() {
        HashSet<PredicateAbstract> result = new HashSet<PredicateAbstract>();

        for (PredicateExpression expr : this.restrictions) {
            result.add(expr.getPredicate());
        }

        return result;
    }

    // public RestrictionBuilder getRestrictionBuilder() {
    // return restrictionBuilder;
    // }
    //
    // public void setRestrictionBuilder(RestrictionBuilder restrictionBuilder) {
    // this.restrictionBuilder = restrictionBuilder;
    // }
}

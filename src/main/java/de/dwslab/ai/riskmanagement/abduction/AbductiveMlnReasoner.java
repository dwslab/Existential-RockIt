package de.dwslab.ai.riskmanagement.abduction;

import de.dwslab.ai.riskmanagement.abduction.model.Model;
import de.dwslab.ai.riskmanagement.abduction.model.formulas.FormulaAbstract;

public class AbductiveMlnReasoner {

    public Model getExtendedModel(Model model) {

        for (FormulaAbstract formula : model.getFormulas()) {
            System.out.println(formula.getAllHiddenPredicatesSet());
            System.out.println("hidden ==================");
            System.out.println(formula.getAllObservedPredicates());
            System.out.println("observed ==================");
            System.out.println(formula.getAllTypes());
            System.out.println("types ==================");
            System.out.println(formula.getForVariables());
            System.out.println("var ==================");
            System.out.println(formula.getIfExpressions());
            System.out.println("if ==================");
            System.out.println(formula.getName());
            break;
        }

        return null;

    }
}

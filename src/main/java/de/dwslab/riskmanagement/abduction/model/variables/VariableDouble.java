package de.dwslab.riskmanagement.abduction.model.variables;

public class VariableDouble extends VariableAbstract {

    public VariableDouble() {
    }

    public VariableDouble(String name) {
        this.setName(name);
    }

    public String toString() {
        return this.getName();
    }

}

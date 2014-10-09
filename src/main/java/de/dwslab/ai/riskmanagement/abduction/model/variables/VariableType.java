package de.dwslab.ai.riskmanagement.abduction.model.variables;

import de.dwslab.ai.riskmanagement.abduction.model.types.Type;

public class VariableType extends VariableAbstract {

    private Type type;

    public VariableType() {
    }

    public VariableType(String name, Type type) {
        this.setName(name);
        this.type = type;
    }

    public VariableType(String name) {
        this.setName(name);
    }

    public void setType(Type type) {
        this.type = type;

    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }

}

package de.dwslab.riskmanagement.abduction.model.variables;

public class VariableString extends VariableAbstract{

	public VariableString(String name){
		this.setName(name);
	}
	public VariableString(){
	}	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(this.getName()).append("\"");
		return sb.toString();
	}
	

}

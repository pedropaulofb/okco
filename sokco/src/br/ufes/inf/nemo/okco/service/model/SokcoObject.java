package br.ufes.inf.nemo.okco.service.model;

import java.util.ArrayList;

public class SokcoObject {

	String pathOwlFileString;
	String reasonerOption;
	String strength;
	ArrayList<String> setInstances;
	
	public String getPathOwlFileString() {
		return pathOwlFileString;
	}
	public void setPathOwlFileString(String pathOwlFileString) {
		this.pathOwlFileString = pathOwlFileString;
	}
	public String getReasonerOption() {
		return reasonerOption;
	}
	public void setReasonerOption(String reasonerOption) {
		this.reasonerOption = reasonerOption;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	public ArrayList<String> getSetInstances() {
		return setInstances;
	}
	public void setSetInstances(ArrayList<String> setInstances) {
		this.setInstances = setInstances;
	}
	
	
	
}

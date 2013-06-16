package ca.gobits.bnf.parser;

import java.util.Collection;


public class BNFStateDefinition {

	private String name;
	private Collection<BNFState> states;
	
	public BNFStateDefinition() {		
	}
	
	public BNFStateDefinition(String name, Collection<BNFState> states) {
		this.name = name;
		this.states = states;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<BNFState> getStates() {
		return states;
	}

	public void setStates(Collection<BNFState> states) {
		this.states = states;
	}
}

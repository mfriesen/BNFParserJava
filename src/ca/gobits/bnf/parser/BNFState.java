package ca.gobits.bnf.parser;

import ca.gobits.bnfparser.tokenizer.BNFToken;

public class BNFState {

	public enum Repetition { ZERO_OR_MORE };
		
	private String name;
	private BNFState nextState;
	private Repetition repetition;
	
	public BNFState() {		
	}

	public BNFState(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Repetition getRepetition() {
		return repetition;
	}

	public void setRepetition(Repetition repetition) {
		this.repetition = repetition;
	}

	public BNFState getNextState() {
		return nextState;
	}

	public void setNextState(BNFState nextState) {
		this.nextState = nextState;
	}
	
	public boolean match(BNFToken token) {
		return name.equals(token.getValue());
	}
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return true;
	}
	
	public boolean isEnd() {
		return false;
	}
	
	public String toString() {
		return "state " + getName();
	}
}

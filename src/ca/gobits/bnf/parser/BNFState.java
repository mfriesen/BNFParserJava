package ca.gobits.bnf.parser;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFState {

	public enum Repetition { NONE, ZERO_OR_MORE };
		
	private String name;
	private BNFState nextState;
	private Repetition repetition;
	
	public BNFState() {
		this.repetition = Repetition.NONE;
	}

	public BNFState(String name) {
		this();
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
		return nameMatches(token) || repetition == Repetition.ZERO_OR_MORE;
	}
	
	private boolean nameMatches(BNFToken token) {
		return getName().equals(token.getValue());
	} 
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return nameMatches(token);
	}
	
	public boolean isEnd() {
		return false;
	}
	
	public String toString() {
		return "state " + getName();
	}
}

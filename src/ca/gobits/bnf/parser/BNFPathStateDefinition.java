package ca.gobits.bnf.parser;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFPathStateDefinition implements BNFPath {

	private BNFStateDefinition stateDefinition;
	private BNFRepetition repetition;
	private BNFToken token;
	private int position;

	public BNFPathStateDefinition() {
		this.position = 0;
	}

	public BNFStateDefinition getStateDefinition() {
		return this.stateDefinition;
	}

	public void setStateDefinition(BNFStateDefinition stateDefinition) {
		this.stateDefinition = stateDefinition;
	}

	@Override
	public BNFRepetition getRepetition() {
		return this.repetition;
	}

	public void setRepetition(BNFRepetition repetition) {
		this.repetition = repetition;
	}

	@Override
	public BNFToken getToken() {
		return this.token;
	}

	public void setToken(BNFToken token) {
		this.token = token;
	}

	@Override
	public boolean isStateEnd() {
		return false;
	}
	
	@Override
	public String toString() {
		return "state definition " + stateDefinition.getName() + " repetition " + repetition + " token " + token.getValue();
	}

	@Override
	public boolean isStateDefinition() {
		return true;
	}
	
	public BNFState getNextSequence() {
		
		BNFState state = null;
		
		if (position < stateDefinition.getStates().size()) {
			state = stateDefinition.getStates().get(position);
			position++;
		}
		
		return state;
	}

	@Override
	public BNFState getNextState() {
		
		BNFState state = null;
		
		if (position < stateDefinition.getStates().size()) {
			state = stateDefinition.getStates().get(position).getNextState();
		}
		
		return state;
	}
}
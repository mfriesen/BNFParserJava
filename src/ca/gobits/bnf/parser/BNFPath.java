package ca.gobits.bnf.parser;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

public interface BNFPath {

	boolean isStateEnd();

	BNFToken getToken();

	BNFRepetition getRepetition();

	boolean isStateDefinition();

	BNFState getNextState();


}

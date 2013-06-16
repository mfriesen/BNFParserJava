package ca.gobits.bnf.parser;

import ca.gobits.bnfparser.tokenizer.BNFToken;

public class BNFStateEmpty extends BNFState {
	public boolean match(BNFToken token) {
		return true;
	}
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return token != null && token.getValue().trim().length() == 0 ? true : false;
	}
}

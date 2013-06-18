package ca.gobits.bnf.parser;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFStateQuotedString extends BNFState {
	
	public boolean match(BNFToken token) {
		String value = token.getValue();
		return value.startsWith("\"") && value.endsWith("\"");
	}
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return true;
	}
}

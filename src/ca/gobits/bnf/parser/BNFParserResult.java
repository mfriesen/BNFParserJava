package ca.gobits.bnf.parser;

import ca.gobits.bnfparser.tokenizer.BNFToken;

public class BNFParserResult {

	private BNFToken top;
	private BNFToken error;
	private boolean success;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public BNFParserResult() {		
	}

	public BNFToken getTop() {
		return top;
	}

	public void setTop(BNFToken top) {
		this.top = top;
	}

	public BNFToken getError() {
		return error;
	}

	public void setError(BNFToken error) {
		this.error = error;
	}
}

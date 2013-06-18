package ca.gobits.bnf.parser;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParseResultImpl implements BNFParseResult {

	private BNFToken top;
	private BNFToken error;
	private BNFToken maxToken;
	private boolean success;

	public BNFParseResultImpl() {		
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public BNFToken getTop() {
		return top;
	}

	public void setTop(BNFToken top) {
		this.top = top;
	}

	@Override
	public BNFToken getError() {
		return error;
	}

	public void setError(BNFToken error) {
		this.error = error;
	}

	public void setMaxMatchToken(BNFToken token) {
		if (this.maxToken == null || (token != null && token.getId() > this.maxToken.getId())) {
			this.maxToken = token;
		}
	}

	public void complete() {

		if (!isSuccess()) {			
			setError(maxToken);
		}
	}
}

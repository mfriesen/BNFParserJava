package ca.gobits.bnf.tokenizer;

public class BNFTokenizerParams {

	private boolean includeWhitespace;
	private boolean includeWhitespaceOther;
	private boolean includeWhitespaceNewlines;
	
	public BNFTokenizerParams() {		
	}

	public boolean isIncludeWhitespace() {
		return this.includeWhitespace;
	}

	public void setIncludeWhitespace(boolean includeWhitespace) {
		this.includeWhitespace = includeWhitespace;
	}

	public boolean isIncludeWhitespaceOther() {
		return this.includeWhitespaceOther;
	}

	public void setIncludeWhitespaceOther(boolean includeWhitespaceOther) {
		this.includeWhitespaceOther = includeWhitespaceOther;
	}

	public boolean isIncludeWhitespaceNewlines() {
		return this.includeWhitespaceNewlines;
	}

	public void setIncludeWhitespaceNewlines(boolean includeWhitespaceNewlines) {
		this.includeWhitespaceNewlines = includeWhitespaceNewlines;
	}
}

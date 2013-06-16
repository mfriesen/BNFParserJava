package ca.gobits.bnfparser.tokenizer;

public class BNFToken {
	
	public enum BNFTokenType { 
		COMMENT, 
		QUOTED_STRING,
		NUMBER, 
		WORD, 
		SYMBOL, 
		WHITESPACE
	};
	
	private String value;
	private BNFTokenType type;
	private BNFToken nextToken;

	public BNFTokenType getType() {
		return type;
	}

	public void setType(BNFTokenType type) {
		this.type = type;
	}

	public BNFToken() {		
	}

	public BNFToken(String value) {
		this.value = value;
	}

	public void appendValue(char c) {
		this.value = this.value + String.valueOf(c);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public BNFToken getNextToken() {
		return nextToken;
	}

	public void setNextToken(BNFToken nextToken) {
		this.nextToken = nextToken;
	}
	
	public String toString() {
		return "TOKEN: " + getValue();
	}

	public boolean isSymbol() {
		return type == BNFTokenType.SYMBOL;
	}

	public boolean isWord() {
		return type == BNFTokenType.WORD;
	}

	public boolean isQuotedString() {
		return type == BNFTokenType.QUOTED_STRING;
	}
	
	public boolean isNumber() {
		return type == BNFTokenType.NUMBER;
	}
	
	public boolean isComment() {
		return type == BNFTokenType.COMMENT;
	}
	
	public boolean isWhitespace() {
		return type == BNFTokenType.WHITESPACE;
	}
}

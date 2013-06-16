package ca.gobits.bnf.parser;

import ca.gobits.bnfparser.tokenizer.BNFToken;


public class BNFPath {

	private int pathPosition;
	private int pathCount;
	
	private BNFToken token;
	private BNFState state;
	
	public BNFPath() {		
	}
	
	public BNFPath(BNFState state, BNFToken token) {
		this.state = state;
		this.token = token;
	}
	
	public BNFToken getToken() {
		return token;
	}

	public void setToken(BNFToken token) {
		this.token = token;
	}

	public BNFState getState() {
		return state;
	}

	public void setState(BNFState state) {
		this.state = state;
	}

	public int getPathPosition() {
		return pathPosition;
	}

	public void setPathPosition(int pathPosition) {
		this.pathPosition = pathPosition;
	}

	public int getPathCount() {
		return pathCount;
	}

	public void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
}

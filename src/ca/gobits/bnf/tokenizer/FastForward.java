package ca.gobits.bnf.tokenizer;

import ca.gobits.bnf.tokenizer.BNFTokenizerFactoryImpl.BNFTokenizerType;

public class FastForward {

	private BNFTokenizerType start = BNFTokenizerType.NONE;
	private BNFTokenizerType[] end = new BNFTokenizerType[] { BNFTokenizerType.NONE };
	private StringBuilder sb = new StringBuilder();
	
	public FastForward() {		
	}

	public BNFTokenizerType getStart() {
		return start;
	}

	public void setStart(BNFTokenizerType start) {
		this.start = start;
	}

	public BNFTokenizerType[] getEnd() {
		return end;
	}

	public void setEnd(BNFTokenizerType[] end) {
		this.end = end;
	}

	public void setEnd(BNFTokenizerType type) {
		this.end = new BNFTokenizerType[] { type };
	}
	
	public boolean isActive() {
		return start != BNFTokenizerType.NONE;
	}
	
	public boolean isComplete(BNFTokenizerType type, BNFTokenizerType lastType, int i, int len) {		
		return isMatch(type, lastType)  || (i == len - 1);
	}
	
	private boolean isMatch(BNFTokenizerType type, BNFTokenizerType lastType) {

		boolean match = false;
		
		BNFTokenizerType[] tmpType = new BNFTokenizerType[end.length];
		tmpType[0] = type;
		for (int i = 1; i < end.length; i++) {
			tmpType[i] = lastType;
		}
		
		if (tmpType.length == end.length) {
			
			match = true;
			for (int i = 0 ; i < tmpType.length; i++) {
				if (tmpType[i] != end[i]) {
					match = false;
					break;
				}
			}
		}
		
		return match;
	}

	public void complete() {
		this.start = BNFTokenizerType.NONE;
		setEnd(BNFTokenizerType.NONE);
		sb.delete(0, sb.length());
	}

	public void appendIfActive(char c) {
		if (isActive()) {
			sb.append(String.valueOf(c));
		}
	}

	public void appendIfActive(String s) {
		if (isActive()) {
			sb.append(s);
		}
	}

	public String getString() {
		return sb.toString();
	}
}
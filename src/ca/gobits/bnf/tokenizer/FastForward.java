//
//  Copyright (c) 2013 Mike Friesen
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

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
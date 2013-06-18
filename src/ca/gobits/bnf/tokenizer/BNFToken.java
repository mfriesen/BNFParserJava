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

public class BNFToken {
	
	public enum BNFTokenType { 
		COMMENT, 
		QUOTED_STRING,
		NUMBER, 
		WORD, 
		SYMBOL, 
		WHITESPACE
	}
	
	private int id;
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
	
	@Override
	public String toString() {
		return "TOKEN value: " + getValue() + " id: " + getId() + " type: " + getType();
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

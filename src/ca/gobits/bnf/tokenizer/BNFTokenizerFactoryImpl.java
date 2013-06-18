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

import java.util.Stack;

import ca.gobits.bnf.tokenizer.BNFToken.BNFTokenType;
public class BNFTokenizerFactoryImpl implements BNFTokenizerFactory {
	
	private enum BNFTokenizerType {
		NONE, 
		COMMENT_SINGLE_LINE, COMMENT_MULTI_LINE, 
		QUOTE_SINGLE, QUOTE_DOUBLE,
		NUMBER, 
		LETTER, 
		SYMBOL, SYMBOL_HASH, SYMBOL_AT, SYMBOL_STAR, SYMBOL_SLASH_FORWARD, 
		WHITESPACE, WHITESPACE_OTHER
	};
	
	@Override
	public BNFToken tokens(String text) {

		StringBuilder sb = new StringBuilder();
		Stack<BNFToken> stack = new Stack<BNFToken>();
		BNFTokenizerType fastForwardStart = BNFTokenizerType.NONE;
		BNFTokenizerType fastForwardEnd = BNFTokenizerType.NONE;
		
		BNFTokenizerType[] lastTypes = new BNFTokenizerType[] { BNFTokenizerType.NONE, BNFTokenizerType.NONE };
		
		int len = text.length();

		for (int i = 0; i < len; i++) {
			
			char c = text.charAt(i);
			BNFTokenizerType type = getType(c, lastTypes);

			boolean fastForwarding = isFastForwarding(fastForwardEnd);
						
			if (fastForwarding) {
				
				boolean fastForwardEnded = isFastForwardingEnded(stack, fastForwardEnd, type, i, len);
				sb.append(String.valueOf(c));

				if (fastForwardEnded) {
					
					finishFastForward(stack, sb, fastForwardStart, fastForwardEnd);

					fastForwarding = false;
					fastForwardEnd = BNFTokenizerType.NONE;
					sb.delete(0, sb.length());
				}
				
			} else {
				
				fastForwardEnd = calculateFastForwardEnd(type, stack);
				fastForwardStart = type;

				if (fastForwardEnd != BNFTokenizerType.NONE) { 
					sb.append(c);
				} else if (!isWhitespace(type)) {
						
					if (type != BNFTokenizerType.NONE) {
					
						if (isAppendable(lastTypes, type)) {
							
							stack.peek().appendValue(c);
							
						} else {
							addBNFToken(stack, type, c);
						}					
					}
				}
			}
			
			lastTypes[1] = lastTypes[0];
			lastTypes[0] = type;
		}
		
		return !stack.isEmpty() ? stack.firstElement() : new BNFToken("");
	}

	private BNFTokenizerType calculateFastForwardEnd(BNFTokenizerType type, Stack<BNFToken> stack) {
		
		BNFToken last = !stack.isEmpty() ? stack.peek() : null;
		BNFTokenizerType tokenizerType = BNFTokenizerType.NONE;
		
		if (type == BNFTokenizerType.COMMENT_SINGLE_LINE) {
			
			tokenizerType = BNFTokenizerType.WHITESPACE_OTHER;
			
		} else if (type == BNFTokenizerType.COMMENT_MULTI_LINE) {
			
			tokenizerType = BNFTokenizerType.COMMENT_MULTI_LINE;					
		
		} else if (type == BNFTokenizerType.QUOTE_DOUBLE) {
			
			tokenizerType = BNFTokenizerType.QUOTE_DOUBLE;
		
		} else if (type == BNFTokenizerType.QUOTE_SINGLE && !isWord(last)) {
			
			tokenizerType = BNFTokenizerType.QUOTE_SINGLE;
		}

		return tokenizerType;
	}

	private boolean isWord(BNFToken last) {
		return last != null && last.isWord();
	}

	private void finishFastForward(Stack<BNFToken> stack, StringBuilder sb, BNFTokenizerType fastForwardStart, BNFTokenizerType fastForwardType) {

		if (isComment(fastForwardStart)) {
			
			stack.pop();
			setNextToken(stack, null);
			
		} else {
			
			addBNFToken(stack, fastForwardType, sb.toString());
		}
	}

	private void addBNFToken(Stack<BNFToken> stack, BNFTokenizerType type, char c) {
		addBNFToken(stack, type, String.valueOf(c));
	}
	
	private void addBNFToken(Stack<BNFToken> stack, BNFTokenizerType type, String c) {
		
		BNFToken token = createBNFToken(c, type);
		
		if (!stack.isEmpty()) {
			stack.peek().setNextToken(token);
		}
		
		stack.push(token);
	}

	private void setNextToken(Stack<BNFToken> stack, BNFToken nextToken) {
		if (!stack.isEmpty()) {
			stack.peek().setNextToken(nextToken);
		}
	}

	private boolean isFastForwardingEnded(Stack<BNFToken> stack, BNFTokenizerType ffType, BNFTokenizerType type, int i, int len) {
		return type == ffType  || (i == len - 1);
	}

	private boolean isFastForwarding(BNFTokenizerType type) {
		return type != BNFTokenizerType.NONE;
	}

	private boolean isAppendable(BNFTokenizerType[] lasts, BNFTokenizerType current) {
		return lasts[0] == current && (current == BNFTokenizerType.LETTER || current == BNFTokenizerType.NUMBER);
	}
	
	private BNFToken createBNFToken(String value, BNFTokenizerType type) {
		BNFToken token = new BNFToken();
		token.setValue(value);

		if (isComment(type)) {
			token.setType(BNFTokenType.COMMENT);
		} else if (isNumber(type)) {
			token.setType(BNFTokenType.NUMBER);
		} else if (isLetter(type)) {
			token.setType(BNFTokenType.WORD);
		} else if (isSymbol(type)) {
			token.setType(BNFTokenType.SYMBOL);
		} else if (isWhitespace(type)) {
			token.setType(BNFTokenType.WHITESPACE);
		} else if (isQuote(type)) {
			token.setType(BNFTokenType.QUOTED_STRING);
		}

		
		return token;
	}
	
	private BNFTokenizerType getType(char c, BNFTokenizerType[] lastTypes) {
		
		BNFTokenizerType type = getType(c);
		
		// check last type for single line comment
		if (isSingleLineComment(lastTypes, type)) {
			
			type = BNFTokenizerType.COMMENT_SINGLE_LINE;
			
		} else if (isMultiLineComment(lastTypes, type)) {
			
			type = BNFTokenizerType.COMMENT_MULTI_LINE;
			
		}
		
		return type;
	}
		
	private boolean isQuote(BNFTokenizerType type) {
		return type == BNFTokenizerType.QUOTE_DOUBLE || type == BNFTokenizerType.QUOTE_SINGLE;
	}

	private boolean isMultiLineComment(BNFTokenizerType[] lastTypes, BNFTokenizerType type) {
		return (lastTypes[0] == BNFTokenizerType.SYMBOL_STAR && type == BNFTokenizerType.SYMBOL_SLASH_FORWARD)
				|| (lastTypes[0] == BNFTokenizerType.SYMBOL_SLASH_FORWARD && type == BNFTokenizerType.SYMBOL_STAR);
	}

	private boolean isSingleLineComment(BNFTokenizerType[] lastTypes, BNFTokenizerType type) {
		return lastTypes[0] == BNFTokenizerType.SYMBOL_SLASH_FORWARD && type == BNFTokenizerType.SYMBOL_SLASH_FORWARD;
	}
	
	public boolean isSymbol(BNFTokenizerType type) {
		return type == BNFTokenizerType.SYMBOL 
				|| type == BNFTokenizerType.SYMBOL_HASH
				|| type == BNFTokenizerType.SYMBOL_AT
				|| type == BNFTokenizerType.SYMBOL_STAR
				|| type == BNFTokenizerType.SYMBOL_SLASH_FORWARD;
	}
	
	public boolean isWhitespace(BNFTokenizerType type) {
		return type == BNFTokenizerType.WHITESPACE || type == BNFTokenizerType.WHITESPACE_OTHER;
	}
	
	public boolean isComment(BNFTokenizerType type) {
		return type == BNFTokenizerType.COMMENT_MULTI_LINE
				|| type == BNFTokenizerType.COMMENT_SINGLE_LINE;
	}
	
	public boolean isNumber(BNFTokenizerType type) {
		return type == BNFTokenizerType.NUMBER;
	}
	
	public boolean isLetter(BNFTokenizerType type) {
		return type == BNFTokenizerType.LETTER;
	}

	public boolean isNone(BNFTokenizerType type) {
		return type == BNFTokenizerType.NONE;
	}
	
	private BNFTokenizerType getType(int c) {
		 if (c >= 0 && c <= ' ') { // From: 0 to: 32 From:0x00 to:0x20
	        return BNFTokenizerType.WHITESPACE_OTHER;
	 	} else if (c == 32) {
	 		return BNFTokenizerType.WHITESPACE;
	    } else if (c == 33) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '"') { // From: 34 to: 34 From:0x22 to:0x22
	        return BNFTokenizerType.QUOTE_DOUBLE;
	    } else if (c == '#') { // From: 35 to: 35 From:0x23 to:0x23
	        return BNFTokenizerType.SYMBOL_HASH;
	    } else if (c >= 36 && c <= 38) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '\'') { // From: 39 to: 39 From:0x27 to:0x27
	        return BNFTokenizerType.QUOTE_SINGLE;
	    } else if (c >= 40 && c <= 41) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == 42) {
	    	return BNFTokenizerType.SYMBOL_STAR;
	    } else if (c == '+') { // From: 43 to: 43 From:0x2B to:0x2B
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == 44) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '-') { // From: 45 to: 45 From:0x2D to:0x2D
	        return BNFTokenizerType.NUMBER;
	    } else if (c == '.') { // From: 46 to: 46 From:0x2E to:0x2E
	        return BNFTokenizerType.NUMBER;
	    } else if (c == '/') { // From: 47 to: 47 From:0x2F to:0x2F
	        return BNFTokenizerType.SYMBOL_SLASH_FORWARD;
	    } else if (c >= '0' && c <= '9') { // From: 48 to: 57 From:0x30 to:0x39
	        return BNFTokenizerType.NUMBER;
	    } else if (c >= 58 && c <= 63) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '@') { // From: 64 to: 64 From:0x40 to:0x40
	        return BNFTokenizerType.SYMBOL_AT;
	    } else if (c >= 'A' && c <= 'Z') { // From: 65 to: 90 From:0x41 to:0x5A
	        return BNFTokenizerType.LETTER;
	    } else if (c >= 91 && c <= 96) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 'a' && c <= 'z') { // From: 97 to:122 From:0x61 to:0x7A
	        return BNFTokenizerType.LETTER;
	    } else if (c >= 123 && c <= 191) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0xC0 && c <= 0xFF) { // From:192 to:255 From:0xC0 to:0xFF
	        return BNFTokenizerType.LETTER;
	    } else if (c >= 0x19E0 && c <= 0x19FF) { // khmer symbols
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0x2000 && c <= 0x2BFF) { // various symbols
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0x2E00 && c <= 0x2E7F) { // supplemental punctuation
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0x3000 && c <= 0x303F) { // cjk symbols & punctuation
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0x3200 && c <= 0x33FF) { // enclosed cjk letters and months, cjk compatibility
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0x4DC0 && c <= 0x4DFF) { // yijing hexagram symbols
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0xFE30 && c <= 0xFE6F) { // cjk compatibility forms, small form variants
	        return BNFTokenizerType.SYMBOL;
	    } else if (c >= 0xFF00 && c <= 0xFFFF) { // hiragana & katakana halfwitdh & fullwidth forms, Specials
	        return BNFTokenizerType.SYMBOL;
	    } else {
	        return BNFTokenizerType.LETTER;
	    }
	}
}
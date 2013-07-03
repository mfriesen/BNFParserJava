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
	
	public enum BNFTokenizerType {
		NONE, 
		COMMENT_SINGLE_LINE, COMMENT_MULTI_LINE, 
		QUOTE_SINGLE, QUOTE_SINGLE_ESCAPED, 
		QUOTE_DOUBLE, QUOTE_DOUBLE_ESCAPED,
		NUMBER, 
		LETTER, 
		SYMBOL, 
		SYMBOL_HASH, SYMBOL_AT, SYMBOL_STAR, SYMBOL_FORWARD_SLASH, SYMBOL_BACKWARD_SLASH, 
		WHITESPACE, WHITESPACE_OTHER, WHITESPACE_NEWLINE
	}
	
	@Override
	public BNFToken tokens(String text) {
		return tokens(text, new BNFTokenizerParams());
	}
	
	@Override
	public BNFToken tokens(String text, BNFTokenizerParams params) {

		Stack<BNFToken> stack = new Stack<BNFToken>();
		BNFFastForward ff = new BNFFastForward();
		
		BNFTokenizerType lastType = BNFTokenizerType.NONE;
		
		int len = text.length();

		for (int i = 0; i < len; i++) {
			
			char c = text.charAt(i);
			BNFTokenizerType type = getType(c, lastType);

			if (ff.isActive()) {
								
				ff.appendIfActive(c);
				
				boolean isFastForwardComplete = ff.isComplete(type, lastType, i, len);

				if (isFastForwardComplete) {
					
					finishFastForward(stack, ff);
					ff.complete();
				}
				
			} else {

				calculateFastForward(ff, type, stack, lastType);
				
				if (ff.isActive()) {
					
					ff.appendIfActive(c);
				
				} else if (includeText(type, params)) {
						
					if (isAppendable(lastType, type)) {
						
						stack.peek().appendValue(c);
						
					} else {
						addBNFToken(stack, type, c);
					}					
				}
			}
			
			lastType = type;
		}
		
		return !stack.isEmpty() ? stack.firstElement() : new BNFToken("");
	}

	private boolean includeText(BNFTokenizerType type, BNFTokenizerParams params) {
		return (params.isIncludeWhitespace() && type == BNFTokenizerType.WHITESPACE) 
				|| (params.isIncludeWhitespaceOther() && type == BNFTokenizerType.WHITESPACE_OTHER)
				|| (params.isIncludeWhitespaceNewlines() && type == BNFTokenizerType.WHITESPACE_NEWLINE)
				|| !isWhitespace(type);
	}
	
	private void calculateFastForward(BNFFastForward ff, BNFTokenizerType type, Stack<BNFToken> stack, BNFTokenizerType lastType) {
		
		BNFToken last = !stack.isEmpty() ? stack.peek() : null;
		ff.setStart(BNFTokenizerType.NONE);

		// single line comment
		if (lastType == BNFTokenizerType.SYMBOL_FORWARD_SLASH && type == BNFTokenizerType.SYMBOL_FORWARD_SLASH) { 
			
			ff.setStart(BNFTokenizerType.COMMENT_SINGLE_LINE);
			ff.setEnd(new BNFTokenizerType[] { BNFTokenizerType.WHITESPACE_NEWLINE });
			
			BNFToken token = stack.pop();
			ff.appendIfActive(token.getValue());
			
		// multi line comment
		} else if (lastType == BNFTokenizerType.SYMBOL_FORWARD_SLASH && type == BNFTokenizerType.SYMBOL_STAR) {
			
			ff.setStart(BNFTokenizerType.COMMENT_MULTI_LINE);
			ff.setEnd(new BNFTokenizerType[] { BNFTokenizerType.SYMBOL_FORWARD_SLASH, BNFTokenizerType.SYMBOL_STAR });
			
			BNFToken token = stack.pop();
			ff.appendIfActive(token.getValue());

		} else if (type == BNFTokenizerType.QUOTE_DOUBLE) {

			ff.setStart(BNFTokenizerType.QUOTE_DOUBLE);			
			ff.setEnd(BNFTokenizerType.QUOTE_DOUBLE);
		
		} else if (type == BNFTokenizerType.QUOTE_SINGLE && !isWord(last)) {
			
			ff.setStart(BNFTokenizerType.QUOTE_SINGLE);
			ff.setEnd(BNFTokenizerType.QUOTE_SINGLE);
		}
	}

	private boolean isWord(BNFToken last) {
		return last != null && last.isWord();
	}

	private void finishFastForward(Stack<BNFToken> stack, BNFFastForward ff) {

		if (isComment(ff.getStart())) {
			
			setNextToken(stack, null);
			
		} else {
			
			addBNFToken(stack, ff.getStart(), ff.getString());
		}
	}

	private void addBNFToken(Stack<BNFToken> stack, BNFTokenizerType type, char c) {
		addBNFToken(stack, type, String.valueOf(c));
	}
	
	private void addBNFToken(Stack<BNFToken> stack, BNFTokenizerType type, String c) {
		
		BNFToken token = createBNFToken(c, type);
		
		if (!stack.isEmpty()) {
			BNFToken peek = stack.peek();			
			peek.setNextToken(token);
			token.setId(peek.getId() + 1);
		} else {
			token.setId(1);
		}
		
		stack.push(token);
	}

	private void setNextToken(Stack<BNFToken> stack, BNFToken nextToken) {
		if (!stack.isEmpty()) {
			stack.peek().setNextToken(nextToken);
		}
	}

	private boolean isAppendable(BNFTokenizerType lastType, BNFTokenizerType current) {
		return lastType == current && (current == BNFTokenizerType.LETTER || current == BNFTokenizerType.NUMBER);
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
		} else if (type == BNFTokenizerType.WHITESPACE_NEWLINE) { 
			token.setType(BNFTokenType.WHITESPACE_NEWLINE);			
		} else if (isWhitespace(type)) {
			token.setType(BNFTokenType.WHITESPACE);
		} else if (isQuote(type)) {
			token.setType(BNFTokenType.QUOTED_STRING);
		}
		
		return token;
	}
		
	private boolean isQuote(BNFTokenizerType type) {
		return type == BNFTokenizerType.QUOTE_DOUBLE || type == BNFTokenizerType.QUOTE_SINGLE;
	}
	
	private boolean isSymbol(BNFTokenizerType type) {
		return type == BNFTokenizerType.SYMBOL 
				|| type == BNFTokenizerType.SYMBOL_HASH
				|| type == BNFTokenizerType.SYMBOL_AT
				|| type == BNFTokenizerType.SYMBOL_STAR
				|| type == BNFTokenizerType.SYMBOL_FORWARD_SLASH
				|| type == BNFTokenizerType.SYMBOL_BACKWARD_SLASH;
	}
	
	private boolean isWhitespace(BNFTokenizerType type) {
		return type == BNFTokenizerType.WHITESPACE || type == BNFTokenizerType.WHITESPACE_OTHER || type == BNFTokenizerType.WHITESPACE_NEWLINE;
	}
	
	private boolean isComment(BNFTokenizerType type) {
		return type == BNFTokenizerType.COMMENT_MULTI_LINE
				|| type == BNFTokenizerType.COMMENT_SINGLE_LINE;
	}
	
	private boolean isNumber(BNFTokenizerType type) {
		return type == BNFTokenizerType.NUMBER;
	}
	
	private boolean isLetter(BNFTokenizerType type) {
		return type == BNFTokenizerType.LETTER;
	}
	
	private BNFTokenizerType getType(int c, BNFTokenizerType lastType) {
		if (c == 10 || c == 13) {
			return BNFTokenizerType.WHITESPACE_NEWLINE;
		} else if (c >= 0 && c <= 31) { // From: 0 to: 31 From:0x00 to:0x20
	        return BNFTokenizerType.WHITESPACE_OTHER;
	 	} else if (c == 32) {
	 		return BNFTokenizerType.WHITESPACE;
	    } else if (c == 33) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '"') { // From: 34 to: 34 From:0x22 to:0x22
	    	return lastType == BNFTokenizerType.SYMBOL_BACKWARD_SLASH ? BNFTokenizerType.QUOTE_DOUBLE_ESCAPED : BNFTokenizerType.QUOTE_DOUBLE;
	    } else if (c == '#') { // From: 35 to: 35 From:0x23 to:0x23
	        return BNFTokenizerType.SYMBOL_HASH;
	    } else if (c >= 36 && c <= 38) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '\'') { // From: 39 to: 39 From:0x27 to:0x27
	        return lastType == BNFTokenizerType.SYMBOL_BACKWARD_SLASH ? BNFTokenizerType.QUOTE_SINGLE_ESCAPED : BNFTokenizerType.QUOTE_SINGLE;
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
	        return BNFTokenizerType.SYMBOL_FORWARD_SLASH;
	    } else if (c >= '0' && c <= '9') { // From: 48 to: 57 From:0x30 to:0x39
	        return BNFTokenizerType.NUMBER;
	    } else if (c >= 58 && c <= 63) {
	        return BNFTokenizerType.SYMBOL;
	    } else if (c == '@') { // From: 64 to: 64 From:0x40 to:0x40
	        return BNFTokenizerType.SYMBOL_AT;
	    } else if (c >= 'A' && c <= 'Z') { // From: 65 to: 90 From:0x41 to:0x5A
	        return BNFTokenizerType.LETTER;
	    } else if (c == 92) { // /
	    	return BNFTokenizerType.SYMBOL_BACKWARD_SLASH;
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
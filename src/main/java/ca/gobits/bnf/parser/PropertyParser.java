package ca.gobits.bnf.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import ca.gobits.bnf.tokenizer.BNFToken;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactory;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactoryImpl;
import ca.gobits.bnf.tokenizer.BNFTokenizerParams;
import ca.gobits.bnf.tokenizer.BNFToken.BNFTokenType;

public class PropertyParser {

	private BNFTokenizerFactory tokenizer = new BNFTokenizerFactoryImpl();
	
	public Map<String, String> parse(InputStream is) throws IOException {
		String str = IOUtils.toString(is);
		return parse(str);
	}

	public Map<String, String> parse(String str) {
		
		Map<String, String> map = new HashMap<String, String>();
		BNFTokenizerParams params = new BNFTokenizerParams();
		params.setIncludeWhitespace(true);		
		params.setIncludeWhitespaceNewlines(true);
		BNFToken token = tokenizer.tokens(str, params);
		
		String start = "";
		StringBuilder sb = new StringBuilder();
		
		while (token != null) {
		
			if (token.getType() == BNFTokenType.WHITESPACE_NEWLINE) {
				
				if (hasText(start) && hasText(sb.toString())) {
					map.put(start.trim(), sb.toString().trim());
				}
				
				start = "";
				sb = new StringBuilder();
				
			} else if (token.getValue().equals("=")) {
				
				start = sb.toString();
				sb = new StringBuilder();
				
			} else {
				
				sb.append(token.getValue());
			}
			
			token = token.getNextToken();
		}
		
		if (hasText(start) && hasText(sb.toString())) {
			map.put(start.trim(), sb.toString().trim());
		}
		
		return map;
	}

	private boolean hasText(String s) {
		return s != null && s.length() > 0;
	}
}
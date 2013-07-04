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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class BNFTokenizerFactoryTest {

	private BNFTokenizerFactory factory = new BNFTokenizerFactoryImpl();
	
	@Test
	public void testEmpty() {
		// given
		String s = "";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("", token.getStringValue());
		assertFalse(token.isWord());
		assertFalse(token.isNumber());
		assertFalse(token.isSymbol());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testSymbolAndWhiteSpace() {
		// given
		String s = "{ \n}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getStringValue());
		assertEquals(1, token.getId());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getStringValue());
		assertEquals(2, token.getId());
		assertTrue(token.isSymbol());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testSingleLineComment() {
		// given
		String s = "{ }//bleh\nasd";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getStringValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getStringValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("asd", token.getStringValue());
		assertNull(token.getNextToken());
	}

	@Test
	public void testMultiLineComment() {
		// given
		String s = "{ }/*bleh\n\nffsdf\n*/asd";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getStringValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getStringValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("asd", token.getStringValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testQuotedString01() {
		// given
		String s = "hi \"asd\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("hi", token.getStringValue());
		token = token.getNextToken();
		assertEquals("\"asd\"", token.getStringValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString02() {
		// given
		String s = "\"asd\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd\"", token.getStringValue());
		assertTrue(token.isQuotedString());
	}

	@Test
	public void testQuotedString03() {
		// given
		String s = "\"asd's\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd's\"", token.getStringValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString04() {
		// given
		String s = "\"asd's";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd's", token.getStringValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString05() {
		// given
		String s = "{ \"asd\":\"123\"}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getStringValue());
		token = token.getNextToken();
		
		assertEquals("\"asd\"", token.getStringValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals(":", token.getStringValue());
		token = token.getNextToken();

		assertEquals("\"123\"", token.getStringValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals("}", token.getStringValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testQuotedString06() {
		// given
		String s = "'asd':'123'}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("'asd'", token.getStringValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals(":", token.getStringValue());
		token = token.getNextToken();

		assertEquals("'123'", token.getStringValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals("}", token.getStringValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testQuotedNumber01() {
		// given
		String s = "'asd':123}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("'asd'", token.getStringValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals(":", token.getStringValue());
		token = token.getNextToken();

		assertEquals("123", token.getStringValue());
		assertTrue(token.isNumber());
		token = token.getNextToken();
		
		assertEquals("}", token.getStringValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testAHrefLink() {
		// given
		String s = "{\"notes\":\"Different browsers have support for different video formats, see sub-features for details. \\r\\n\\r\\nThe Android browser (before 2.3) requires <a href=\\\"http://www.broken-links.com/2010/07/08/making-html5-video-work-on-android-phones/\\\">specific handling</a> to run the video element.\"}";
		
		// when
		BNFToken token = factory.tokens(s);

		assertEquals("{", token.getStringValue());
		token = token.getNextToken();

		assertEquals("\"notes\"", token.getStringValue());
		token = token.getNextToken();

		assertEquals(":", token.getStringValue());
		token = token.getNextToken();
		
		assertEquals("\"Different browsers have support for different video formats, see sub-features for details. \\r\\n\\r\\nThe Android browser (before 2.3) requires <a href=\\\"http://www.broken-links.com/2010/07/08/making-html5-video-work-on-android-phones/\\\">specific handling</a> to run the video element.\"", token.getStringValue());
		token = token.getNextToken();
		
		assertEquals("}", token.getStringValue());
		token = token.getNextToken();
	}
	
	@Test
	public void testJsonGrammar() throws Exception {
		
		// given
		InputStream in = getClass().getResourceAsStream("/json.bnf");
		String s = IOUtils.toString(in, "UTF-8");
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("@", token.getStringValue());
		token = token.getNextToken();
		assertEquals("start", token.getStringValue());
		assertTrue(token.isWord());
		token = token.getNextToken();
		assertEquals("=", token.getStringValue());
		token = token.getNextToken();
		assertEquals("Empty", token.getStringValue());
		token = token.getNextToken();
		assertEquals("|", token.getStringValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("array", token.getStringValue());
		token = token.getNextToken();
		assertEquals("|", token.getStringValue());
		token = token.getNextToken();
		assertEquals("object", token.getStringValue());
		token = token.getNextToken();
		assertEquals(";", token.getStringValue());
		token = token.getNextToken();
		assertEquals("object", token.getStringValue());
		token = token.getNextToken();
		assertEquals("=", token.getStringValue());
		token = token.getNextToken();
		assertEquals("openCurly", token.getStringValue());
		token = token.getNextToken();
		assertEquals("objectContent", token.getStringValue());
		token = token.getNextToken();
		assertEquals("closeCurly", token.getStringValue());
		token = token.getNextToken();
		assertEquals(";", token.getStringValue());
	}
}

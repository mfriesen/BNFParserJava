package ca.gobits.bnfparser.tokenizer;

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
		assertEquals("", token.getValue());
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
		assertEquals("{", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getValue());
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
		assertEquals("{", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("asd", token.getValue());
		assertNull(token.getNextToken());
	}

	@Test
	public void testMultiLineComment() {
		// given
		String s = "{ }/*bleh\n\nffsdf\n*/asd";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("}", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("asd", token.getValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testQuotedString01() {
		// given
		String s = "hi \"asd\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("hi", token.getValue());
		token = token.getNextToken();
		assertEquals("\"asd\"", token.getValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString02() {
		// given
		String s = "\"asd\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd\"", token.getValue());
		assertTrue(token.isQuotedString());
	}

	@Test
	public void testQuotedString03() {
		// given
		String s = "\"asd's\"";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd's\"", token.getValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString04() {
		// given
		String s = "\"asd's";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("\"asd's", token.getValue());
		assertTrue(token.isQuotedString());
	}
	
	@Test
	public void testQuotedString05() {
		// given
		String s = "{ \"asd\":\"123\"}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("{", token.getValue());
		token = token.getNextToken();
		
		assertEquals("\"asd\"", token.getValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals(":", token.getValue());
		token = token.getNextToken();

		assertEquals("\"123\"", token.getValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals("}", token.getValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testQuotedString06() {
		// given
		String s = "'asd':'123'}";
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("'asd'", token.getValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals(":", token.getValue());
		token = token.getNextToken();

		assertEquals("'123'", token.getValue());
		assertTrue(token.isQuotedString());
		token = token.getNextToken();
		
		assertEquals("}", token.getValue());
		assertNull(token.getNextToken());
	}
	
	@Test
	public void testJsonGrammar() throws Exception {
		
		// given
		InputStream in = getClass().getResourceAsStream("/ca/gobits/bnf/parser/json.bnf");
		String s = IOUtils.toString(in, "UTF-8");
		
		// when
		BNFToken token = factory.tokens(s);
		
		// then
		assertEquals("@", token.getValue());
		token = token.getNextToken();
		assertEquals("start", token.getValue());
		assertTrue(token.isWord());
		token = token.getNextToken();
		assertEquals("=", token.getValue());
		token = token.getNextToken();
		assertEquals("Empty", token.getValue());
		token = token.getNextToken();
		assertEquals("|", token.getValue());
		assertTrue(token.isSymbol());
		token = token.getNextToken();
		assertEquals("array", token.getValue());
		token = token.getNextToken();
		assertEquals("|", token.getValue());
		token = token.getNextToken();
		assertEquals("object", token.getValue());
		token = token.getNextToken();
		assertEquals(";", token.getValue());
		token = token.getNextToken();
		assertEquals("object", token.getValue());
		token = token.getNextToken();
		assertEquals("=", token.getValue());
		token = token.getNextToken();
		assertEquals("openCurly", token.getValue());
		token = token.getNextToken();
		assertEquals("objectContent", token.getValue());
		token = token.getNextToken();
		assertEquals("closeCurly", token.getValue());
		token = token.getNextToken();
		assertEquals(";", token.getValue());
	}
}

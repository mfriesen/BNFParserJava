package ca.gobits.bnf.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.gobits.bnfparser.tokenizer.BNFToken;
import ca.gobits.bnfparser.tokenizer.BNFTokenizerFactory;
import ca.gobits.bnfparser.tokenizer.BNFTokenizerFactoryImpl;

public class BNFParserTest {

	private BNFTokenizerFactory tokenizerFactory = new BNFTokenizerFactoryImpl();
	private BNFStateDefinitionFactoryImpl sdf = new BNFStateDefinitionFactoryImpl();
	private Map<String, BNFStateDefinition> map;
	private BNFParser parser;
	
	@Before
	public void before() throws Exception {
		map = sdf.json();
		parser = new BNFParser(map);
	}
	
	@Test
	public void testOpenClose() throws Exception {
		
		// given
		String json = "{}";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParserResult result = parser.parser(token);
		
		// then
		assertNotNull(result.getTop());
		assertNull(result.getError());
		assertTrue(result.isSuccess());
	}
	
	@Test
	public void testEmpty() throws Exception {
		
		// given
		String json = "";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParserResult result = parser.parser(token);
		
		// then
		assertTrue(result.isSuccess());
		assertNotNull(result.getTop());
		assertNull(result.getError());
	}
	
	@Test
	public void testQuotedString() throws Exception {
		
		// given
		String json = "{ \"asd\":\"123\"}";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParserResult result = parser.parser(token);
		
		// then
		assertTrue(result.isSuccess());
		assertNotNull(result.getTop());
		assertNull(result.getError());
	}
}

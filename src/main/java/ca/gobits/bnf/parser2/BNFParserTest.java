package ca.gobits.bnf.parser2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.gobits.bnf.parser.BNFParseResult;
import ca.gobits.bnf.parser2.BNFSymbol.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactory;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactoryImpl;

public class BNFParserTest
{
    private BNFTokenizerFactory tokenizerFactory = new BNFTokenizerFactoryImpl();
    private Map<String, BNFSequences> map = new HashMap<String, BNFSequences>();
    private BNFParserImpl parser;
    
    @Before
    public void before() throws Exception 
    {
        map.put("@start",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("Empty"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("array"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("object"))))));

        map.put("object",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("openCurly"), new BNFSymbol("objectContent"), new BNFSymbol("closeCurly"))))));
        
        map.put("objectContent",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("Empty"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("actualObject"))))));

        map.put("actualObject",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("property"), new BNFSymbol("commaProperty", BNFRepetition.ZERO_OR_MORE))))));
        
        map.put("property",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("propertyName"), new BNFSymbol("colon"), new BNFSymbol("value"))))));

        map.put("commaProperty",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("comma"), new BNFSymbol("property"))))));

        map.put("propertyName",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("QuotedString"))))));

        map.put("array",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("openBracket"), new BNFSymbol("arrayContent"), new BNFSymbol("closeBracket"))))));

        map.put("arrayContent",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("Empty"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("actualArray"))))));
        
        map.put("actualArray",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("value"), new BNFSymbol("commaValue", BNFRepetition.ZERO_OR_MORE))))));

        map.put("commaValue",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("comma"), new BNFSymbol("value"))))));

        map.put("value",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("null"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("true"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("false"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("array"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("object"))),
                        new BNFSequence(Arrays.asList(new BNFSymbol("number"))),                        
                        new BNFSequence(Arrays.asList(new BNFSymbol("string"))))));

        map.put("string",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("QuotedString"))))));

        map.put("number",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("Number"))))));

        map.put("null",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'null'"))))));

        map.put("true",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'true'"))))));

        map.put("false",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'false'"))))));

        map.put("openCurly",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'{'"))))));

        map.put("closeCurly",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'}'"))))));
        
        map.put("openBracket",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("'['"))))));

        map.put("closeBracket",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("']'"))))));

        map.put("comma",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("','"))))));

        map.put("colon",
                new BNFSequences(Arrays.asList(
                        new BNFSequence(Arrays.asList(new BNFSymbol("':'"))))));
        
        parser = new BNFParserImpl(map);
    }
    
	// testOpenCloseBrace
	@Test
	public void testParse01() throws Exception {
		
		// given
		String json = "{}";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParseResult result = parser.parse(token);
		
		// then
		assertNotNull(result.getTop());
		assertNull(result.getError());
		assertTrue(result.isSuccess());
	}
	
	// testOpenCloseBracket
	@Test
	public void testParse02() throws Exception {
		
		// given
		String json = "[]";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParseResult result = parser.parse(token);
		
		// then
		assertTrue(result.isSuccess());
		assertNotNull(result.getTop());
		assertNull(result.getError());
	}
	
	// testEmpty
	@Test
	public void testParse03() throws Exception {
		
		// given
		String json = "";
		BNFToken token = tokenizerFactory.tokens(json);

		// when		
		BNFParseResult result = parser.parse(token);
		
		// then
		assertTrue(result.isSuccess());
		assertNotNull(result.getTop());
		assertNull(result.getError());
	}

    // good JSON
    @Test
    public void testParse14() throws Exception 
    {
        // given
        String json = "{\"A\":null}";
        BNFToken token = tokenizerFactory.tokens(json);

        // when       
        BNFParseResult result = parser.parse(token);
       
        // then
        assertNotNull(result.getTop());
        assertNull(result.getError());
        assertTrue(result.isSuccess());
    }
    
    // bad JSON
    @Test
    public void testParse15() throws Exception 
    {
        // given
        String json = "{\"A\":\"B\",\"C\":}";
        BNFToken token = tokenizerFactory.tokens(json);

        // when       
        BNFParseResult result = parser.parse(token);
       
        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getTop());
        assertNotNull(result.getError());
        assertEquals(":", result.getError().getStringValue());
    }

}

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

package ca.gobits.bnf.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.gobits.bnf.tokenizer.BNFToken;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactory;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactoryImpl;

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

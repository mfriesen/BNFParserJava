//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.bnf.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFStateEnd;
import ca.gobits.bnf.parser.states.BNFStateQuotedString;


public class BNFStateDefinitionFactoryTest {

	@Test
	public void testStartJson() throws Exception {
		BNFStateDefinitionFactory f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition start = map.get("@start");
		assertNotNull(start);
		assertEquals(3, start.getStates().size());
		assertEquals("array", start.getStates().get(0).getName());
		assertEquals("object", start.getStates().get(1).getName());
		assertEquals("Empty", start.getStates().get(2).getName());
		for (BNFState ss : start.getStates()) {
			assertEquals("@end", ss.getNextState().getName());
			assertEquals(BNFStateEnd.class, ss.getNextState().getClass());
		}
	}

	@Test
	public void testOpenCurly() throws Exception {
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition openCurly = map.get("openCurly");
		assertNotNull(openCurly);
		assertEquals(1, openCurly.getStates().size());
		Iterator<BNFState> itr = openCurly.getStates().iterator();
		BNFState ss = itr.next();
		assertEquals("{", ss.getName());
		assertTrue(ss.isTerminal());
	}
	
	@Test
	public void testObject() throws Exception {
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition object = map.get("object");
		assertNotNull(object);
		assertEquals(1, object.getStates().size());
		Iterator<BNFState> itr = object.getStates().iterator();
		BNFState ss = itr.next();
		assertEquals("openCurly", ss.getName());
		ss = ss.getNextState();
		assertEquals("objectContent", ss.getName());
		ss = ss.getNextState();
		assertEquals("closeCurly", ss.getName());
	}
	
	@Test
	public void testQuotedString() throws Exception {
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition string = map.get("string");
		assertNotNull(string);
		assertEquals(1, string.getStates().size());
		BNFState ss = string.getStates().iterator().next();
		assertEquals(BNFStateQuotedString.class, ss.getClass());
	}
	
	public void testKeys() {
		
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		assertTrue(map.containsKey("@start"));
		
		assertTrue(map.containsKey("object"));
		assertTrue(map.containsKey("objectContent"));
		assertTrue(map.containsKey("actualObject"));
		assertTrue(map.containsKey("property"));
		assertTrue(map.containsKey("commaProperty"));
		assertTrue(map.containsKey("propertyName"));
		assertTrue(map.containsKey("arrayContent"));
		assertTrue(map.containsKey("actualArray"));
		assertTrue(map.containsKey("commaValue"));
		assertTrue(map.containsKey("value"));
		assertTrue(map.containsKey("string"));
		assertTrue(map.containsKey("number"));
		assertTrue(map.containsKey("null"));
		assertTrue(map.containsKey("true"));
		assertTrue(map.containsKey("false"));

		assertTrue(map.containsKey("openCurly"));
		assertTrue(map.containsKey("closeCurly"));
		assertTrue(map.containsKey("openBracket"));
		assertTrue(map.containsKey("closeBracket"));
		assertTrue(map.containsKey("comma"));
		assertTrue(map.containsKey("colon"));
	}
}

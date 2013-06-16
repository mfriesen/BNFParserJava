package ca.gobits.bnf.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;


public class BNFStateDefinitionFactoryTest {

	@Test
	public void startJson() throws Exception {
		BNFStateDefinitionFactory f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition start = map.get("@start");
		assertNotNull(start);
		assertEquals(3, start.getStates().size());
		for (BNFState ss : start.getStates()) {
			assertEquals("@end", ss.getNextState().getName());
			assertEquals(BNFStateEnd.class, ss.getNextState().getClass());
		}
	}

	@Test
	public void openCurly() throws Exception {
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition openCurly = map.get("openCurly");
		assertNotNull(openCurly);
		assertEquals(1, openCurly.getStates().size());
		Iterator<BNFState> itr = openCurly.getStates().iterator();
		BNFState ss = itr.next();
		assertEquals("{", ss.getName());
	}
	
	@Test
	public void quotedString() throws Exception {
		BNFStateDefinitionFactoryImpl f = new BNFStateDefinitionFactoryImpl();
		Map<String, BNFStateDefinition> map = f.json();
		
		BNFStateDefinition string = map.get("string");
		assertNotNull(string);
		assertEquals(1, string.getStates().size());
		BNFState ss = string.getStates().iterator().next();
		assertEquals(BNFStateQuotedString.class, ss.getClass());
	}
}

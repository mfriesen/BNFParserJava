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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.gobits.bnf.parser.BNFSymbol.BNFRepetition;

public class BNFSequenceFactoryTest {

	private BNFSequenceFactory factory = new BNFSequenceFactoryImpl();
	
	@Test
	public void testJson01() {
		
		// given		
		// when
		Map<String, BNFSequences> result = factory.json();
		
		// then
		assertEquals(23, result.size());
		
		verifyAtStart(result.get("@start"));
		
		verifyObject(result.get("object"));
		
		verifyActualObject(result.get("actualObject"));
		
		verifyColon(result.get("colon"));
	}

	private void verifyAtStart(BNFSequences s) {
		
		assertEquals(3, s.getSequences().size());
		
		assertEquals(1, getSymbols(s, 0).size());		
		assertEquals("array", getSymbolsName(s, 0, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));
		
		assertEquals(1, getSymbols(s, 1).size());
		assertEquals("object", getSymbolsName(s, 1, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 1, 0));

		assertEquals(1, getSymbols(s, 2).size());
		assertEquals("Empty", getSymbolsName(s, 2, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 2, 0));
	}
	
	private void verifyObject(BNFSequences s) {
		assertEquals(1, s.getSequences().size());
		assertEquals(3, getSymbols(s, 0).size());
		
		assertEquals("openCurly", getSymbolsName(s, 0, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));

		assertEquals("objectContent", getSymbolsName(s, 0, 1));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 1));

		assertEquals("closeCurly", getSymbolsName(s, 0, 2));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 2));
	}

	private void verifyActualObject(BNFSequences s) {
		assertEquals(1, s.getSequences().size());
		assertEquals(2, getSymbols(s, 0).size());

		assertEquals("property", getSymbolsName(s, 0, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));

		assertEquals("commaProperty", getSymbolsName(s, 0, 1));
		assertEquals(BNFRepetition.ZERO_OR_MORE, getSymbolsRepetition(s, 0, 1));
	}

	private void verifyColon(BNFSequences s) {
		
		assertEquals(1, s.getSequences().size());
		assertEquals(1, getSymbols(s, 0).size());

		assertEquals("':'", getSymbolsName(s, 0, 0));
		assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));		
	}
	
	private String getSymbolsName(BNFSequences s, int position, int index) {
		List<BNFSymbol> symbols = getSymbols(s, position);
		return symbols.get(index).getName();
	}

	private BNFRepetition getSymbolsRepetition(BNFSequences s, int position, int index) {
		List<BNFSymbol> symbols = getSymbols(s, position);
		return symbols.get(index).getRepetition();
	}
	
	private List<BNFSymbol> getSymbols(BNFSequences s, int position) {
		return s.getSequences().get(position).getSymbols();
	}
}

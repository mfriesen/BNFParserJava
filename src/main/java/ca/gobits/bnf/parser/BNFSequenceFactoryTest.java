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


/**
 * BNFSequenceFactoryTest Unit Tests.
 */
public class BNFSequenceFactoryTest {

    /** THREE. */
    private static final int THREE = 3;

    /** NUMBER_OF_JSON_SEQUENCES. */
    private static final int NUMBER_OF_JSON_SEQUENCES = 23;

    /** instance of BNFSequenceFactory. */
    private final BNFSequenceFactory factory = new BNFSequenceFactoryImpl();

    /**
     * Test creating JSON Sequence Factory.
     */
    @Test
    public void testJson01() {

        // given
        // when
        Map<String, List<BNFSequence>> result = factory.json();

        // then
        assertEquals(NUMBER_OF_JSON_SEQUENCES, result.size());

        verifyAtStart(result.get("@start"));

        verifyObject(result.get("object"));

        verifyActualObject(result.get("actualObject"));

        verifyColon(result.get("colon"));
    }

    /**
     * @param s -
     */
    private void verifyAtStart(final List<BNFSequence> s) {

        assertEquals(THREE, s.size());

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

    /**
     * @param s -
     */
    private void verifyObject(final List<BNFSequence> s) {

        assertEquals(1, s.size());
        assertEquals(THREE, getSymbols(s, 0).size());

        assertEquals("openCurly", getSymbolsName(s, 0, 0));
        assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));

        assertEquals("objectContent", getSymbolsName(s, 0, 1));
        assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 1));

        assertEquals("closeCurly", getSymbolsName(s, 0, 2));
        assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 2));
    }

    /**
     * @param s -
     */
    private void verifyActualObject(final List<BNFSequence> s) {
        assertEquals(1, s.size());
        assertEquals(2, getSymbols(s, 0).size());

        assertEquals("property", getSymbolsName(s, 0, 0));
        assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));

        assertEquals("commaProperty", getSymbolsName(s, 0, 1));
        assertEquals(BNFRepetition.ZERO_OR_MORE, getSymbolsRepetition(s, 0, 1));
    }

    /**
     * @param s -
     */
    private void verifyColon(final List<BNFSequence> s) {

        assertEquals(1, s.size());
        assertEquals(1, getSymbols(s, 0).size());

        assertEquals("':'", getSymbolsName(s, 0, 0));
        assertEquals(BNFRepetition.NONE, getSymbolsRepetition(s, 0, 0));
    }

    /**
     * @param s -
     * @param position -
     * @param index -
     * @return String
     */
    private String getSymbolsName(final List<BNFSequence> s, final int position, final int index) {
        List<BNFSymbol> symbols = getSymbols(s, position);
        return symbols.get(index).getName();
    }

    /**
     * @param s -
     * @param position -
     * @param index -
     * @return BNFRepetition
     */
    private BNFRepetition getSymbolsRepetition(final List<BNFSequence> s, final int position, final int index) {
        List<BNFSymbol> symbols = getSymbols(s, position);
        return symbols.get(index).getRepetition();
    }

    /**
     * @param s -
     * @param position -
     * @return List<BNFSymbol>
     */
    private List<BNFSymbol> getSymbols(final List<BNFSequence> s, final int position) {
        return s.get(position).getSymbols();
    }
}

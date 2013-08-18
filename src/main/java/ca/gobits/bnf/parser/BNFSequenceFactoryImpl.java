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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gobits.bnf.parser.BNFSymbol.BNFRepetition;

/**
 * BNF Sequence Factory implementation.
 *
 */
public class BNFSequenceFactoryImpl implements BNFSequenceFactory {

    @Override
    public Map<String, BNFSequences> json() {

        Map<String, String> prop = getGrammarJSON();

        return buildMap(prop);
    }

    /**
     * @param prop - grammar property map
     * @return Map<String, BNFSequences>
     */
    private Map<String, BNFSequences> buildMap(final Map<String, String> prop) {

        Map<String, BNFSequences> result = new HashMap<String, BNFSequences>();

        for (Map.Entry<String, String> e : prop.entrySet()) {

            String name = e.getKey().toString();

            String value = e.getValue().toString();

            List<String> sequenceNames = createSequenceNames(value);

            BNFSequences sequences = createBNFSequences(sequenceNames);
            result.put(name, sequences);
        }

        return result;
    }

    /**
     * @param sequenceNames -
     * @return BNFSequences
     */
    private BNFSequences createBNFSequences(final List<String> sequenceNames) {
        List<BNFSequence> list = createBNFSequenceList(sequenceNames);
        return new BNFSequences(list);
    }

    /**
     * @param sequenceNames -
     * @return List<BNFSequence>
     */
    private List<BNFSequence> createBNFSequenceList(final List<String> sequenceNames) {

        List<BNFSequence> list = new ArrayList<BNFSequence>(
                sequenceNames.size());

        for (String s : sequenceNames) {
            BNFSequence sequence = createSequence(s);
            list.add(sequence);
        }

        return list;
    }

    /**
     * @param s -
     * @return BNFSequence
     */
    private BNFSequence createSequence(final String s) {

        List<BNFSymbol> symbols = createSymbols(s);
        return new BNFSequence(symbols);
    }

    /**
     * @param s -
     * @return List<BNFSymbol>
     */
    private List<BNFSymbol> createSymbols(final String s) {

        String[] split = s.trim().split(" ");

        List<BNFSymbol> symbols = new ArrayList<BNFSymbol>(split.length);

        for (String ss : split) {
            BNFSymbol symbol = createSymbol(ss);
            symbols.add(symbol);
        }

        return symbols;
    }

    /**
     * @param s -
     * @return BNFSymbol
     */
    private BNFSymbol createSymbol(final String s) {

        String ss = s;
        BNFRepetition repetition = BNFRepetition.NONE;

        if (ss.endsWith("*")) {
            ss = ss.substring(0, ss.length() - 1);
            repetition = BNFRepetition.ZERO_OR_MORE;
        }

        return new BNFSymbol(ss, repetition);
    }

    /**
     * @param value -
     * @return List<String>
     */
    private List<String> createSequenceNames(final String value) {

        String[] values = value.split("[|]");
        List<String> list = new ArrayList<>(values.length);

        for (String s : values) {

            if (s.endsWith(";")) {
                s = s.substring(0, s.length() - 1);
            }

            list.add(s.trim());
        }

        sortSequenceNames(list);

        return list;
    }

    /**
     * @param list -
     */
    private void sortSequenceNames(final List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                if (o1.equals("Empty")) {
                    return 1;
                } else if (o2.equals("Empty")) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * Load JSON grammar.
     * @return Map<String, String>
     */
    private Map<String, String> getGrammarJSON() {
        PropertyParser parser = new PropertyParser();
        InputStream is = getClass().getResourceAsStream("/json.bnf");
        try {
            return parser.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.gobits.bnf.tokenizer.BNFToken;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactory;
import ca.gobits.bnf.tokenizer.BNFTokenizerFactoryImpl;

/**
 * BNFParser Unit Tests.
 */
public class BNFParserTest {

    /** BNFSequenceFactory instance. */
    private BNFSequenceFactory sequenceFactory;

    /** BNFTokenizerFactory instance. */
    private BNFTokenizerFactory tokenizerFactory;

    /** Map of BNFSequences instance. */
    private Map<String, List<BNFSequence>> map;

    /** BNFParser instance. */
    private BNFParser parser;

    /**
     * Tests Before.
     *
     * @throws Exception - any Exception
     */
    @Before
    public void before() throws Exception {

        this.sequenceFactory = new BNFSequenceFactoryImpl();
        this.tokenizerFactory = new BNFTokenizerFactoryImpl();
        this.map = this.sequenceFactory.json();
        this.parser = new BNFParserImpl(this.map);
    }

    /**
     * testOpenCloseBrace.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse01() throws Exception {

        // given
        String json = "{}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertNotNull(result.getTop());
        assertNull(result.getError());
        assertTrue(result.isSuccess());
    }

    /**
     * testOpenCloseBracket.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse02() throws Exception {

        // given
        String json = "[]";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testEmpty.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse03() throws Exception {

        // given
        String json = "";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testQuotedString.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse04() throws Exception {

        // given
        String json = "{ \"asd\":\"123\"}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testNumber.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse05() throws Exception {

        // given
        String json = "{ \"asd\":123}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testSimple01.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse06() throws Exception {

        // given
        String json = "{\"id\": \"118019484951173_228591\",\"message\": \"test test\"}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testSimple02.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse07() throws Exception {

        // given
        String json = "{\"id\": \"118019484951173_228591\",\"message\": \"test test\",\"created_time\": \"2011-06-19T09:14:16+0000\"}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testNested.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse08() throws Exception {
        // given
        String json = "{\"card\":\"2\",\"numbers\":{\"Conway\":[1,11,21,1211,111221,312211],\"Fibonacci\":[0,1,1,2,3,5,8,13,21,34]}}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testArray.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse09() throws Exception {
        // given
        String json = "[1,11,21,1211,111221,312211]";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertTrue(result.isSuccess());
        assertNotNull(result.getTop());
        assertNull(result.getError());
    }

    /**
     * testBadSimple00.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse10() throws Exception {
        // given
        String json = "asdasd";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getTop());
        assertEquals(result.getError(), result.getTop());
        assertEquals(json, result.getError().getStringValue());
    }

    /**
     * testBadSimple01.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse11() throws Exception {
        // given
        String json = "{ asdasd";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getTop());
        assertNotNull(result.getError());
        assertEquals(2, result.getError().getId());
        assertEquals("asdasd", result.getError().getStringValue());
    }

    /**
     * testBadSimple02.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse12() throws Exception {

        // given
        String json = "{\"id\": \"118019484951173_228591\",\"message\": \"test test\",\"created_time\"! \"2011-06-19T09:14:16+0000\"}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getTop());
        assertNotNull(result.getError());
        assertEquals("!", result.getError().getStringValue());
    }

    /**
     * testBadSimple03.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse13() throws Exception {

        // given
        String json = "[";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertNotNull(result.getTop());
        assertNotNull(result.getError());
        assertFalse(result.isSuccess());
        assertEquals("[", result.getError().getStringValue());
    }

    /**
     * good JSON.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse14() throws Exception {
        // given
        String json = "{\"A\":null}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertNotNull(result.getTop());
        assertNull(result.getError());
        assertTrue(result.isSuccess());
    }

    /**
     * bad JSON.
     *
     * @throws Exception - any Exception
     */
    @Test
    public void testParse15() throws Exception {
        // given
        String json = "{\"A\":\"B\",\"C\":}";
        BNFToken token = this.tokenizerFactory.tokens(json);

        // when
        BNFParseResult result = this.parser.parse(token);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getTop());
        assertNotNull(result.getError());
        assertEquals("}", result.getError().getStringValue());
    }
}
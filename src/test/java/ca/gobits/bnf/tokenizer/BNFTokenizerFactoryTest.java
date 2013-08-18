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

package ca.gobits.bnf.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
/**
 * BNFTokenizerFactory Unit Test.
 */
public class BNFTokenizerFactoryTest {

    /** instance of BNFTokenizerFactory. */
    private final BNFTokenizerFactory factory = new BNFTokenizerFactoryImpl();

    /**
     * testEmpty.
     */
    @Test
    public void testTokens01() {
        // given
        String s = "";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("", token.getStringValue());
        assertFalse(token.isWord());
        assertFalse(token.isNumber());
        assertFalse(token.isSymbol());
        assertNull(token.getNextToken());
    }

    /**
     * testSymbolAndWhiteSpace.
     */
    @Test
    public void testTokens02() {
        // given
        String s = "{ \n}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        assertEquals(1, token.getId());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("}", token.getStringValue());
        assertEquals(2, token.getId());
        assertTrue(token.isSymbol());
        assertNull(token.getNextToken());
    }

    /**
     * testSingleLineComment.
     */
    @Test
    public void testTokens03() {
        // given
        String s = "{ }//bleh\nasd";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("}", token.getStringValue());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("asd", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testMultiLineComment.
     */
    @Test
    public void testTokens04() {
        // given
        String s = "{ }/*bleh\n\nffsdf\n*/asd";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("}", token.getStringValue());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("asd", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testQuotedString01.
     */
    @Test
    public void testTokens05() {
        // given
        String s = "hi \"asd\"";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("hi", token.getStringValue());
        token = token.getNextToken();
        assertEquals("\"asd\"", token.getStringValue());
        assertTrue(token.isQuotedString());
    }

    /**
     * testQuotedString02.
     */
    @Test
    public void testTokens06() {
        // given
        String s = "\"asd\"";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("\"asd\"", token.getStringValue());
        assertTrue(token.isQuotedString());
    }

    /**
     * testQuotedString03.
     */
    @Test
    public void testTokens07() {
        // given
        String s = "\"asd's\"";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("\"asd's\"", token.getStringValue());
        assertTrue(token.isQuotedString());
    }

    /**
     * testQuotedString04.
     */
    @Test
    public void testTokens08() {
        // given
        String s = "\"asd's";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("\"asd's", token.getStringValue());
        assertTrue(token.isQuotedString());
    }

    /**
     * testQuotedString05.
     */
    @Test
    public void testTokens09() {
        // given
        String s = "{ \"asd\":\"123\"}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        token = token.getNextToken();

        assertEquals("\"asd\"", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals(":", token.getStringValue());
        token = token.getNextToken();

        assertEquals("\"123\"", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals("}", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testQuotedString06.
     */
    @Test
    public void testTokens10() {
        // given
        String s = "'asd':'123'}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("'asd'", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals(":", token.getStringValue());
        token = token.getNextToken();

        assertEquals("'123'", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals("}", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testQuotedNumber01.
     */
    @Test
    public void testTokens11() {
        // given
        String s = "'asd':123}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("'asd'", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals(":", token.getStringValue());
        token = token.getNextToken();

        assertEquals("123", token.getStringValue());
        assertTrue(token.isNumber());
        token = token.getNextToken();

        assertEquals("}", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testAHrefLink.
     */
    @Test
    public void testTokens12() {

        // given
        String s = "{\"notes\":\"Different browsers have support for different video formats, see sub-features for details. \\r\\n\\r\\nThe Android browser (before 2.3) requires <a href=\\\"http://www.broken-links.com/2010/07/08/making-html5-video-work-on-android-phones/\\\">specific handling</a> to run the video element.\"}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        token = token.getNextToken();

        assertEquals("\"notes\"", token.getStringValue());
        token = token.getNextToken();

        assertEquals(":", token.getStringValue());
        token = token.getNextToken();

        assertEquals(
                "\"Different browsers have support for different video formats, see sub-features for details. \\r\\n\\r\\nThe Android browser (before 2.3) requires <a href=\\\"http://www.broken-links.com/2010/07/08/making-html5-video-work-on-android-phones/\\\">specific handling</a> to run the video element.\"",
                token.getStringValue());
        token = token.getNextToken();

        assertEquals("}", token.getStringValue());
        token = token.getNextToken();

        assertNull(token);
    }

    /**
     * testJsonGrammar.
     * @throws Exception -
     */
    @Test
    public void testTokens13() throws Exception {

        // given
        InputStream in = getClass().getResourceAsStream("/json.bnf");
        String s = IOUtils.toString(in, "UTF-8");

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("@", token.getStringValue());
        token = token.getNextToken();
        assertEquals("start", token.getStringValue());
        assertTrue(token.isWord());
        token = token.getNextToken();
        assertEquals("=", token.getStringValue());
        token = token.getNextToken();
        assertEquals("Empty", token.getStringValue());
        token = token.getNextToken();
        assertEquals("|", token.getStringValue());
        assertTrue(token.isSymbol());
        token = token.getNextToken();
        assertEquals("array", token.getStringValue());
        token = token.getNextToken();
        assertEquals("|", token.getStringValue());
        token = token.getNextToken();
        assertEquals("object", token.getStringValue());
        token = token.getNextToken();
        assertEquals(";", token.getStringValue());
        token = token.getNextToken();
        assertEquals("object", token.getStringValue());
        token = token.getNextToken();
        assertEquals("=", token.getStringValue());
        token = token.getNextToken();
        assertEquals("openCurly", token.getStringValue());
        token = token.getNextToken();
        assertEquals("objectContent", token.getStringValue());
        token = token.getNextToken();
        assertEquals("closeCurly", token.getStringValue());
        token = token.getNextToken();
        assertEquals(";", token.getStringValue());
    }

    /**
     * testRussianCharacters.
     */
    @Test
    public void testTokens14() {

        // given
        String s = "{\"text\":\"Й\"}";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("{", token.getStringValue());
        token = token.getNextToken();

        assertEquals("\"text\"", token.getStringValue());
        token = token.getNextToken();

        assertEquals(":", token.getStringValue());
        token = token.getNextToken();

        assertEquals("\"Й\"", token.getStringValue());
        assertTrue(token.isQuotedString());
        token = token.getNextToken();

        assertEquals("}", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testUnicodeCharacter.
     */
    @Test
    public void testTokens15() {

        // given
        String s = "\u042d\u0442\u043e\u0440\u0443\u0441\u0441\u043a\u0438\u0439\u0442\u0435\u043a\u0441\u0442";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("Эторусскийтекст", token.getStringValue());
        assertNull(token.getNextToken());
    }

    /**
     * testUnicodeCharacter.
     */
    @Test
    public void testTokens16() {

        // given
        String s = "\u042d\u0442\u043e\u0440\u0443\u0441\u0441\u043a\u0438\u0439\u0442\u0435\u043a\u0441\u0442";

        // when
        BNFToken token = factory.tokens(s);

        // then
        assertEquals("Эторусскийтекст", token.getStringValue());
        assertNull(token.getNextToken());
    }
}
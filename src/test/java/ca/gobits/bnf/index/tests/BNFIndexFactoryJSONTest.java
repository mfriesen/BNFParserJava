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

package ca.gobits.bnf.index.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.gobits.bnf.index.BNFIndex;
import ca.gobits.bnf.index.BNFIndexFactory;
import ca.gobits.bnf.index.BNFIndexFactoryJSON;
import ca.gobits.bnf.index.BNFIndexNode;
import ca.gobits.bnf.index.BNFIndexPath;
import ca.gobits.bnf.parser.BNFParseResult;
import ca.gobits.bnf.parser.BNFParser;
import ca.gobits.bnf.parser.BNFParserFactory;
import ca.gobits.bnf.parser.BNFParserFactoryImpl;

/**
 * BNFIndexBuilder Unit Tests.
 *
 */
public class BNFIndexFactoryJSONTest {

    /** instance of BNFParser. */
    private BNFParser jsonParser;

    /** instance of BNFIndexBuilder. */
    private BNFIndexFactory indexBuilder;

    /**
     * Setup Tests.
     */
    @Before
    public void setup() {

        BNFParserFactory parserFactory = new BNFParserFactoryImpl();
        this.jsonParser = parserFactory.json();
        this.indexBuilder = new BNFIndexFactoryJSON();
    }

    /**
     * testCreateIndex01.
     */
    @Test
    public void testCreateIndex01() {

        // given
        String s = "[]";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        List<BNFIndexNode> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        BNFIndexNode node0 = nodes.get(0);
        assertEquals("[", node0.getKeyValue());
        assertNull(node0.getStringValue());
        assertEquals(0, node0.getNodes().size());

        BNFIndexNode node1 = nodes.get(1);
        assertEquals("]", node1.getKeyValue());
        assertNull(node1.getStringValue());
        assertEquals(0, node1.getNodes().size());
    }

    /**
     * testCreateIndex02.
     */
    @Test
    public void testCreateIndex02() {

        // given
        String s = "{ \"food\" : \"chicken\" }";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        List<BNFIndexNode> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        BNFIndexNode nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getKeyValue());
        assertTrue(nodeA0.isShouldSkip());
        assertNull(nodeA0.getStringValue());
        assertEquals(1, nodeA0.getNodes().size());

        BNFIndexNode nodeB0 = nodeA0.getNodes().get(0);
        assertEquals("\"food\"", nodeB0.getKeyValue());
        assertEquals("\"chicken\"", nodeB0.getStringValue());
        assertEquals(0, nodeB0.getNodes().size());

        BNFIndexNode nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getKeyValue());
        assertNull(nodeA1.getStringValue());
        assertEquals(0, nodeA1.getNodes().size());
    }

    /**
     * testCreateIndex03.
     */
    @Test
    public void testCreateIndex03() {

        // given
        String s = "{\"firstName\" : \"John\",\"address\" : {\"postalCode\" : 10021}}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        List<BNFIndexNode> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        BNFIndexNode nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getKeyValue());
        assertNull(nodeA0.getStringValue());
        assertEquals(3, nodeA0.getNodes().size());

        BNFIndexNode nodeB0 = nodeA0.getNodes().get(0);
        assertEquals("\"firstName\"", nodeB0.getKeyValue());
        assertEquals("\"John\"", nodeB0.getStringValue());
        assertEquals(0, nodeB0.getNodes().size());

        BNFIndexNode nodeB1 = nodeA0.getNodes().get(1);
        assertEquals("\"address\"", nodeB1.getKeyValue());
        assertEquals("{", nodeB1.getStringValue());
        assertEquals(1, nodeB1.getNodes().size());

        BNFIndexNode nodeC0 = nodeB1.getNodes().get(0);
        assertEquals("\"postalCode\"", nodeC0.getKeyValue());
        assertEquals("10021", nodeC0.getStringValue());
        assertEquals(0, nodeC0.getNodes().size());

        BNFIndexNode nodeB2 = nodeA0.getNodes().get(2);
        assertEquals("}", nodeB2.getKeyValue());
        assertNull(nodeB2.getStringValue());
        assertEquals(0, nodeB2.getNodes().size());

        BNFIndexNode nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getKeyValue());
        assertNull(nodeA1.getStringValue());
        assertEquals(0, nodeA1.getNodes().size());
    }

    /**
     * testCreateIndex04.
     */
    @Test
    public void testCreateIndex04() {

        // given
        String s = "{\"phoneNumbers\": [{\"type\": \"home\",\"number\": \"212 555-1234\"},{\"type\": \"fax\",\"number\": \"646 555-4567\"}]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        List<BNFIndexNode> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        BNFIndexNode nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getKeyValue());
        assertNull(nodeA0.getStringValue());
        assertEquals(2, nodeA0.getNodes().size());

        BNFIndexNode nodeB0 = nodeA0.getNodes().get(0);
        assertEquals("\"phoneNumbers\"", nodeB0.getKeyValue());
        assertEquals("[", nodeB0.getStringValue());
        assertEquals(4, nodeB0.getNodes().size());

        BNFIndexNode nodeC0 = nodeB0.getNodes().get(0);
        assertEquals("{", nodeC0.getKeyValue());
        assertNull(nodeC0.getStringValue());
        assertEquals(2, nodeC0.getNodes().size());

        BNFIndexNode nodeD0 = nodeC0.getNodes().get(0);
        assertEquals("\"type\"", nodeD0.getKeyValue());
        assertEquals("\"home\"", nodeD0.getStringValue());
        assertEquals(0, nodeD0.getNodes().size());

        BNFIndexNode nodeD1 = nodeC0.getNodes().get(1);
        assertEquals("\"number\"", nodeD1.getKeyValue());
        assertEquals("\"212 555-1234\"", nodeD1.getStringValue());
        assertEquals(0, nodeD1.getNodes().size());

        BNFIndexNode nodeC1 = nodeB0.getNodes().get(1);
        assertEquals("}", nodeC1.getKeyValue());
        assertNull(nodeC1.getStringValue());
        assertEquals(0, nodeC1.getNodes().size());

        BNFIndexNode nodeC2 = nodeB0.getNodes().get(2);
        assertEquals("{", nodeC2.getKeyValue());
        assertNull(nodeC2.getStringValue());
        assertEquals(2, nodeC2.getNodes().size());

        nodeD0 = nodeC2.getNodes().get(0);
        assertEquals("\"type\"", nodeD0.getKeyValue());
        assertEquals("\"fax\"", nodeD0.getStringValue());
        assertEquals(0, nodeD0.getNodes().size());

        nodeD1 = nodeC2.getNodes().get(1);
        assertEquals("\"number\"", nodeD1.getKeyValue());
        assertEquals("\"646 555-4567\"", nodeD1.getStringValue());
        assertEquals(0, nodeD1.getNodes().size());

        BNFIndexNode nodeC3 = nodeB0.getNodes().get(3);
        assertEquals("}", nodeC3.getKeyValue());
        assertNull(nodeC3.getStringValue());
        assertEquals(0, nodeC3.getNodes().size());

        BNFIndexNode nodeB1 = nodeA0.getNodes().get(1);
        assertEquals("]", nodeB1.getKeyValue());
        assertNull(nodeB1.getStringValue());
        assertEquals(0, nodeB1.getNodes().size());

        BNFIndexNode nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getKeyValue());
        assertNull(nodeA1.getStringValue());
        assertEquals(0, nodeA1.getNodes().size());
    }

    /**
     * testCreateIndex05.
     */
    @Test
    public void testCreateIndex05() {

        // given
        String s = "{\"list\": [ \"A\", \"B\" ]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        List<BNFIndexNode> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        BNFIndexNode nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getKeyValue());
        assertNull(nodeA0.getStringValue());
        assertEquals(2, nodeA0.getNodes().size());

        BNFIndexNode nodeB0 = nodeA0.getNodes().get(0);
        assertEquals("\"list\"", nodeB0.getKeyValue());
        assertEquals("[", nodeB0.getStringValue());
        assertEquals(2, nodeB0.getNodes().size());

        BNFIndexNode nodeC0 = nodeB0.getNodes().get(0);
        assertNull(nodeC0.getKeyValue());
        assertEquals("\"A\"", nodeC0.getStringValue());
        assertEquals(0, nodeC0.getNodes().size());

        BNFIndexNode nodeC1 = nodeB0.getNodes().get(1);
        assertNull(nodeC1.getKeyValue());
        assertEquals("\"B\"", nodeC1.getStringValue());
        assertEquals(0, nodeC1.getNodes().size());

        BNFIndexNode nodeB1 = nodeA0.getNodes().get(1);
        assertEquals("]", nodeB1.getKeyValue());
        assertNull(nodeB1.getStringValue());
        assertEquals(0, nodeB1.getNodes().size());

        BNFIndexNode nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getKeyValue());
        assertNull(nodeA1.getStringValue());
        assertEquals(0, nodeA1.getNodes().size());
    }

    /**
     * testCreateIndex06 ParseResult is not Successful.
     */
    @Test(expected = RuntimeException.class)
    public void testCreateIndex06() {

        // given
        String s = "{\"list\": \"A\", \"B\" ]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);
        assertFalse(parseResult.isSuccess());

        // when
        this.indexBuilder.createIndex(parseResult);
    }

    /**
     * testFindIndex01.
     */
    @Test
    public void testFindIndex01() {

        // given
        String s = "{\"firstName\":\"John\",\"address\" : {\"streetAddress\" : \"21 2nd Street\"},\"phoneNumbers\":[{\"type\" :\"home\",\"number\":\"212 555-1234\"}]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex resultIndex = this.indexBuilder.createIndex(parseResult);

        // then
        BNFIndexPath result1 = resultIndex.getPath("\"address\"");
        assertNotNull(result1);

        BNFIndexPath result2 = resultIndex.getPath("address\"");
        assertNull(result2);

        BNFIndexPath result3 = resultIndex.getPath("\"phoneNumbers\"");
        assertNotNull(result3);

        BNFIndexPath result4 = result3.getPath("\"type\"");
        assertNotNull(result4);
    }

    /**
     * testFindIndex02.
     */
    @Test
    public void testFindIndex02() {

        // given
        String s = "{\"firstName\":\"John\",\"address\" : {\"streetAddress\" : \"21 2nd Street\"},\"phoneNumbers\":[{\"type\" :\"home\",\"number\":\"212 555-1234\"}]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex resultIndex = this.indexBuilder.createIndex(parseResult);

        // then
        BNFIndexPath result = resultIndex.getPath("\"phoneNumbers\"").getPath("\"number\"");
        BNFIndexPath resultNode = result.getNode();
        assertNotNull(resultNode);
        assertTrue(resultNode.eq("\"212 555-1234\""));
    }

    /**
     * testFindIndex03.
     */
    @Test
    public void testFindIndex03() {

        // given
        String s = "{\"firstName\":\"John\",\"address\" : {\"streetAddress\" : \"21 2nd Street\"},\"phoneNumbers\":[{\"type\" :\"home\",\"number\":\"212 555-1234\"}]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);

        // when
        BNFIndex resultIndex = this.indexBuilder.createIndex(parseResult);

        // then
        BNFIndexPath result = resultIndex.getPath("\"phoneNumbers\"");
        BNFIndexPath resultNode = result.getNode();
        assertNotNull(resultNode);
        assertTrue(resultNode.eq("["));
    }
}
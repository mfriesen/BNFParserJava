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
        List<? extends BNFIndexPath> nodes = result.getPaths();
        assertEquals(2, nodes.size());

        BNFIndexPath node0 = nodes.get(0);
        assertEquals("[", node0.getPathName());
        //        assertNull(node0.getStringValue());
        assertEquals(0, node0.getPaths().size());

        BNFIndexPath node1 = nodes.get(1);
        assertEquals("]", node1.getPathName());
        //        assertNull(node1.getStringValue());
        assertEquals(0, node1.getPaths().size());
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
        List<? extends BNFIndexPath> nodes = result.getPaths();
        assertEquals(2, nodes.size());

        BNFIndexPath nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getPathName());
        //        assertTrue(nodeA0.eq(null));
        assertEquals(1, nodeA0.getPaths().size());
        assertNotNull(nodeA0.getPath("\"food\""));

        BNFIndexPath nodeB0 = nodeA0.getPaths().get(0);
        assertEquals("\"food\"", nodeB0.getPathName());
        assertTrue(nodeB0.eq("\"chicken\""));
        assertEquals(0, nodeB0.getPaths().size());

        BNFIndexPath nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getPathName());
        //        assertTrue(nodeA1.eq(null));
        assertEquals(0, nodeA1.getPaths().size());
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
        List<? extends BNFIndexPath> nodes = result.getPaths();
        assertEquals(2, nodes.size());

        BNFIndexPath nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getPathName());
        //        assertTrue(nodeA0.eq(null));
        assertEquals(3, nodeA0.getPaths().size());

        BNFIndexPath nodeB0 = nodeA0.getPaths().get(0);
        assertEquals(nodeB0.getPathName(), "\"firstName\"");
        assertTrue(nodeB0.eq("\"John\""));
        assertEquals(0, nodeB0.getPaths().size());

        BNFIndexPath nodeB1 = nodeA0.getPaths().get(1);
        assertEquals(nodeB1.getPathName(), "\"address\"");
        assertTrue(nodeB1.eq("{"));
        assertEquals(1, nodeB1.getPaths().size());

        BNFIndexPath nodeC0 = nodeB1.getPaths().get(0);
        assertEquals(nodeC0.getPathName(), "\"postalCode\"");
        assertTrue(nodeC0.eq("10021"));
        assertEquals(0, nodeC0.getPaths().size());

        BNFIndexPath nodeB2 = nodeA0.getPaths().get(2);
        assertEquals(nodeB2.getPathName(), "}");
        assertEquals(0, nodeB2.getPaths().size());

        BNFIndexPath nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getPathName());
        assertEquals(0, nodeA1.getPaths().size());
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
        List<? extends BNFIndexPath> nodes = result.getPaths();
        assertEquals(2, nodes.size());

        BNFIndexPath nodeA0 = nodes.get(0);
        assertEquals(nodeA0.getPathName(), "{");
        //        assertTrue(nodeA0.eq(null));
        assertEquals(2, nodeA0.getPaths().size());

        BNFIndexPath nodeB0 = nodeA0.getPaths().get(0);
        assertEquals(nodeB0.getPathName(), "\"phoneNumbers\"");
        assertTrue(nodeB0.eq("["));
        assertEquals(4, nodeB0.getPaths().size());

        BNFIndexPath nodeC0 = nodeB0.getPaths().get(0);
        assertEquals(nodeC0.getPathName(), "{");
        //        assertTrue(nodeC0.eq(null));
        assertEquals(2, nodeC0.getPaths().size());

        BNFIndexPath nodeD0 = nodeC0.getPaths().get(0);
        assertEquals("\"type\"", nodeD0.getPathName());
        assertTrue(nodeD0.eq("\"home\""));
        assertEquals(0, nodeD0.getPaths().size());

        BNFIndexPath nodeD1 = nodeC0.getPaths().get(1);
        assertEquals("\"number\"", nodeD1.getPathName());
        assertTrue(nodeD1.eq("\"212 555-1234\""));
        assertEquals(0, nodeD1.getPaths().size());

        BNFIndexPath nodeC1 = nodeB0.getPaths().get(1);
        assertEquals(nodeC1.getPathName(), "}");
        //        assertTrue(nodeC1.eq(null));
        assertEquals(0, nodeC1.getPaths().size());

        BNFIndexPath nodeC2 = nodeB0.getPaths().get(2);
        assertEquals("{", nodeC2.getPathName());
        //        assertTrue(nodeC2.eq(null));
        assertEquals(2, nodeC2.getPaths().size());

        nodeD0 = nodeC2.getPaths().get(0);
        assertEquals("\"type\"", nodeD0.getPathName());
        assertTrue(nodeD0.eq("\"fax\""));
        assertEquals(0, nodeD0.getPaths().size());

        nodeD1 = nodeC2.getPaths().get(1);
        assertEquals("\"number\"", nodeD1.getPathName());
        assertTrue(nodeD1.eq("\"646 555-4567\""));
        assertEquals(0, nodeD1.getPaths().size());

        BNFIndexPath nodeC3 = nodeB0.getPaths().get(3);
        assertEquals("}", nodeC3.getPathName());
        //        assertTrue(nodeC3.eq(null));
        assertEquals(0, nodeC3.getPaths().size());

        BNFIndexPath nodeB1 = nodeA0.getPaths().get(1);
        assertEquals("]", nodeB1.getPathName());
        //        assertTrue(nodeB1.eq(null));
        assertEquals(0, nodeB1.getPaths().size());

        BNFIndexPath nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getPathName());
        //        assertTrue(nodeA1.eq(null));
        assertEquals(0, nodeA1.getPaths().size());
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
        List<? extends BNFIndexPath> nodes = result.getPaths();
        assertEquals(2, nodes.size());

        BNFIndexPath nodeA0 = nodes.get(0);
        assertEquals("{", nodeA0.getPathName());
        //        assertTrue(nodeA0.eq(null));
        assertEquals(2, nodeA0.getPaths().size());

        BNFIndexPath nodeB0 = nodeA0.getPaths().get(0);
        assertEquals("\"list\"", nodeB0.getPathName());
        assertTrue(nodeB0.eq("["));
        assertEquals(2, nodeB0.getPaths().size());

        BNFIndexPath nodeC0 = nodeB0.getPaths().get(0);
        assertNull(nodeC0.getPathName());
        assertTrue(nodeC0.eq("\"A\""));
        assertEquals(0, nodeC0.getPaths().size());

        BNFIndexPath nodeC1 = nodeB0.getPaths().get(1);
        assertNull(nodeC1.getPathName());
        assertTrue(nodeC1.eq("\"B\""));
        assertEquals(0, nodeC1.getPaths().size());

        BNFIndexPath nodeB1 = nodeA0.getPaths().get(1);
        assertEquals("]", nodeB1.getPathName());
        //        assertTrue(nodeB1.eq(null));
        assertEquals(0, nodeB1.getPaths().size());

        BNFIndexPath nodeA1 = nodes.get(1);
        assertEquals("}", nodeA1.getPathName());
        //        assertTrue(nodeA1.eq(null));
        assertEquals(0, nodeA1.getPaths().size());
    }

    /**
     * testCreateIndex06 ParseResult is not Successful.
     */
    @Test
    public void testCreateIndex06() {

        // given
        String s = "{\"list\": \"A\", \"B\" ]}";
        BNFParseResult parseResult = this.jsonParser.parse(s);
        assertFalse(parseResult.isSuccess());

        // when
        BNFIndex result = this.indexBuilder.createIndex(parseResult);

        // then
        assertNull(result);
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
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

package ca.gobits.bnf.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Index Node.
 */
public class BNFIndexNode {

    /** key for the node. */
    private String keyValue;

    /** value for the node. */
    private String stringValue;

    /** list of children nodes. */
    private List<BNFIndexNode> nodeList;

    /**
     * default constructor.
     */
    public BNFIndexNode() {
        this.nodeList = new ArrayList<BNFIndexNode>();
    }

    /**
     * constructor.
     * @param key -
     * @param value -
     */
    public BNFIndexNode(final String key, final String value) {
        this();
        this.keyValue = key;
        this.stringValue = value;
    }

    /**
     * @return String
     */
    public String getKeyValue() {
        return this.keyValue;
    }

    /**
     * Setter for Key value.
     * @param key -
     */
    public void setKeyValue(final String key) {
        this.keyValue = key;
    }

    /**
     * @return List<BNFTreeNode>
     */
    public List<BNFIndexNode> getNodes() {
        return this.nodeList;
    }

    /**
     * @param nodes -
     */
    public void setNodes(final List<BNFIndexNode> nodes) {
        this.nodeList = nodes;
    }

    /**
     * Add node.
     * @param node -
     */
    public void addNode(final BNFIndexNode node) {
        this.nodeList.add(node);
    }

    /**
     * @return String
     */
    public String getStringValue() {
        return this.stringValue;
    }

    /**
     * @param value -
     */
    public void setStringValue(final String value) {
        this.stringValue = value;
    }
}
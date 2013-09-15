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

package ca.gobits.bnf.index;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Index Node.
 */
public class BNFIndexNode implements BNFIndexPath {

    /** key for the node. */
    private String keyValue;

    /** value for the node. */
    private String value;

    /** whether this node should be searched or skipped. */
    private boolean shouldSkip;

    /** list of children nodes. */
    private final List<BNFIndexNode> nodes;

    /**
     * default constructor.
     */
    public BNFIndexNode() {
        this.nodes = new ArrayList<BNFIndexNode>();
    }

    /**
     * constructor.
     * @param key -
     * @param string -
     */
    public BNFIndexNode(final String key, final String string) {
        this();
        this.keyValue = key;
        this.value = string;
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
     * Add node.
     * @param node -
     */
    public void addNode(final BNFIndexNode node) {
        this.nodes.add(node);
    }

    @Override
    public BNFIndexPath getPath(final String path) {
        return BNFIndexHelper.getPath(this.nodes, path);
    }

    /**
     * @return boolean
     */
    public boolean isShouldSkip() {
        return this.shouldSkip;
    }

    /**
     * Whether this index node is skipped on search.
     * @param skip -
     */
    public void setShouldSkip(final boolean skip) {
        this.shouldSkip = skip;
    }

    @Override
    public BNFIndexNode getNode() {
        return this;
    }

    @Override
    public boolean eq(final String string) {
        return this.value != null && this.value.equals(string);
    }

    @Override
    public String getPathName() {
        return this.keyValue;
    }

    @Override
    public List<? extends BNFIndexPath> getPaths() {
        return this.nodes;
    }

    /**
     * @return List<BNFIndexNode>
     */
    public List<BNFIndexNode> getNodes() {
        return this.nodes;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
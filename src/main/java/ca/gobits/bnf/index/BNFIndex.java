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
 * BNFIndex is a data structure that improves the speed of data retrieval operations.
 *
 * Indexes are used to quickly locate data without having to search every object.
 */
public class BNFIndex implements BNFIndexPath {

    /** Top nodes of the tree. */
    private List<BNFIndexNode> nodeList;

    /**
     * default contructor.
     */
    public BNFIndex() {
        this.nodeList = new ArrayList<BNFIndexNode>();
    }

    /**
     * @return List<BNFIndexNode> nodes of the tree
     */
    public List<BNFIndexNode> getNodes() {
        return this.nodeList;
    }

    /**
     * Setter for the Tree Top Nodes.
     * @param nodes - tree nodes
     */
    public void setNodes(final List<BNFIndexNode> nodes) {
        this.nodeList = nodes;
    }

    /**
     * Adds Node to Index.
     * @param node -
     */
    public void addNode(final BNFIndexNode node) {
        this.nodeList.add(node);
    }

    @Override
    public BNFIndexPath getPath(final String path) {
        return BNFIndexHelper.getPath(this.nodeList, path);
    }

    @Override
    public BNFIndexNode getNode() {
        return null;
    }

    @Override
    public boolean eq(final String string) {
        return false;
    }

    @Override
    public String getPathName() {
        return null;
    }

    @Override
    public List<? extends BNFIndexPath> getPaths() {
        return this.nodeList;
    }

    @Override
    public String getValue() {
        return null;
    }
}
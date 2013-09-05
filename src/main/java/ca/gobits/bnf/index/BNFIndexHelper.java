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

import java.util.List;

/**
 * BNFIndexHelper - helper class for BNFIndex.
 */
public final class BNFIndexHelper {

    /**
     * default constructor.
     */
    private BNFIndexHelper() {
    }

    /**
     * Finds an Index Path Node.
     * @param nodes -
     * @param path -
     * @return BNFIndexPath
     */
    public static BNFIndexPath getPath(final List<BNFIndexNode> nodes, final String path) {

        BNFIndexPath result = null;

        for (BNFIndexNode node : nodes) {

            if (node.isShouldSkip()) {

                result = getPath(node.getNodes(), path);
                break;

            } else {

                if (node.getPathName().equals(path)) {
                    result = node;
                    break;
                }
            }
        }

        return result;
    }
}

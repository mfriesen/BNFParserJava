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
 * BNFIndexPath describes how to traverse an index and find values.
 */
public interface BNFIndexPath {

    /**
     * Method for finding a path on an Index.
     * @param path -
     * @return BNFIndexPath
     */
    BNFIndexPath getPath(final String path);

    /**
     * @return BNFIndexPath
     */
    BNFIndexPath getNode();

    /**
     * Compare IndexPath value to a string.
     * @param value -
     * @return boolean
     */
    boolean eq(final String value);

    /**
     * @return String
     */
    String getPathName();

    /**
     * @return List<? extends BNFIndexPath>
     */
    List<? extends BNFIndexPath> getPaths();

    /**
     * @return String
     */
    String getValue();
}

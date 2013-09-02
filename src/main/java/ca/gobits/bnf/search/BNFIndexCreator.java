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

import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * Interface for BNFIndex.
 */
public interface BNFIndexCreator {

    /**
     * Returns with token is a start token.
     * @param token -
     * @return boolean
     */
    boolean isStartNode(BNFToken token);

    /**
     * Returns with token is a end token.
     * @param token -
     * @return boolean
     */
    boolean isEndNode(BNFToken token);

    /**
     * Returns with token is a key token.
     * @param token -
     * @return boolean
     */
    boolean isKey(BNFToken token);

    /**
     * Returns with token is a value token.
     * @param token -
     * @return boolean
     */
    boolean isValue(BNFToken token);

    /**
     * Returns the next token.
     * @param token -
     * @return BNFToken
     */
    BNFToken getNextToken(BNFToken token);
}

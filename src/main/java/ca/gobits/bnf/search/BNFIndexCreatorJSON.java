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
 * JSON Index Creator implementation.
 */
public class BNFIndexCreatorJSON implements BNFIndexCreator {

    @Override
    public boolean isStartNode(final BNFToken token) {
        String value = token.getStringValue();
        return value.equals("{") || value.equals("[");
    }

    @Override
    public boolean isEndNode(final BNFToken token) {
        String value = token.getStringValue();
        return value.equals("}") || value.equals("]");
    }

    @Override
    public boolean isKey(final BNFToken token) {

        boolean key = false;
        String value = token.getStringValue();

        if (value != null && token.getNextToken() != null) {
            BNFToken nextToken = token.getNextToken();
            key = ":".equals(nextToken.getStringValue());
        }

        return key;
    }

    @Override
    public boolean isValue(final BNFToken token) {
        String value = token.getStringValue();
        return !value.equals(":") && !value.equals(",");
    }

    @Override
    public BNFToken getNextToken(final BNFToken token) {

        BNFToken nextToken = token.getNextToken();
        return nextToken;
    }
}
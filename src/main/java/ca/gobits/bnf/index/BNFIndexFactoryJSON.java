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

import java.util.Stack;

import ca.gobits.bnf.parser.BNFParseResult;
import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * BNFIndexBuilder implements for creating a Tree structure index.
 *
 */
public class BNFIndexFactoryJSON implements BNFIndexFactory {

    @Override
    public BNFIndex createIndex(final BNFParseResult result) {

        if (!result.isSuccess()) {
            return null;
        }

        Stack<BNFIndexNode> stack = new Stack<BNFIndexNode>();

        BNFIndex index = new BNFIndex();

        String keyValue = null;
        BNFToken token = result.getTop();

        while (token != null) {

            if (isStartNode(token)) {

                BNFIndexNode node = createIndexNode(token, keyValue);

                if (keyValue == null) {
                    node.setShouldSkip(true);
                }

                addNode(stack, index, node);

                stack.push(node);
                keyValue = null;

            } else if (isKey(token)) {

                keyValue = getStringValue(token);

            } else if (isEndNode(token)) {

                stack.pop();

                BNFIndexNode node = createIndexNode(token, keyValue);
                addNode(stack, index, node);

            } else if (isValue(token)) {

                BNFIndexNode node = new BNFIndexNode(keyValue, getStringValue(token));
                addNode(stack, index, node);

                keyValue = null;
            }

            token = getNextToken(token);
        }

        return index;
    }

    /**
     * Create an IndexNode.
     * @param token -
     * @param keyValue -
     * @return BNFIndexNode
     */
    private BNFIndexNode createIndexNode(final BNFToken token, final String keyValue) {
        BNFIndexNode node = null;

        if (keyValue != null) {
            node = new BNFIndexNode(keyValue, getStringValue(token));
        } else {
            node = new BNFIndexNode(getStringValue(token), null);
        }

        return node;
    }

    /**
     * Add Node to Index.
     * @param stack -
     * @param index -
     * @param node -
     */
    private void addNode(final Stack<BNFIndexNode> stack, final BNFIndex index, final BNFIndexNode node) {
        if (stack.isEmpty()) {
            index.addNode(node);
        } else {
            stack.peek().addNode(node);
        }
    }

    /**
     * Gets the String value for the token if String is QuotedString, removes quotes.
     * @param token -
     * @return String
     */
    private String getStringValue(final BNFToken token) {

        String value = token.getStringValue();
        return value;
    }

    /**
     * Returns with token is a start token.
     * @param token -
     * @return boolean
     */
    public boolean isStartNode(final BNFToken token) {
        String value = token.getStringValue();
        return value.equals("{") || value.equals("[");
    }

    /**
     * Returns with token is a end token.
     * @param token -
     * @return boolean
     */
    public boolean isEndNode(final BNFToken token) {
        String value = token.getStringValue();
        return value.equals("}") || value.equals("]");
    }

    /**
     * Returns with token is a key token.
     * @param token -
     * @return boolean
     */
    public boolean isKey(final BNFToken token) {

        boolean key = false;
        String value = token.getStringValue();

        if (value != null && token.getNextToken() != null) {
            BNFToken nextToken = token.getNextToken();
            key = ":".equals(nextToken.getStringValue());
        }

        return key;
    }

    /**
     * Returns with token is a value token.
     * @param token -
     * @return boolean
     */
    public boolean isValue(final BNFToken token) {
        String value = token.getStringValue();
        return !value.equals(":") && !value.equals(",");
    }

    /**
     * Returns the next token.
     * @param token -
     * @return BNFToken
     */
    public BNFToken getNextToken(final BNFToken token) {

        BNFToken nextToken = token.getNextToken();
        return nextToken;
    }
}
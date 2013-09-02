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

import java.util.Stack;

import ca.gobits.bnf.parser.BNFParseResult;
import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * BNFIndexBuilder implements for creating a Tree structure index.
 *
 */
public class BNFIndexBuilderImpl implements BNFIndexBuilder {

    @Override
    public BNFIndex createIndex(final BNFParseResult result, final BNFIndexCreator indexCreator) {

        if (!result.isSuccess()) {
            throw new IllegalArgumentException("Result must have status success=true");
        }

        Stack<BNFIndexNode> stack = new Stack<BNFIndexNode>();

        BNFIndex index = new BNFIndex();

        String keyValue = null;
        BNFToken token = result.getTop();

        while (token != null) {

            if (indexCreator.isStartNode(token)) {

                BNFIndexNode node = new BNFIndexNode(keyValue, token.getStringValue());
                addNode(stack, index, node);

                stack.push(node);
                keyValue = null;

            } else if (indexCreator.isKey(token)) {

                keyValue = token.getStringValue();

            } else if (indexCreator.isEndNode(token)) {

                stack.pop();
                BNFIndexNode node = new BNFIndexNode(null, token.getStringValue());
                addNode(stack, index, node);

            } else if (indexCreator.isValue(token)) {

                BNFIndexNode node = new BNFIndexNode(keyValue, token.getStringValue());
                addNode(stack, index, node);

                keyValue = null;
            }

            token = indexCreator.getNextToken(token);
        }

        return index;
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
}
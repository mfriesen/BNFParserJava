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

package ca.gobits.bnf.parser;

import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * BNF Parser Result implementation.
 *
 */
public class BNFParseResultImpl implements BNFParseResult {

    /** Top token. */
    private BNFToken top;

    /** Top error token. */
    private BNFToken error;

    /** Max token was successfully validated. */
    private BNFToken maxToken;

    /** Was Parser successful. */
    private boolean success;

    /**
     * default constructor.
     */
    public BNFParseResultImpl() {
    }

    @Override
    /**
     * @return boolean
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * @param status -
     */
    public void setSuccess(final boolean status) {
        this.success = status;
    }

    @Override
    /**
     * @return BNFToken
     */
    public BNFToken getTop() {
        return this.top;
    }

    /**
     * @param token -
     */
    public void setTop(final BNFToken token) {
        this.top = token;
    }

    @Override
    /**
     * @return BNFToken
     */
    public BNFToken getError() {
        return this.error;
    }

    /**
     * @param token -
     */
    public void setError(final BNFToken token) {
        this.error = token;
    }

    /**
     * @param token -
     */
    public void setMaxMatchToken(final BNFToken token) {
        if (this.maxToken == null
                || (token != null && token.getId() > this.maxToken.getId())) {
            this.maxToken = token;
        }
    }

    /**
     * complete.
     */
    public void complete() {

        if (!isSuccess()) {
            setError(this.maxToken);
        }
    }
}

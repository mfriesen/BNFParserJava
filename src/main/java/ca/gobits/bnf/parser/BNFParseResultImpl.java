//
//  Copyright (c) 2013 Mike Friesen
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package ca.gobits.bnf.parser;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParseResultImpl implements BNFParseResult {

	private BNFToken top;
	private BNFToken error;
	private BNFToken maxToken;
	private boolean success;

	public BNFParseResultImpl() {		
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public BNFToken getTop() {
		return top;
	}

	public void setTop(BNFToken top) {
		this.top = top;
	}

	@Override
	public BNFToken getError() {
		return error;
	}

	public void setError(BNFToken error) {
		this.error = error;
	}

	public void setMaxMatchToken(BNFToken token) {
		if (this.maxToken == null || (token != null && token.getId() > this.maxToken.getId())) {
			this.maxToken = token;
		}
	}

	public void complete() {

		if (!isSuccess()) {			
			setError(maxToken);
		}
	}
}
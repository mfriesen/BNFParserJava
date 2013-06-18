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

public class BNFStateEmpty extends BNFState {
	public boolean match(BNFToken token) {
		return true;
	}
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return token != null && token.getValue().trim().length() == 0 ? true : false;
	}
}

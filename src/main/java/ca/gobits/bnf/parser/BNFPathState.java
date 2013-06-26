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

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFStateEnd;
import ca.gobits.bnf.tokenizer.BNFToken;


public class BNFPathState implements BNFPath {

	private BNFToken token;
	private BNFState state;
	
	public BNFPathState() {
	}
	
	public BNFPathState(BNFState state, BNFToken token) {
		this();
		this.state = state;
		this.token = token;
	}
	
	@Override
	public String toString() {
		return state + " " + token;
	}
	
	@Override
	public BNFToken getToken() {
		return token;
	}

	public void setToken(BNFToken token) {
		this.token = token;
	}

	public BNFState getState() {
		return state;
	}

	public void setState(BNFState state) {
		this.state = state;
	}

	@Override
	public boolean isStateEnd() {
		return getState().getClass().equals(BNFStateEnd.class);
	}

	@Override
	public boolean isStateDefinition() {
		return false;
	}

	@Override
	public BNFState getNextState() {
		return state.getNextState();
	}
}
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
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFPathStateDefinition implements BNFPath {

	private BNFStateDefinition stateDefinition;
	private BNFToken token;
	private int position;

	public BNFPathStateDefinition() {
		this.position = 0;
	}

	public BNFStateDefinition getStateDefinition() {
		return this.stateDefinition;
	}

	public void setStateDefinition(BNFStateDefinition stateDefinition) {
		this.stateDefinition = stateDefinition;
	}

	@Override
	public BNFToken getToken() {
		return this.token;
	}

	public void setToken(BNFToken token) {
		this.token = token;
	}

	@Override
	public boolean isStateEnd() {
		return false;
	}
	
	@Override
	public String toString() {
		return "state definition " + stateDefinition.getName() + " token " + token.getStringValue();
	}

	@Override
	public boolean isStateDefinition() {
		return true;
	}
	
	public boolean hasNextSequence()
	{
		BNFState state = null;
		
		if (position < stateDefinition.getStates().size()) {
			state = stateDefinition.getStates().get(position);
		}
		
		return state != null;
	}
	
	public BNFState getNextSequence() {
		
		BNFState state = null;
		
		if (position < stateDefinition.getStates().size()) {
			state = stateDefinition.getStates().get(position);
			position++;
		}
		
		return state;
	}

	@Override
	public BNFState getNextState() {
		
		BNFState state = null;
		
		if (position < stateDefinition.getStates().size()) {
			state = stateDefinition.getStates().get(position).getNextState();
		}
		
		return state;
	}	
}
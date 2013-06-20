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

package ca.gobits.bnf.parser.states;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFState {

	public enum BNFRepetition { NONE, ZERO_OR_MORE }
		
	private String name;
	private BNFState nextState;
	private BNFRepetition repetition;
	private int position;
	
	public BNFState() {
		this.repetition = BNFRepetition.NONE;
	}

	public BNFState(String name) {
		this();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BNFRepetition getRepetition() {
		return repetition;
	}

	public void setRepetition(BNFRepetition repetition) {
		this.repetition = repetition;
	}

	public BNFState getNextState() {
		return nextState;
	}

	public void setNextState(BNFState nextState) {
		this.nextState = nextState;
	}
	
	public boolean match(BNFToken token) {
		return nameMatches(token) /*|| repetition == Repetition.ZERO_OR_MORE*/;
	}
	
	private boolean nameMatches(BNFToken token) {
		return getName().equals(token.getValue());
	} 
	
	public boolean matchAdvancedToNextToken(BNFToken token) {
		return nameMatches(token);
	}
	
	public boolean isEnd() {
		return false;
	}
	
	@Override
	public String toString() {
		return "state " + getName();
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public boolean isTerminal() {
		return false;
	}
}

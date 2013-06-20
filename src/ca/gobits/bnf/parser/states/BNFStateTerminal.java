package ca.gobits.bnf.parser.states;

public class BNFStateTerminal extends BNFState {
	
	public BNFStateTerminal() {
		super();
	}
	
	public BNFStateTerminal(String name) {
		super(name);
	}
	@Override
	public boolean isTerminal() {
		return true;
	}
}

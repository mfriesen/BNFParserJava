package ca.gobits.bnf.parser;

public class BNFStateEnd extends BNFState {

	public BNFStateEnd() {
		super("@end");
	}
	
	public boolean isEnd() {
		return true;
	}
}

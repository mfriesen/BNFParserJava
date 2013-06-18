package ca.gobits.bnf.tokenizer;

public interface BNFTokenizerFactory {
	BNFToken tokens(String text);
}

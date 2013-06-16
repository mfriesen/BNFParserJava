package ca.gobits.bnfparser.tokenizer;

public interface BNFTokenizerFactory {
	BNFToken tokens(String text);
}

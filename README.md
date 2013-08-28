## BNFParserJava

**BNFParserJava** is a [**Backus-Naur Form**](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form) Framework for JAVA written by **Mike Friesen** in Java and released under the **Apache 2 Open Source License**.

An [**Objective-C**](https://github.com/mfriesen/BNFParser) version is also available.

**BNFParserJava** was inspired by the framework [ParseKit](http://parsekit.com/) by Todd Ditchendorf.

The **BNFParserJava** Framework offers 3 basic services of general interest to developers:

1. String Tokenization via the `BNFTokenizerFactory` and `BNFToken` classes

2. Property Key/Value mapper via `PropertyParser`

3. Text Parsing via Grammars via **BNFParser** [see grammar syntax](http://parsekit.com/grammars.html)

### 1. String Tokenization

The string tokenizer breaks down any string into a series of letter/number/symbols for easy processing.

#### How do I use it? 
    String text = "The cow jumped over the moon!";
    BNFTokenizerFactory factory = new BNFTokenizerFactoryImpl();
    BNFToken token = factory.tokens(text);
    while (token != null) {
      System.out.println("TOKEN " + token.getStringValue());
      token = token.getNextToken();
    }

### 2. PropertyParser

Uses the string tokenizer to parse a string and create key/value mapping based on the `'='` symbol.

#### How do I use it?

    String text = "sample key = sample value";
    PropertyParser parser = new PropertyParser();
    Map<String, String> keyValueMap = parser.parse(text);
    Assert.assertNotNull(keyValueMap.get("sample key"));

### 3. Text Parsing via Grammars

**BNFParser** currently only ships with a **JSON grammar** so the example are based on that.

#### How do I use it?

    // Create String Tokens
    String text = "{ \"key\":\"value\"}";
    BNFTokenizerFactory tokenizerFactory = new BNFTokenizerFactoryImpl();
    BNFToken token = tokenizerFactory.tokens(text);
    
    // Create Backus-Naur Form State Definitions
    BNFSequenceFactory factory = new BNFSequenceFactoryImpl();
    Map<String, List<BNFSequence>> map = sdf.json();
    
    // Run Tokens through Parser
    BNFParser parser = new BNFParserImpl(map);
    BNFParseResult result = parser.parse(token);
    
    // Verify results
    
    // verify text passes grammar:
    Assert.assertTrue(result.isSuccess()); // verify text passes grammar
    
    // the "first" token, same as the token returned from the tokenizer factory:
    Assert.assertNotNull(result.getTop());
    
    // the "first" error token, this token and any afterwards are considered to not have passed the grammar:
    Assert.assertNull(result.getError());

### Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/Netflix/BNFParser/issues).

### Contributing

We love contributions! If you'd like to contribute please submit a pull request via Github. 

### LICENSE

This library is distributed under the **Apache 2 Open Source License**.
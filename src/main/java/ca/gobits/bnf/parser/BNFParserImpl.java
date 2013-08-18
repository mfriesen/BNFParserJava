//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.bnf.parser;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import ca.gobits.bnf.parser.BNFParserState.BNFParserRepetition;
import ca.gobits.bnf.parser.BNFParserState.ParserState;
import ca.gobits.bnf.parser.BNFSymbol.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * BNF Parser implementation.
 */
public class BNFParserImpl implements BNFParser {

    /** Number Pattern. */
    private final Pattern numberPattern = Pattern.compile("^[\\d\\-\\.]+$");

    /** Holder for BNFSequences map. */
    private final Map<String, BNFSequences> stateDefinitions;

    /** BNF processing stack. */
    private final Stack<BNFParserState> stack = new Stack<BNFParserState>();

    /**
     * constructor.
     * @param map -
     */
    public BNFParserImpl(final Map<String, BNFSequences> map) {
        this.stateDefinitions = map;
    }

    @Override
    public BNFParseResult parse(final BNFToken token) {
        BNFSequences sd = stateDefinitions.get("@start");
        addParserState(sd, token, BNFParserRepetition.NONE, BNFRepetition.NONE);

        return parseSequences(token);
    }

    /**
     * Main loop for parsing.
     * @param startToken -
     * @return BNFParseResultImpl
     */
    private BNFParseResultImpl parseSequences(final BNFToken startToken) {

        boolean success = false;
        BNFToken maxMatchToken = startToken;
        BNFToken errorToken = null;

        BNFParseResultImpl result = new BNFParseResultImpl();
        result.setTop(startToken);

        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            if (holder.getState() == ParserState.EMPTY) {

                stack.pop();
                BNFToken token = stack.peek().getCurrentToken();
                if (!isEmpty(token)) {
                    rewindToNextSymbol();
                } else {
                    success = true;
                    errorToken = null;
                    rewindToNextSequence();
                }
            } else if (holder.getState() == ParserState.NO_MATCH_WITH_ZERO_REPETITION) {
                processNoMatchWithZeroRepetition();
            } else if (holder.getState() == ParserState.MATCH_WITH_ZERO_REPETITION) {
                processMatchWithZeroRepetition();
            } else if (holder.getState() == ParserState.NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH) {
                maxMatchToken = processNoMatchWithZeroRepetitionLookingForFirstMatch();
                errorToken = null;
                success = true;
            } else if (holder.getState() == ParserState.MATCH) {
                maxMatchToken = processMatch();
                errorToken = null;
                success = true;
            } else if (holder.getState() == ParserState.NO_MATCH) {
                BNFToken eToken = processNoMatch();
                errorToken = updateErrorToken(errorToken, eToken);
                success = false;
            } else {
                processStack();
            }
        }

        result.setError(errorToken);
        result.setMaxMatchToken(maxMatchToken);
        result.setSuccess(success);

        return result;
    }

    /**
     * Returns The BNFToken with the largest ID.
     * @param token1 -
     * @param token2 -
     * @return BNFToken
     */
    private BNFToken updateErrorToken(final BNFToken token1, final BNFToken token2) {
        return token1 != null && token1.getId() > token2.getId() ? token1
                : token2;
    }

    /**
     * Rewind stack to the next sequence.
     * @return BNFToken
     */
    private BNFToken processNoMatch() {

        debugPrintIndents();
        System.out.println("-> no match, rewinding to next sequence");

        stack.pop();

        BNFToken token = stack.peek().getCurrentToken();

        rewindToNextSequence();

        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            holder.resetToken();
        }

        return token;
    }

    /**
     * @return BNFToken
     */
    private BNFToken processMatchWithZeroRepetition() {
        stack.pop();

        BNFToken token = stack.peek().getCurrentToken();

        debugPrintIndents();
        System.out.println("-> matched token " + token.getStringValue() + " rewind to start of repetition");

        rewindToStartOfRepetition();

        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            holder.advanceToken(token.getNextToken());
        }

        return token;
    }

    /**
     * @return BNFToken
     */
    private BNFToken processNoMatchWithZeroRepetitionLookingForFirstMatch() {

        stack.pop();

        BNFToken token = stack.peek().getCurrentToken();

        debugPrintIndents();
        System.out
        .println("-> no match Zero Or More Looking for First Match token "
                + debug(token) + " rewind outside of Repetition");

        rewindToOutsideOfRepetition();
        rewindToNextSymbol();

        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            holder.advanceToken(token);
        }

        return token;
    }

    /**
     * Rewind stack to next symbol.
     * @return BNFToken
     */
    private BNFToken processMatch() {

        stack.pop();

        BNFToken token = stack.peek().getCurrentToken();

        debugPrintIndents();
        System.out.println("-> matched token " + token.getStringValue()
                + " rewind to next symbol");

        rewindToNextSymbolOrRepetition();

        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            token = token.getNextToken();
            holder.advanceToken(token);
        }

        return token;
    }

    /**
     * processNoMatchWithZeroRepetition.
     */
    private void processNoMatchWithZeroRepetition() {

        debugPrintIndents();
        System.out.println("-> " + ParserState.NO_MATCH_WITH_ZERO_REPETITION
                + ", rewind to next symbol");

        stack.pop();

        BNFToken token = stack.peek().getCurrentToken();

        rewindToNextSymbol(BNFParserRepetition.ZERO_OR_MORE);

        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            holder.advanceToken(token);
        }
    }

    /**
     * @param repetition -
     */
    private void rewindToNextSymbol(final BNFParserRepetition repetition) {
        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            if (holder.isSequence() && !holder.isComplete()
                    && holder.getParserRepetition() != repetition) {
                break;
            }

            stack.pop();
        }
    }

    /**
     * rewindToOutsideOfRepetition.
     */
    private void rewindToOutsideOfRepetition() {

        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            if (holder.getParserRepetition() != BNFParserRepetition.NONE) {
                stack.pop();
            } else {
                break;
            }
        }
    }

    /**
     * rewindToStartOfRepetition.
     */
    private void rewindToStartOfRepetition() {

        BNFParserState startOfRepetition = null;

        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            if (holder.getParserRepetition() != BNFParserRepetition.NONE) {
                startOfRepetition = holder;
                stack.pop();
            } else {
                break;
            }
        }

        if (startOfRepetition != null) {
            this.stack.push(startOfRepetition);
        }
    }

    /**
     * Rewinds to next incomplete sequence or to ZERO_OR_MORE repetition which
     * ever one is first.
     */
    private void rewindToNextSymbolOrRepetition() {
        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            if (holder.getRepetition() == BNFRepetition.ZERO_OR_MORE
                    && holder.isComplete()) {
                holder.reset();
                if (holder.getRepetition() != BNFRepetition.NONE) {
                    holder.setParserRepetition(BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH);
                }
                break;
            } else if (holder.isSequence() && !holder.isComplete()) {
                if (holder.getParserRepetition() == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {
                    holder.setParserRepetition(BNFParserRepetition.NONE);
                }

                break;
            }

            stack.pop();
        }
    }

    /**
     * Rewinds to next incomplete sequence or to ZERO_OR_MORE repetition which
     * ever one is first.
     */
    private void rewindToNextSymbol() {
        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();

            if (holder.isSequence() && !holder.isComplete()) {
                break;
            }

            stack.pop();
        }
    }

    /**
     * rewindToNextSequence.
     */
    private void rewindToNextSequence() {

        while (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            if (holder.isSequences()) {
                break;
            }

            stack.pop();
        }
    }

    /**
     * processStack.
     */
    private void processStack() {

        BNFParserState holder = stack.peek();

        if (holder.isComplete()) {
            stack.pop();
        } else {
            if (holder.isSequences()) {
                if (holder.isComplete()) {
                    stack.pop();

                } else {

                    BNFSequence pipeLine = holder.getNextSequence();
                    addParserState(pipeLine, holder.getCurrentToken(), holder.getParserRepetition(), BNFRepetition.NONE);
                }
            } else if (holder.isSequence()) {
                BNFSymbol pipe = holder.getNextSymbol();
                String nextPipe = pipe.getName();
                BNFSequences sd = stateDefinitions.get(nextPipe);

                BNFParserRepetition repetition = getParserRepetition(holder,
                        pipe);

                if (sd != null) {
                    addParserState(sd, holder.getCurrentToken(), repetition,
                            pipe.getRepetition());
                } else {
                    if (nextPipe.equals("Empty")) {
                        addParserState(ParserState.EMPTY);
                    } else if (isMatch(nextPipe, holder.getCurrentToken())) {
                        addParserState(ParserState.MATCH);
                    } else if (repetition == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {

                        addParserState(ParserState.NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH);

                    } else if (repetition == BNFParserRepetition.ZERO_OR_MORE) {
                        addParserState(ParserState.NO_MATCH_WITH_ZERO_REPETITION);
                    } else {
                        addParserState(ParserState.NO_MATCH);
                    }

                }
            }
        }
    }

    /**
     * @param symbolName -
     * @param token -
     * @return boolean
     */
    private boolean isMatch(final String symbolName, final BNFToken token) {

        boolean match = false;

        if (token != null) {
            String s = isQuotedString(symbolName) ? symbolName.substring(1,
                    symbolName.length() - 1) : symbolName;
            match = s.equals(token.getStringValue())
                    || isQuotedString(symbolName, token)
                    || isNumber(symbolName, token);
        }

        return match;
    }

    /**
     * @param value -
     * @return boolean
     */
    private boolean isQuotedString(final String value) {
        return (value.startsWith("\"") && value.endsWith("\""))
                || value.startsWith("'") && value.endsWith("'");
    }

    /**
     * @param symbolName -
     * @param token -
     * @return boolean
     */
    private boolean isQuotedString(final String symbolName, final BNFToken token) {
        String value = token.getStringValue();
        return symbolName.equals("QuotedString") && isQuotedString(value);
    }

    /**
     * @param symbolName -
     * @param token -
     * @return boolean
     */
    private boolean isNumber(final String symbolName, final BNFToken token) {

        boolean match = false;

        if (token != null && symbolName.equals("Number")) {
            String value = token.getStringValue();
            match = numberPattern.matcher(value).matches();
        }

        return match;
    }

    /**
     * @param state -
     */
    private void addParserState(final ParserState state) {
        stack.push(new BNFParserState(state));
    }

    /**
     * @param sd -
     * @param token -
     * @param parserRepetition -
     * @param repetition -
     */
    private void addParserState(final BNFSequences sd, final BNFToken token, final BNFParserRepetition parserRepetition, final BNFRepetition repetition) {

        if (sd.getSequences().size() == 1) {
            addParserState(sd.getSequences().get(0), token, parserRepetition,
                    repetition);
        } else {
            debug(sd, token, parserRepetition);
            stack.push(new BNFParserState(sd, token, parserRepetition,
                    repetition));
        }
    }

    /**
     * @param sequence -
     * @param token -
     * @param parserRepetition -
     * @param repetition -
     */
    private void addParserState(final BNFSequence sequence, final BNFToken token, final BNFParserRepetition parserRepetition, final BNFRepetition repetition) {
        debug(sequence, token, parserRepetition);
        stack.push(new BNFParserState(sequence, token, parserRepetition, repetition));
    }

    /**
     * @param holder -
     * @param symbol -
     * @return BNFParserRepetition
     */
    private BNFParserRepetition getParserRepetition(final BNFParserState holder, final BNFSymbol symbol) {

        BNFRepetition symbolRepetition = symbol.getRepetition();
        BNFParserRepetition holderRepetition = holder.getParserRepetition();

        if (symbolRepetition != BNFRepetition.NONE
                && holderRepetition == BNFParserRepetition.NONE) {
            holderRepetition = BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH;
        } else if (symbolRepetition != BNFRepetition.NONE
                && holderRepetition != BNFParserRepetition.NONE) {
            holderRepetition = BNFParserRepetition.ZERO_OR_MORE;
        }

        return holderRepetition;
    }

    /**
     * @param currentToken -
     * @return boolean
     */
    private boolean isEmpty(final BNFToken currentToken) {
        return currentToken == null || currentToken.getStringValue() == null || currentToken.getStringValue().length() == 0;
    }

    /**
     * debug.
     */
    private void debugPrintIndents() {
        int size = this.stack.size() - 1;
        for (int i = 0; i < size; i++) {
            System.out.print(" ");
        }
    }

    /**
     * debug.
     * @param token -
     * @return String
     */
    private String debug(final BNFToken token) {
        return token != null ? token.getStringValue() : null;
    }

    /**
     * debug.
     * @param sequence -
     * @param token -
     * @param repetition -
     */
    private void debug(final BNFSequence sequence, final BNFToken token, final BNFParserRepetition repetition) {
        debugPrintIndents();
        System.out.println("-> procesing pipe line " + sequence + " for token "
                + debug(token) + " with repetition " + repetition);
    }

    /**
     * debug.
     * @param sd -
     * @param token -
     * @param repetition -
     */
    private void debug(final BNFSequences sd, final BNFToken token, final BNFParserRepetition repetition) {
        debugPrintIndents();
        System.out.println("-> adding pipe lines " + sd.getSequences()
                + " for token " + debug(token) + " with repetition "
                + repetition);
    }
}
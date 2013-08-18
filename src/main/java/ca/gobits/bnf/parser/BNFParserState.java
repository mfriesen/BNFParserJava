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

import ca.gobits.bnf.parser.BNFSymbol.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

/**
 * BNFParserState holds the states of the parser.
 */
public class BNFParserState {

    /** BNFParserRepetition. */
    public enum BNFParserRepetition {
        /** NONE. */
        NONE,
        /** ZERO OR MORE. */
        ZERO_OR_MORE,
        /** ZERO OR MORE LOOKING FOR FIRST MATCH. */
        ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH
    }

    /** BNFParser State. */
    public enum HolderState {
        /** NONE. */
        NONE,
        /** MATCH. */
        MATCH,
        /** NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH. */
        NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH,
        /** NO_MATCH. */
        NO_MATCH,
        /** MATCH_WITH_ZERO_REPETITION. */
        MATCH_WITH_ZERO_REPETITION,
        /** NO_MATCH_WITH_ZERO_REPETITION. */
        NO_MATCH_WITH_ZERO_REPETITION,
        /** EMPTY. */
        EMPTY
    }

    /** position in sequence or symbols. */
    private int currentPosition = -1;

    /** current state. */
    private HolderState state;

    /** original token. */
    private BNFToken originalToken;

    /** current token. */
    private BNFToken currentToken;

    /** BNFSequences. */
    private BNFSequences sequences;

    /** BNFSequence. */
    private BNFSequence sequence;

    /** BNFRepetition. */
    private BNFRepetition repetition;

    /** BNFParserRepetition. */
    private BNFParserRepetition parserRepetition;

    /**
     * default constructor.
     */
    private BNFParserState() {
        setState(HolderState.NONE);
        this.parserRepetition = BNFParserRepetition.NONE;
    }

    /**
     * constructor.
     * @param parserState -
     */
    public BNFParserState(final HolderState parserState) {
        this();
        setState(parserState);
    }

    /**
     * constructor.
     * @param seqs -
     * @param token -
     */
    public BNFParserState(final BNFSequences seqs, final BNFToken token) {
        this(token);
        this.sequences = seqs;
    }

    /**
     * constructor.
     * @param token -
     */
    private BNFParserState(final BNFToken token) {
        this();
        this.originalToken = token;
        this.currentToken = this.originalToken;
    }

    /**
     * constructor.
     * @param seq -
     * @param token -
     */
    public BNFParserState(final BNFSequence seq, final BNFToken token) {
        this(token);
        this.sequence = seq;
    }

    /**
     * constructor.
     * @param sd -
     * @param token -
     * @param parserRep -
     * @param rep -
     */
    public BNFParserState(final BNFSequences sd, final BNFToken token, final BNFParserRepetition parserRep, final BNFRepetition rep) {
        this(sd, token);
        this.parserRepetition = parserRep;
        this.repetition = rep;
    }

    public BNFParserState(BNFSequence sequence, BNFToken token, BNFParserRepetition parserRepetition, BNFRepetition repetition) {
        this(sequence, token);
        this.parserRepetition = parserRepetition;
        this.repetition = repetition;
    }

    public void advanceToken(BNFToken token2) {
        this.currentToken = token2;
    }

    public void resetToken() {
        this.currentToken = this.originalToken;
    }

    public boolean isSequences() {
        return this.sequences != null;
    }

    public boolean isSequence() {
        return this.sequence != null;
    }

    public BNFToken getCurrentToken() {
        return currentToken;
    }

    public BNFSequences getPipeLines() {
        return sequences;
    }

    public void setPipeLines(BNFSequences pipeLines) {
        this.sequences = pipeLines;
    }

    public boolean isComplete() {
        return this.isCompleteSequence() || isCompleteSymbol();
    }

    public BNFSequence getPipeLine() {
        return sequence;
    }

    public HolderState getState() {
        return state;
    }

    public void setState(HolderState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        if (sequences != null) {
            return this.sequences.toString();
        }

        if (this.sequence != null) {
            return this.sequence.toString();
        }

        return "status " + this.state;
    }

    public BNFParserRepetition getParserRepetition() {
        return parserRepetition;
    }

    public void setParserRepetition(BNFParserRepetition parserRepetition) {
        this.parserRepetition = parserRepetition;
    }

    public BNFSequence getNextSequence() {
        BNFSequence seq = null;
        int i = currentPosition + 1;

        if (i < this.sequences.getSequences().size()) {
            seq = this.sequences.getSequences().get(i);
            currentPosition = i;
        }

        return seq;
    }

    public boolean isCompleteSequence() {
        return this.sequences != null
                && this.currentPosition >= this.sequences.getSequences().size() - 1;
    }

    public BNFSymbol getNextSymbol() {

        BNFSymbol symbol = null;
        int i = this.currentPosition + 1;

        if (i < this.sequence.getSymbols().size()) {
            symbol = this.sequence.getSymbols().get(i);
            this.currentPosition = i;
        }

        return symbol;
    }

    public boolean isCompleteSymbol() {
        return this.sequence != null
                && this.currentPosition >= this.sequence.getSymbols().size() - 1;
    }

    public BNFRepetition getRepetition() {
        return this.repetition;
    }

    public void reset() {
        this.currentPosition = -1;
    }
}

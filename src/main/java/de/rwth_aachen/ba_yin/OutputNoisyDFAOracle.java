package de.rwth_aachen.ba_yin;

import de.learnlib.oracle.SingleQueryOracle;
import de.learnlib.oracle.membership.SimulatorOracle;

import java.lang.Boolean;
import java.util.Map;
import java.util.Random;

import net.automatalib.automaton.fsa.DFA;
import net.automatalib.word.Word;

public class OutputNoisyDFAOracle<I> extends SimulatorOracle<I, Boolean> implements SingleQueryOracle.SingleQueryOracleDFA<I> {
    
	private final float mistakeProb;
    private final Random random = new Random();
    private Map<Word<I>, Boolean> knownMistakes;

    public OutputNoisyDFAOracle(DFA<?, I> dfa, float mistakeProb, Map<Word<I>, Boolean> knownMistakes) {
        super(dfa);
        this.mistakeProb = mistakeProb;
        this.knownMistakes = knownMistakes;
    }

    @Override
    public Boolean answerQuery(Word<I> prefix, Word<I> suffix) {
    	
    	Word<I> fullQuery = prefix.concat(suffix);
     	
    	if (knownMistakes.containsKey(fullQuery)) {
            return knownMistakes.get(fullQuery);
        }else {
    	
        	boolean shouldFlip = random.nextFloat() < mistakeProb;
        	boolean originalResult = super.answerQuery(prefix, suffix);
        
        	if (shouldFlip) {
        		knownMistakes.put(fullQuery, !originalResult);
        		return !originalResult;
        	}else {
        		knownMistakes.put(fullQuery, originalResult);
        		return originalResult;
        	}
        }
    }
}
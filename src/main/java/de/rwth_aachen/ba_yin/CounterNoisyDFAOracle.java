package de.rwth_aachen.ba_yin;

import java.util.Map;
import java.util.Random;

import de.learnlib.oracle.SingleQueryOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.word.Word;

public class CounterNoisyDFAOracle<I> extends SimulatorOracle<I, Boolean> implements SingleQueryOracle.SingleQueryOracleDFA<I> {
    
	private final Integer initCount;
	private final float mistakeProb;
	private final Map<Character, Integer> alphabetToCounter;
	private Map<Word<I>, Boolean> knownMistakes;
	boolean sup;
    private final Random random = new Random();
    Alphabet<I> alphabet;

    public CounterNoisyDFAOracle(DFA<?, I> dfa, float mistakeProb, Map<Character, Integer> alphabetToCounter, Map<Word<I>, Boolean> knownMistakes, Alphabet<I> alphabet, Integer initCount) {
        super(dfa);
        this.mistakeProb = mistakeProb;
        this.knownMistakes = knownMistakes;
        this.alphabetToCounter = alphabetToCounter;
        this.alphabet = alphabet;
        this.initCount = initCount;
        this.sup = true;
    }

    @Override
    public Boolean answerQuery(Word<I> prefix, Word<I> suffix) {
    	
    	Word<I> fullQuery = prefix.concat(suffix);

    	if (knownMistakes.containsKey(fullQuery)) {
            return knownMistakes.get(fullQuery);
        }else {
        	Integer counter = initCount;

        	for (int i = 0; i < prefix.length(); i++) {
                counter = counter + alphabetToCounter.get(prefix.getSymbol(i));
            }
        	for (int i = 0; i < suffix.length(); i++) {
        		counter = counter + alphabetToCounter.get(suffix.getSymbol(i));
            }
        	
        	boolean result = super.answerQuery(prefix,suffix);
        	
        	if(counter < 0) {
        		 if (random.nextFloat() < mistakeProb) {
                     result = !sup;
                 }else {
                	 result = sup;
                 }
        	}
        	
        	knownMistakes.put(fullQuery, result);
        	return result;
        }
    }
}
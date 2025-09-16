package de.rwth_aachen.ba_yin;

import java.util.Map;

import de.learnlib.oracle.SingleQueryOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.word.Word;

public class CounterDFAOracle<I> extends SimulatorOracle<I, Boolean> implements SingleQueryOracle.SingleQueryOracleDFA<I> {
    
	private final Integer initCount;
	private final Map<Character, Integer> alphabetToCounter;
	boolean sup;
    Alphabet<I> alphabet;

    public CounterDFAOracle(DFA<?, I> dfa, Map<Character, Integer> alphabetToCounter, Alphabet<I> alphabet, Integer initCount) {
        super(dfa);
        this.alphabetToCounter = alphabetToCounter;
        this.alphabet = alphabet;
        this.initCount = initCount;
        this.sup = true;
    }

    @Override
    public Boolean answerQuery(Word<I> prefix, Word<I> suffix) {
    	Integer counter = initCount;

        for (int i = 0; i < prefix.length(); i++) {
        	counter = counter + alphabetToCounter.get(prefix.getSymbol(i));
        }
        for (int i = 0; i < suffix.length(); i++) {
        	counter = counter + alphabetToCounter.get(suffix.getSymbol(i));
        }
        
        if(counter < 0) {
        	return !sup;
        }else {
        	return super.answerQuery(prefix,suffix);
        }
    }
}
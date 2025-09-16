package de.rwth_aachen.ba_yin;

import java.util.HashMap;
import java.util.Map;

import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.word.Word;

public class CounterDFA<I> {
	private final CompactDFA<I> dfa;
	private final float mistakeProb;
    private final Map<Word<Character>, Boolean> knownMistakes;
    private final Map<Character, Integer> alphabetToCounter;
    private final Integer initCount;

    public CounterDFA(CompactDFA<I> dfa, float mistakeProb, Map<Character, Integer> alphabetToCounter) {
        this.dfa = dfa;
        this.mistakeProb = mistakeProb;
        this.knownMistakes = new HashMap<>();
        this.alphabetToCounter = alphabetToCounter;
        this.initCount = 0;
    }
    
    public Integer getInitCount() {
    	return initCount;
    }
    
    public float getMistakeProb() {
    	return mistakeProb;
    }

    public CompactDFA<I> getDfa() {
        return dfa;
    }

    public Map<Character, Integer> getAlphabetToCounter() {
        return alphabetToCounter;
    }

    public Map<Word<Character>, Boolean> getKnownMistakes() {
        return knownMistakes;
    }
}

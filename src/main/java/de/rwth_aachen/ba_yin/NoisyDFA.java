package de.rwth_aachen.ba_yin;


import java.util.HashMap;
import java.util.Map;

import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.word.Word;

public class NoisyDFA<I> {
    private final CompactDFA<I> dfa;
    private final float mistakeProb;
    private final Map<Word<Character>, Boolean> knownMistakes;

    public NoisyDFA(CompactDFA<I> dfa, float mistakeProb) {
        this.dfa = dfa;
        this.mistakeProb = mistakeProb;
        this.knownMistakes = new HashMap<>();
    }

    public CompactDFA<I> getDfa() {
        return dfa;
    }

    public float getMistakeProb() {
        return mistakeProb;
    }

    public Map<Word<Character>, Boolean> getKnownMistakes() {
        return knownMistakes;
    }
}
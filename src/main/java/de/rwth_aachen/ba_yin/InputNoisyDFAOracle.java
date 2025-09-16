package de.rwth_aachen.ba_yin;

import de.learnlib.oracle.SingleQueryOracle;
import de.learnlib.oracle.membership.SimulatorOracle;

import java.lang.Boolean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.word.Word;

public class InputNoisyDFAOracle<I> extends SimulatorOracle<I, Boolean> implements SingleQueryOracle.SingleQueryOracleDFA<I> {
    
	private final float mistakeProb;
    private final Random random = new Random();
    private Map<Word<I>, Boolean> knownMistakes;
    Alphabet<I> alphabet;

    public InputNoisyDFAOracle(DFA<?, I> dfa, float mistakeProb, Map<Word<I>, Boolean> knownMistakes, Alphabet<I> alphabet) {
        super(dfa);
        this.mistakeProb = mistakeProb;
        this.knownMistakes = knownMistakes;
        this.alphabet = alphabet;
    }

    @Override
    public Boolean answerQuery(Word<I> prefix, Word<I> suffix) {
    	
    	Word<I> fullQuery = prefix.concat(suffix);

    	if (knownMistakes.containsKey(fullQuery)) {
            return knownMistakes.get(fullQuery);
        }else {
        	List<I> prefix2 = new ArrayList<>(prefix.length());
        	List<I> suffix2 = new ArrayList<>(prefix.length());
        	prefix.forEach(prefix2::add);
        	suffix.forEach(suffix2::add);

        	for (int i = 0; i < prefix.length(); i++) {
                if (random.nextFloat() < mistakeProb) {
                    int randomCharacter = random.nextInt(alphabet.size());
                    prefix2.set(i, alphabet.getSymbol(randomCharacter));
                }else {
                	prefix2.set(i, prefix.getSymbol(i));
                }
            }
        	for (int i = 0; i < suffix.length(); i++) {
                if (random.nextFloat() < mistakeProb) {
                    int randomCharacter = random.nextInt(alphabet.size());
                    suffix2.set(i, alphabet.getSymbol(randomCharacter));
                }else {
                	suffix2.set(i, suffix.getSymbol(i));
                }
            }
        	
        	Word<I> newPrefix = Word.fromList(prefix2);
        	Word<I> newSuffix = Word.fromList(suffix2);
        	
        	boolean result = super.answerQuery(newPrefix,newSuffix);
        	knownMistakes.put(fullQuery, result);
        	return result;
        }
    }
}
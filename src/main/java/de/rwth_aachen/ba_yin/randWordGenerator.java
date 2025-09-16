package de.rwth_aachen.ba_yin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.membership.DFASimulatorOracle;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.util.automaton.builder.AutomatonBuilders;
import net.automatalib.util.automaton.builder.DFABuilder;
import net.automatalib.word.Word;

public class randWordGenerator {
	 private static final Random random = new Random();
	 	
	 
	 public static <I> List<Word<I>> randomWords(int sampleNum, Alphabet<I> alphabet, double wordLenParam) {
	    List<Word<I>> words = new ArrayList<>(sampleNum);
	    
	    for (int i = 0; i < sampleNum; i++) {
	    	words.add(randomWord(alphabet, wordLenParam));
	    }
	    return words;	
	 }
	 
	 public static <I> Word<I> randomWord(Alphabet<I> alphabet, double wordLenParam) {
		 List<I> word = new ArrayList<>();
		 int alphabetSize = alphabet.size();
		 
		 while (true) {
			 int nextLetter = random.nextInt(alphabetSize);
			 word.add(alphabet.getSymbol(nextLetter));
			 if (random.nextFloat() < wordLenParam) {
				 break;
			 }
		 }
		 Word<I> newWord = Word.fromList(word);
		 return newWord;
	 }	
	 
	 public static <I> double distanceCalculator(CompactDFA<Character> dfa1, DFAMembershipOracle<Character> oracle2, double wordLengthParam, double delta, double epsilon,List<Word<Character>> samples){
		 int n = (int) (Math.ceil(Math.log(2.0 / delta) / (2 * epsilon * epsilon)) + 1);
 
		 if (samples == null) {
			 samples = randomWords(n, dfa1.getInputAlphabet(), 0.01);
			 }
		 int mistakes = 0;	

		 DFAMembershipOracle<Character> oracle1 = new DFASimulatorOracle<>(dfa1);
		 for (Word<Character> word : samples) {	
			 boolean in1 = oracle1.answerQuery(word);
			 boolean in2 = oracle2.answerQuery(word);
			 if (in1 != in2) {
				 mistakes++;
			 }
		 }

		 double distance = (double) mistakes / n;
		 return distance;
	 }
	 
	 public static <I> double distanceCalAAE(CompactDFA<Character> dfa1, DFA<Object, Character> result, double wordLengthParam, double delta,double epsilon,List<Word<Character>> samples){
		 Object[] states = result.getStates().toArray();
		 Object initState = result.getInitialState();
		 List<Object> finalStates = new ArrayList<>();
		 Alphabet<Character> sigma = dfa1.getInputAlphabet();
		 // take care of experiments speed
		 DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder0 builder1 = AutomatonBuilders.newDFA(sigma).withInitial(initState);
		 DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder6 builder2 = null;
		 for (Character letter : sigma) {
			 Object nextState = result.getTransition(initState, letter);
			 builder2 = builder1.from(initState).on(letter).to(nextState);
		 }
		 
		 for (Object state : states) {
			 if (state != initState) {
				 for (Character letter : sigma) {
					 Object nextState = result.getTransition(state, letter);
					 builder2 = builder1.from(state).on(letter).to(nextState);
				 }
			 }
			 boolean finalS = result.getStateProperty(state);
			 if(finalS == true) {
				 finalStates.add(state);
			 }
		 }
		 
		 DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder0 builder3 = builder2.withAccepting(finalStates.get(0));
		 for(int i = 1; i < finalStates.size(); i++) {
			 builder3 = builder3.withAccepting(finalStates.get(i));
		 }
		 
		 CompactDFA<Character> fromDFA = builder3.create();
		 
		 DFAMembershipOracle<Character> oracle1 = new DFASimulatorOracle<>(fromDFA);
		 double distance = distanceCalculator(dfa1, oracle1, 0.01, 0.01, 0.01, null);
		 return distance;
	 }
	 
	 public static double[] compareDFAs(CompactDFA<Character> dfa1, DFAMembershipOracle<Character> oracle2, DFA<Object, Character> result, double delta,double epsilon,List<Word<Character>> samples) {		 
		 double distance1_2 = distanceCalculator(dfa1, oracle2, 0.01, delta, epsilon, null);
		 double distance1_3 = distanceCalAAE(dfa1, result, 0.01, delta, epsilon, null);
		 double[] results = new double[] {distance1_2, distance1_3}; 
		 return results;
	 }
	 
}

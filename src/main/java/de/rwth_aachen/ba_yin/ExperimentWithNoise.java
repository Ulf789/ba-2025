package de.rwth_aachen.ba_yin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFA;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFABuilder;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFA;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFABuilder;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.util.automaton.builder.AutomatonBuilders;
import net.automatalib.util.automaton.fsa.DFAs;

public class ExperimentWithNoise {
	private static final int EXPLORATION_DEPTH = 2;
	public static void experimentOutputL(List<CompactDFA<Character>> dfaList){		
				
		//set four different mistake probability
		float[]  mistakeProbs = {0.01f, 0.005f, 0.0025f, 0.0015f, 0.001f};
		//maxRounds of experiments
		int maxRounds = 5;
		
		//values of delta and epsilon
		double deltaEpsilon = 0.01;
		
		String[] title = {"p", "d(L(A),L(Mn))", "d(L(A),L(Ae))", "gain", "deviation"};
		
		for (String value : title) {
	        System.out.printf("%-15.13s", value);
	    }
		System.out.println();
		for (int i = 0; i < mistakeProbs.length; i++) {
			double results[] = new double[5];
			double finalamn = 0;
			double finalaae = 0;
			double finalgain = 0;
			double[] aae = new double[50];
			int count = 0;
	        float mistakeProb = mistakeProbs[i];
	        for (CompactDFA<Character> dfa : dfaList) {

	    		Map<Character, Integer> alphabetToCounter = new HashMap<>();
	    		
	    		Random rand = new Random();
	    		for (Character symbol : dfa.getInputAlphabet()) {
	    		    if (rand.nextInt(dfa.getInputAlphabet().size()) <= dfa.getInputAlphabet().size() / 4) {
	    		        alphabetToCounter.put(symbol, -1);
	    		    } else {
	    		    	alphabetToCounter.put(symbol, rand.nextInt(6));
	    		    }
	    		}
	        	
	        	NoisyDFA<Character> newNoisyDFA = new NoisyDFA<>(dfa, mistakeProb);
	        	CounterDFA<Character> newCounterNoisyDFA = new CounterDFA<>(dfa, 0.1f, alphabetToCounter);
	        	DFAMembershipOracle<Character> oracleO = new OutputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes());
	        	DFAMembershipOracle<Character> oracleI = new InputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet());
	        	DFAMembershipOracle<Character> oracleC = new CounterNoisyDFAOracle<>(newCounterNoisyDFA.getDfa(),newCounterNoisyDFA.getMistakeProb(), newCounterNoisyDFA.getAlphabetToCounter(), newCounterNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet(), 0);
	        	DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(oracleC);
	        	
	        	ClassicLStarDFA<Character> lstar =
	                    new ClassicLStarDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet())
	                                                           .withOracle(mqOracle)
	                                                           .create();
	        	
			    DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
			    
			    Alphabet<Character> inputs = dfa.getInputAlphabet();
			    
			    MRDFAExperiment<Character> experiment = new MRDFAExperiment<>(lstar, wMethod, inputs, maxRounds);
			    experiment.runWithoutOutput();
			    
			    DFA<Object, Character> result= (DFA<Object, Character>) experiment.getFinalHypothesis();
			    double[] finalDistance = randWordGenerator.compareDFAs(dfa, oracleC, result, deltaEpsilon, deltaEpsilon, null);
			    finalamn = finalamn + finalDistance[0];
			    finalaae = finalaae + finalDistance[1];
			    aae[count] = finalDistance[1];
			    count++;
			    if (finalDistance[1] != 0) {
			    	finalgain += finalDistance[0]/finalDistance[1];
		        }else finalgain += 1;
	            }
	        results[0] = mistakeProb;
		    results[1] = finalamn / 50;
		    results[2] = finalaae / 50;
		    results[3] = finalamn / finalaae;
		    for (int j = 0; j < count; j++) {
		    	results[4] += (aae[j] - results[2])*(aae[j] - results[2]);
		    }
		    results[4] = results[4] / count;
		    for (double value : results) {
		        System.out.printf("%-15.8f", value);
		    }
		    System.out.println();
			}
		}
	
	
	
	
	public static void experimentOutputOP(List<CompactDFA<Character>> dfaList){		
				
		//set four different mistake probability
		float[]  mistakeProbs = {0.01f, 0.005f, 0.0025f, 0.0015f, 0.001f};
		//maxRounds of experiments
		int maxRounds = 320;
		
		//values of delta and epsilon
		double deltaEpsilon = 0.01;
		
		String[] title = {"p", "d(L(A),L(Mn))", "d(L(A),L(Ae))", "gain", "deviation"};
		
		for (String value : title) {
	        System.out.printf("%-15.13s", value);
	    }
		System.out.println();
		for (int i = 0; i < mistakeProbs.length; i++) {
			double results[] = new double[5];
			double finalamn = 0;
			double finalaae = 0;
			double finalgain = 0;
			double[] aae = new double[50];
			int count = 0;
	        float mistakeProb = mistakeProbs[i];
	        for (CompactDFA<Character> dfa : dfaList) {

	    		Map<Character, Integer> alphabetToCounter = new HashMap<>();
	    		
	    		Random rand = new Random();
	    		for (Character symbol : dfa.getInputAlphabet()) {
	    		    if (rand.nextInt(dfa.getInputAlphabet().size()) <= dfa.getInputAlphabet().size() / 4) {
	    		        alphabetToCounter.put(symbol, -1);
	    		    } else {
	    		    	alphabetToCounter.put(symbol, rand.nextInt(6));
	    		    }
	    		}
	        	
	        	NoisyDFA<Character> newNoisyDFA = new NoisyDFA<>(dfa, mistakeProb);
	        	CounterDFA<Character> newCounterNoisyDFA = new CounterDFA<>(dfa, 0.1f, alphabetToCounter);
	        	DFAMembershipOracle<Character> oracleO = new OutputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes());
	        	DFAMembershipOracle<Character> oracleI = new InputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet());
	        	DFAMembershipOracle<Character> oracleC = new CounterNoisyDFAOracle<>(newCounterNoisyDFA.getDfa(),newCounterNoisyDFA.getMistakeProb(), newCounterNoisyDFA.getAlphabetToCounter(), newCounterNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet(), 0);
	        	DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(oracleO);
	        	
	        	OPLearnerDFA<Character> op =
	        			new OPLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet())
	        			.withOracle(mqOracle)
	        			.create();
	        	
			    DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
			    
			    Alphabet<Character> inputs = dfa.getInputAlphabet();
			    
			    MRDFAExperiment<Character> experiment = new MRDFAExperiment<>(op, wMethod, inputs, maxRounds);
			    experiment.runWithoutOutput();
			    
			    DFA<Object, Character> result= (DFA<Object, Character>) experiment.getFinalHypothesis();
			    double[] finalDistance = randWordGenerator.compareDFAs(dfa, oracleO, result, deltaEpsilon, deltaEpsilon, null);
			    finalamn = finalamn + finalDistance[0];
			    finalaae = finalaae + finalDistance[1];
			    aae[count] = finalDistance[1];
			    count++;
			    if (finalDistance[1] != 0) {
			    	finalgain += finalDistance[0]/finalDistance[1];
		        }else finalgain += 1;
	            }
	        results[0] = mistakeProb;
		    results[1] = finalamn / 50;
		    results[2] = finalaae / 50;
		    results[3] = finalamn / finalaae;
		    for (int j = 0; j < count; j++) {
		    	results[4] += (aae[j] - results[2])*(aae[j] - results[2]);
		    }
		    results[4] = results[4] / count;
		    for (double value : results) {
		        System.out.printf("%-15.8f", value);
		    }
		    System.out.println();
			}
		}
	
	public static void experimentOutputTTT(List<CompactDFA<Character>> dfaList){			
		//set four different mistake probability
		float[]  mistakeProbs = {0.01f, 0.005f, 0.0025f, 0.0015f, 0.001f};
		//maxRounds of experiments
		int maxRounds = 320;
		
		//values of delta and epsilon
		double deltaEpsilon = 0.01;
		
		String[] title = {"p", "d(L(A),L(Mn))", "d(L(A),L(Ae))", "gain", "deviation"};
		
		for (String value : title) {
	        System.out.printf("%-15.13s", value);
	    }
		System.out.println();
		for (int i = 0; i < mistakeProbs.length; i++) {
			double results[] = new double[5];
			double finalamn = 0;
			double finalaae = 0;
			double finalgain = 0;
			double[] aae = new double[50];
			int count = 0;
	        float mistakeProb = mistakeProbs[i];
	        for (CompactDFA<Character> dfa : dfaList) {

	    		Map<Character, Integer> alphabetToCounter = new HashMap<>();
	    		
	    		Random rand = new Random();
	    		for (Character symbol : dfa.getInputAlphabet()) {
	    		    if (rand.nextInt(dfa.getInputAlphabet().size()) <= dfa.getInputAlphabet().size() / 4) {
	    		        alphabetToCounter.put(symbol, -1);
	    		    } else {
	    		    	alphabetToCounter.put(symbol, rand.nextInt(6));
	    		    }
	    		}
	        	
	        	NoisyDFA<Character> newNoisyDFA = new NoisyDFA<>(dfa, mistakeProb);
	        	CounterDFA<Character> newCounterNoisyDFA = new CounterDFA<>(dfa, 0.1f, alphabetToCounter);
	        	DFAMembershipOracle<Character> oracleO = new OutputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes());
	        	DFAMembershipOracle<Character> oracleI = new InputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet());
	        	DFAMembershipOracle<Character> oracleC = new CounterNoisyDFAOracle<>(newCounterNoisyDFA.getDfa(),newCounterNoisyDFA.getMistakeProb(), newCounterNoisyDFA.getAlphabetToCounter(), newCounterNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet(), 0);
	        	DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(oracleO);
	        	
	        	TTTLearnerDFA<Character> ttt =
	                    new TTTLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet()) 
	                    .withOracle(mqOracle)
	                    .create();
	        	
			    DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
			    
			    Alphabet<Character> inputs = dfa.getInputAlphabet();
			    
			    MRDFAExperiment<Character> experiment = new MRDFAExperiment<>(ttt, wMethod, inputs, maxRounds);
			    experiment.runWithoutOutput();
			    
			    DFA<Object, Character> result= (DFA<Object, Character>) experiment.getFinalHypothesis();
			    double[] finalDistance = randWordGenerator.compareDFAs(dfa, oracleO, result, deltaEpsilon, deltaEpsilon, null);
			    finalamn = finalamn + finalDistance[0];
			    finalaae = finalaae + finalDistance[1];
			    aae[count] = finalDistance[1];
			    count++;
			    if (finalDistance[1] != 0) {
			    	finalgain += finalDistance[0]/finalDistance[1];
		        }else finalgain += 1;
	            }
	        results[0] = mistakeProb;
		    results[1] = finalamn / 50;
		    results[2] = finalaae / 50;
		    results[3] = finalamn / finalaae;
		    for (int j = 0; j < count; j++) {
		    	results[4] += (aae[j] - results[2])*(aae[j] - results[2]);
		    }
		    results[4] = results[4] / count;
		    for (double value : results) {
		        System.out.printf("%-15.8f", value);
		    }
		    System.out.println();
			}
		}
}

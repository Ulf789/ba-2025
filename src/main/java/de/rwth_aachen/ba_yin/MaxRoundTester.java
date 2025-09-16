package de.rwth_aachen.ba_yin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.learnlib.algorithm.LearningAlgorithm;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFA;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFABuilder;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFA;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFABuilder;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.EquivalenceOracle;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
import de.learnlib.query.DefaultQuery;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;

public class MaxRoundTester<I> {
	//length of counterexample's prefix <= EXPLORATION_DEPTH
	private static final int EXPLORATION_DEPTH = 2;
	public static void maxRoundTester(){
		
		//generate five random DFAs
		List<CompactDFA<Character>> dfaList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			dfaList.add(randDFA.constructSUL()); 
		}
		
		//set four different mistake probability
		float[] mistakeProbs = {0.005f, 0.0025f, 0.0015f, 0.001f};
		
		//maxRounds of experiments
		int maxRounds = 500;
		
		int recordround = 20;
		
		//for every mistake probability
		for (float mistakeProb : mistakeProbs) {
			List<List<Double>> resultTable = new ArrayList<>();
			
			//for every DFA every 20 rounds check distance
			for (CompactDFA<Character> dfa : dfaList) {
				NoisyDFA<Character> newNoisyDFA = new NoisyDFA<>(dfa, mistakeProb);
				DFAMembershipOracle<Character> oracleO = new OutputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes());
	        	DFAMembershipOracle<Character> oracleI = new InputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes(), dfa.getInputAlphabet());
				DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(oracleI);
				
				// construct OP instance
		        OPLearnerDFA<Character> op =
		                new OPLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet()) // input alphabet
		                                                       .withOracle(mqOracle) // membership oracle with counting
		                                                       .create();
		        // construct L* instance
		        ClassicLStarDFA<Character> lstar =
		                new ClassicLStarDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet()) // input alphabet
		                                                       .withOracle(mqOracle) // membership oracle
		                                                       .create();
		        // construct TTT instance
		        TTTLearnerDFA<Character> ttt1 =
		                new TTTLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet()) // input alphabet
		                                                       .withOracle(mqOracle) // membership oracle
		                                                       .create();
		        
		        
		        //set equivalence query oracle
		        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
		        
		        //set experiment
		        List<Double> distances = new ArrayList<>(4);
		        Alphabet<Character> inputs = dfa.getInputAlphabet();
		        distanceTracking(distances, recordround, maxRounds, dfa, op, wMethod, inputs);
		        resultTable.add(distances);
			}
			
			//print table
			System.out.println("---------------------------    Table for p = " + mistakeProb + "    --------------------------");
			System.out.printf("%-10s", "");

			for (int i=20; i <= maxRounds; i = i + recordround) {
				//left align
				System.out.printf("%-15s\t", i + "rounds    ");
			}
			System.out.println();
			for (int i = 0; i < 5; i++) {
		        System.out.printf("%-10s", "DFA " + (i+1) + ": ");
		        for (double d : resultTable.get(i)) {
		        	//keep 8 decimal places
		            System.out.printf("%-15.8f\t", d);
		        }
		        System.out.println();
		    }
			
			int numRows = resultTable.size();
			int numCols = resultTable.get(0).size();

			List<Double> avg = new ArrayList<>();

			for (int col = 0; col < numCols; col++) {
			    double sum = 0.0;
			    for (int row = 0; row < numRows; row++) {
			        sum += resultTable.get(row).get(col);
			    }
			    avg.add(sum / numRows);
			}
			System.out.printf("%-10s", "avg");
			for (double avgs : avg) {
	        	//keep 8 decimal places
	            System.out.printf("%-15.8f\t", avgs);
	        }
			System.out.println();
		}
	}
	
	public static void accuracyTester(){
		//generate 35 random DFAs
		List<CompactDFA<Character>> dfaList = new ArrayList<>();
		for (int i = 0; i < 35; i++) {
			dfaList.add(randDFA.constructSUL());
		}
				
		//set four different mistake probability
		float[] mistakeProbs = {0.01f, 0.005f, 0.0025f, 0.0015f, 0.001f};
		//maxRounds of experiments
		int maxRounds = 200;
		
		//values of delta and epsilon
		double[] deltaEpsilon = {0.05, 0.01, 0.005, 0.001};
		
		double[][] results = new double[mistakeProbs.length + 1][deltaEpsilon.length + 1];
		
		for (int i = 0; i < mistakeProbs.length; i++) {
	        float mistakeProb = mistakeProbs[i];
	        
	        for (int j = 0; j < deltaEpsilon.length; j++) {
	            double eps = deltaEpsilon[j];
	            double totalGain = 0;

	            for (CompactDFA<Character> dfa : dfaList) {
	            	double gain = 0;
	            	NoisyDFA<Character> newNoisyDFA = new NoisyDFA<>(dfa, mistakeProb);
					DFAMembershipOracle<Character> oracle = new OutputNoisyDFAOracle<>(dfa, mistakeProb, newNoisyDFA.getKnownMistakes());
					DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(oracle);
					
					ClassicLStarDFA<Character> lstar =
		                    new ClassicLStarDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet())
		                                                           .withOracle(mqOracle)
		                                                           .create();
					
			        OPLearnerDFA<Character> op =
			                new OPLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet())
			                                                       .withOracle(mqOracle)
			                                                       .create();
			        
			        TTTLearnerDFA<Character> ttt =
		                    new TTTLearnerDFABuilder<Character>().withAlphabet(dfa.getInputAlphabet()) 
		                    .withOracle(mqOracle)
		                    .create();
			        
			        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
			        
			        //set experiment
			        Alphabet<Character> inputs = dfa.getInputAlphabet();
			        
			        MRDFAExperiment<Character> experiment = new MRDFAExperiment<>(op, wMethod, inputs, maxRounds);
			        experiment.runWithoutOutput();
			        
			        DFA<Object, Character> result= (DFA<Object, Character>) experiment.getFinalHypothesis();
			        double[] finalDistance = randWordGenerator.compareDFAs(dfa, oracle, result, eps, eps, null);
			        if (finalDistance[1] != 0) {
			        	totalGain += finalDistance[0]/finalDistance[1];
			        }else totalGain += 1;
			        System.out.println(totalGain);
	            }
	            results[i][j] = totalGain / dfaList.size();
	        }
		}
		System.out.printf("%15s", "p\\δ=ε");
		for (double eps : deltaEpsilon) {
		    System.out.printf("%12.4f", eps);
		}
		System.out.println();

		for (int i = 0; i < mistakeProbs.length; i++) {
		    System.out.printf("%15.4f", mistakeProbs[i]);
		    for (int j = 0; j < deltaEpsilon.length; j++) {
		        System.out.printf("%12.4f", results[i][j]);
		    }
		    System.out.println();
		}
		
	}
	
	public static <I> void distanceTracking(List<Double> distanceRecordList, int recordRound, int maxRounds,
			 					CompactDFA<Character> dfa1,
								LearningAlgorithm<? extends DFA<?, I>, I, Boolean> learningAlgorithm,
								EquivalenceOracle<? super DFA<?, I>, I, Boolean> equivalenceAlgorithm,
								Alphabet<I> inputs) {
		Counter rounds = new Counter("Learning rounds", "Round");
		
	    rounds.increment();
	    //System.out.println("Starting round " + rounds.getCount());
	    learningAlgorithm.startLearning();

	    while (true) {
	        DFA<?, I> hyp = learningAlgorithm.getHypothesisModel();

	        //System.out.println("Searching for counterexample");
	        DefaultQuery<I, Boolean> ce = equivalenceAlgorithm.findCounterExample(hyp, inputs);

	        if (ce == null) {
	            return;
	        }

	        //System.out.println("Found counterexample: " + ce.getInput());
	        //System.out.println("Refining hypothesis");
	        boolean refined = learningAlgorithm.refineHypothesis(ce);
	        assert refined;
	        
	        // every (20) rounds record
	        if ((rounds.getCount() % recordRound == 0) && (rounds.getCount() != 0)) {
	            double distance = randWordGenerator.distanceCalAAE(dfa1, (DFA<Object, Character>)learningAlgorithm.getHypothesisModel(), 0.01, 0.01, 0.01, null);
	            distanceRecordList.add(distance);
	            //System.out.println("Recorded distance at round " + rounds.getCount() + ": " + distance);
	        }

	        rounds.increment();

	        if (rounds.getCount() > maxRounds) {
	        	//System.out.println("All recorded distances: " + distanceRecordList);
	            return;
	        }
	        //System.out.println("Starting round " + rounds.getCount());
	    }
	}
}
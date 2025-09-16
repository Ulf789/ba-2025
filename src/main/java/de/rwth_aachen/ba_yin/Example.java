package de.rwth_aachen.ba_yin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;


import net.automatalib.util.automaton.builder.DFABuilder;
//L* AL
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithm.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;

//OP AL
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFA;
import de.learnlib.algorithm.observationpack.dfa.OPLearnerDFABuilder;

//TTT AL
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFA;
import de.learnlib.algorithm.ttt.dfa.TTTLearnerDFABuilder;

import de.learnlib.datastructure.discriminationtree.model.AbstractDiscriminationTree;

import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.MembershipOracle.DFAMembershipOracle;
import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
import de.learnlib.oracle.membership.DFASimulatorOracle;
import de.learnlib.util.Experiment.DFAExperiment;
import de.learnlib.util.statistic.SimpleProfiler;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.automaton.fsa.DFA;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.util.automaton.builder.AutomatonBuilders;
import net.automatalib.visualization.Visualization;
import net.automatalib.word.Word;

/**
 * This example shows the usage of a learning algorithm and an equivalence test as part of an experiment in order to
 * learn a simulated SUL (system under learning).
 */
public final class Example {

	//static List<Integer> finalstates = null;
	
    private static final int EXPLORATION_DEPTH = 2;
    
    private Example() {
        // prevent instantiation
    }

    public static  void main(String[] args) throws IOException {
    	
    	/*RandomGenerator random = RandomGenerator.getDefault();
    	
    	int a = random.nextInt(100);
    	
    	System.out.println(a);*/
    	
        // load DFA and alphabet
        //CompactDFA<Character> target = randDFA.constructSUL();
    	
    	//max rounds of experiment
    	int maxRounds = 17;
    	int maxRounds1 = 500;
    	
    	//current noisyDFA
    	CompactDFA<Character> target = newNoisyDFA().getDfa();
    	//CompactDFA<Character> target= newNoisyDFA().getDfa();
        Alphabet<Character> inputs = target.getInputAlphabet();

        // construct a simulator membership query oracle
        // input  - Character (determined by example)
        //float mistakeProb = newNoisyDFA().getMistakeProb();
        float mistakeProb = newNoisyDFA().getMistakeProb();
        //Map<Word<Character>, Boolean> mistakeMemory = newNoisyDFA().getKnownMistakes();
        Map<Word<Character>, Boolean> mistakeMemory = newNoisyDFA().getKnownMistakes();
		DFAMembershipOracle<Character> sul = new OutputNoisyDFAOracle<>(target, mistakeProb, mistakeMemory);
		DFAMembershipOracle<Character> oracleI = new InputNoisyDFAOracle<>(target, mistakeProb, mistakeMemory, target.getInputAlphabet());
        //DFAMembershipOracle<Character> sul = new OutputNoisyDFAOracle<>(target, mistakeProb, mistakeMemory);
        //DFAMembershipOracle<Character> sul = new CounterNoisyDFAOracle<>(target, mistakeProb, newCounterNoisyDFA().getAlphabetToCounter(), mistakeMemory, inputs, 0);
        //DFAMembershipOracle<Character> sul = new DFASimulatorOracle<>(target);

        // oracle for counting queries wraps SUL
        DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(sul);

        
        
        // construct L* instance
        ClassicLStarDFA<Character> lstar =
                new ClassicLStarDFABuilder<Character>().withAlphabet(inputs) // input alphabet
                                                       .withOracle(mqOracle) // membership oracle
                                                       .create();                                   
        
        // construct OP instance
        OPLearnerDFA<Character> op =
                new OPLearnerDFABuilder<Character>().withAlphabet(inputs) // input alphabet
                                                       .withOracle(mqOracle) // membership oracle
                                                       .create();
        
        
        // construct TTT instance
        TTTLearnerDFA<Character> ttt =
                new TTTLearnerDFABuilder<Character>().withAlphabet(inputs) // input alphabet
                                                       .withOracle(mqOracle) // membership oracle
                                                       .create();
        

        // construct a W-method conformance test
        // exploring the system up to depth 4 from
        // every state of a hypothesis
        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);

        
        
        // construct a learning experiment from
        // the learning algorithm and the conformance test.
        // The experiment will execute the main loop of
        // active learning with max rounds
        MRDFAExperiment<Character> experiment = new MRDFAExperiment<>(lstar, wMethod, inputs, maxRounds);
        MRDFAExperiment<Character> experiment1 = new MRDFAExperiment<>(op, wMethod, inputs, maxRounds1);
        //DFAExperiment<Character> experiment = new DFAExperiment<>(op1, wMethod, inputs);
        
        //experiment.run();
        
        
        // run experiment
        //experiment.run();
        //experiment1.run();

        // get learned model
        //DFA<Object, Character> result= (DFA<Object, Character>) experiment.getFinalHypothesis();
        //DFA<Object, Character> result1= (DFA<Object, Character>) experiment1.getFinalHypothesis();
        //CompactDFA<Character> result = (CompactDFA<Character>) experiment.getFinalHypothesis();

        //random words test
        //List<Word<Character>> samples = randWordGenerator.randomWords(3, inputs, (float) 0.01);
        //System.out.println(samples);
        
        /*
        //distance test
        DFAMembershipOracle<Character> oracle5 = new OutputNoisyDFAOracle<>(newNoisyDFA().getDfa(), newNoisyDFA().getMistakeProb(), newNoisyDFA().getKnownMistakes());
        DFAMembershipOracle<Character> oracle4 = new InputNoisyDFAOracle<>(newNoisyDFA().getDfa(), newNoisyDFA().getMistakeProb(), newNoisyDFA().getKnownMistakes(), target.getInputAlphabet());
        DFAMembershipOracle<Character> oracle2 = new CounterNoisyDFAOracle<>(newCounterNoisyDFA().getDfa(), newCounterNoisyDFA().getMistakeProb(), newCounterNoisyDFA().getAlphabetToCounter(), newCounterNoisyDFA().getKnownMistakes(), target.getInputAlphabet(), 0);
        DFAMembershipOracle<Character> oracle3 = new DFASimulatorOracle<>(target);
        double distance = randWordGenerator.distanceCalculator(target, oracle5, 0.01, 0.01, 0.01, null);
        
        //information gain
        double[] finalDistance = randWordGenerator.compareDFAs(target, oracle5, result, 0.01, 0.01, null);
        System.out.println(distance);
        System.out.println(Arrays.toString(finalDistance));
        System.out.println(finalDistance[0]/finalDistance[1]);
        
        
        /* Object states = result.getInitialState();
        Object states1 = result.getStates();
        Object[] states2 = result.getInitialStates().toArray();
        /*for (Object state : states1) {
            System.out.println("state: " + state + "final?" + result.getStateProperty(state));
        }*/
        //System.out.println(states1);
        //Visualization.visualize(target, inputs);
        //Visualization.visualize(result, inputs);
        
        
        //MaxRoundTester
        //
        MaxRoundTester.maxRoundTester();
        //AccuracyTester
        //MaxRoundTester.accuracyTester();      
        
        
        // report results lstar
        //System.out.println(finalstates);
        /*System.out.println("-------------------------------------------------------");

        // profiling
        SimpleProfiler.logResults();

        // learning statistics
        System.out.println(experiment.getRounds().getSummary());
        System.out.println(mqOracle.getStatisticalData().getSummary());

        // model statistics
        System.out.println("States: " + result.size());
        System.out.println("Sigma: " + inputs.size());

        // show model
        Visualization.visualize(result, inputs);

        System.out.println("-------------------------------------------------------");

        //show final observation table
        System.out.println("Final observation table:");
        new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);
        */
        
        //show final discrimination tree for OP algorithm
        //Visualization.visualize(op.getDiscriminationTree());
        //Visualization.visualize(ttt.getHypothesisDS());
        
        
        //show final observation table
        /*System.out.println("Final observation table:");
        new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);*/

        //System.out.println(lstar.getObservationTable().getAllPrefixes().size());
        //System.out.println(lstar.getObservationTable().getSuffixes().size());
        //System.out.println(inputs);
        //System.out.println(result.size());
        //System.out.println(result1.size());
        
        
        //new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);
        
        /*//generate 50 random DFAs
        List<CompactDFA<Character>> dfaList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
        	dfaList.add(randDFA.constructSUL());
        }
        ExperimentWithNoise.experimentOutputL(dfaList);
        ExperimentWithNoise.experimentOutputOP(dfaList);
        ExperimentWithNoise.experimentOutputTTT(dfaList);*/
        
        /*
        //Alphabet<Character> sigma = Alphabets.characters('a', 'b', 'c', 'd');
        List<Character> chars = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h','i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y','z');
        //Alphabet<Character> sigma = Alphabets.fromList(chars);
        
        //create randomGenerator
        RandomGenerator random = RandomGenerator.getDefault();
        
        int alphabetsize = random.nextInt(3, 21);
        
        Alphabet<Character> sigma = Alphabets.fromList(chars.subList(0, alphabetsize));
        
        System.out.println(alphabetsize);
        System.out.println(sigma);      
        */
    }

    /**
     * creates example from Angluin's seminal paper.
     *
     * @return example dfa
     */
    /*private static NoisyDFA<Character> constructSUL() {
        // input alphabet contains characters 'a'..'b'
        Alphabet<Character> sigma = Alphabets.characters('a', 'b');

        // create automaton
        CompactDFA<Character> dfa = AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                    .on('a').to("q1")
                    .on('b').to("q2")
                .from("q1")
                    .on('a').to("q0")
                    .on('b').to("q3")
                .from("q2")
                    .on('a').to("q3")
                    .on('b').to("q0")
                .from("q3")
                    .on('a').to("q2")
                    .on('b').to("q1")
                .withAccepting("q0")
                .create();
        
        return new NoisyDFA<>(dfa, 0.1f);
    }*/
    
    private static NoisyDFA<Character> newNoisyDFA(){
    	CompactDFA<Character> target = randDFA.constructSUL();
    	return new NoisyDFA<>(target,0.005f);
    }
    
    private static CounterDFA<Character> newCounterNoisyDFA() {
        // input alphabet contains characters 'a'..'b'
    	List<Character> chars = Arrays.asList('a', 'b', 'c', 'd');
		Alphabet<Character> sigma = Alphabets.fromList(chars);
        
        //character to integer map
        Map<Character, Integer> alphabetToCounter = new HashMap<>();
        alphabetToCounter.put('a', 1);
        alphabetToCounter.put('b', -1);
        alphabetToCounter.put('c', 2);
        alphabetToCounter.put('d', 0);

        // create automaton
        CompactDFA<Character> dfa = AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                    .on('a').to("q1")
                    .on('b').to("q2")
                .from("q1")
                    .on('a').to("q0")
                    .on('b').to("q3")
                .from("q2")
                    .on('a').to("q3")
                    .on('b').to("q0")
                .from("q3")
                    .on('a').to("q2")
                    .on('b').to("q1")
                .withAccepting("q0")
                .create();
        
        return new CounterDFA<>(dfa, 0.1f, alphabetToCounter);
    }
}

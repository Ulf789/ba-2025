package de.rwth_aachen.ba_yin;

import de.learnlib.algorithm.LearningAlgorithm;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.oracle.EquivalenceOracle;
import de.learnlib.query.DefaultQuery;
import net.automatalib.alphabet.Alphabet;
import net.automatalib.automaton.fsa.DFA;

public class MRDFAExperiment<I> {
	private final LearningAlgorithm<? extends DFA<?, I>, I, Boolean> learningAlgorithm;
    private final EquivalenceOracle<? super DFA<?, I>, I, Boolean> equivalenceAlgorithm;
    private final Alphabet<I> inputs;
    private final Counter rounds = new Counter("Learning rounds", "Round");
    private final int maxRounds;

    private DFA<?, I> finalHypothesis = null;

    public MRDFAExperiment(LearningAlgorithm<? extends DFA<?, I>, I, Boolean> learningAlgorithm,
                           EquivalenceOracle<? super DFA<?, I>, I, Boolean> equivalenceAlgorithm,
                           Alphabet<I> inputs,
                           int maxRounds) {
        this.learningAlgorithm = learningAlgorithm;
        this.equivalenceAlgorithm = equivalenceAlgorithm;
        this.inputs = inputs;
        this.maxRounds = maxRounds;
    } 

    public DFA<?, I> run() {
        rounds.increment();
        System.out.println("Starting round " + rounds.getCount());
        //create an initial hypothesis
        learningAlgorithm.startLearning();

        while (true) {
            DFA<?, I> hyp = learningAlgorithm.getHypothesisModel();

            System.out.println("Searching for counterexample");
            DefaultQuery<I, Boolean> ce = equivalenceAlgorithm.findCounterExample(hyp, inputs);
            if (ce == null) {
                finalHypothesis = hyp;
                return finalHypothesis;
            }

            System.out.println("Found counterexample: " + ce.getInput());
            
            System.out.println("Refining hypothesis");
            //refinement; true - real counterexample
            boolean refined = learningAlgorithm.refineHypothesis(ce);
            assert refined;
            rounds.increment();
            
            if (rounds.getCount() > maxRounds) {
                finalHypothesis = learningAlgorithm.getHypothesisModel();
                return finalHypothesis;
            }
            System.out.println("Starting round " + rounds.getCount());
        }
    }
    
    public DFA<?, I> runWithoutOutput() {
        rounds.increment();
        //create an initial hypothesis
        learningAlgorithm.startLearning();

        while (true) {
            DFA<?, I> hyp = learningAlgorithm.getHypothesisModel();

            DefaultQuery<I, Boolean> ce = equivalenceAlgorithm.findCounterExample(hyp, inputs);
            if (ce == null) {
                finalHypothesis = hyp;
                return finalHypothesis;
            }

            //refinement; true - real counterexample
            boolean refined = learningAlgorithm.refineHypothesis(ce);
            assert refined;
            rounds.increment();
            
            if (rounds.getCount() > maxRounds) {
                finalHypothesis = learningAlgorithm.getHypothesisModel();
                return finalHypothesis;
            }
        }
    }

    public DFA<?, I> getFinalHypothesis() {
        if (finalHypothesis == null) {
            throw new IllegalStateException("Experiment not yet run");
        }
        return finalHypothesis;
    }

    public Counter getRounds() {
        return rounds;
    }
}



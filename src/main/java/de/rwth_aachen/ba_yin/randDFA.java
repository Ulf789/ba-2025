package de.rwth_aachen.ba_yin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

import net.automatalib.alphabet.Alphabet;
import net.automatalib.alphabet.impl.Alphabets;
import net.automatalib.automaton.fsa.impl.CompactDFA;
import net.automatalib.util.automaton.builder.AutomatonBuilders;
import net.automatalib.util.automaton.builder.DFABuilder;

public class randDFA {
	
	public static CompactDFA<Character> constructSUL() {
        // input alphabet contains characters 'a'..'b'
        //Alphabet<Character> sigma = Alphabets.characters('a', 'b', 'c', 'd');
		List<Character> chars = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h','i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y','z');
		//Alphabet<Character> sigma = Alphabets.fromList(chars);
		
        //create randomGenerator
        RandomGenerator random = RandomGenerator.getDefault();
        
        int alphabetsize = random.nextInt(3, 21);
        
        Alphabet<Character> sigma = Alphabets.fromList(chars.subList(0, alphabetsize));
        
        //create random states information
        int min_states = 20;
        int max_states = 60;
        int states = random.nextInt(min_states, max_states);
        int num_of_final = random.nextInt(1, states - 1);
    	int init = random.nextInt(0, states - 1);
    	
    	//random choose final states
    	List<Integer> fstates = new ArrayList<>();
    	for (int i = 0; i <= states - 1; i++) {
    	    fstates.add(i);
    	}
    	List<Integer> finalstates = choice(fstates, num_of_final);
    	//finalstates = choice(fstates, num_of_final);
		
    	// create automaton without transitions
        DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder0 builder = AutomatonBuilders.newDFA(sigma)
                .withInitial(init);
                //.withAccepting(1,3);
        
        //builder initial
        DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder6 builder1 = null;
    	builder1 = builder.from(0).on('b').to(random.nextInt(0, states-1));
    	builder1 = builder.from(0).on('a').to(random.nextInt(0, states-1));
        
    	//create random transitions
        for (int i = 1; i <= states - 1; i++) {
    	    for (Character b : sigma){
    	    	int nextq = random.nextInt(0, states-1);
    	    	builder1 = builder1.from(i)
                .on(b).to(nextq);
    	    }
    	}
        
        //add final states to DFABuilder
        DFABuilder<Integer, Character, CompactDFA<Character>>.DFABuilder0 builder2 = builder1.withAccepting(finalstates.get(0));
        for (int i = 1; i < finalstates.size(); i++) {
            builder2 = builder2.withAccepting(finalstates.get(i));
        }
        
        //from a DFABuilder to return a DFA
        return builder2.create();
    }
    
    public static <T> List<T> choice(List<T> input, int size) {
        List<T> copy = new ArrayList<>(input);
        Collections.shuffle(copy);
        return copy.subList(0, size);
    }

}

package coursework;

import java.util.ArrayList;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;


public class EvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = tournamentSelection(); 
			Individual parent2 = tournamentSelection();

			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = twoPointCrossover(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 * Tournament Selection
	 */
	private Individual tournamentSelection() {
		
		
		ArrayList<Individual> participants = new ArrayList<Individual>();
		
		
		int k  = Parameters.tournamentSize;
		
		for (int i=0;i<k;i++)
		{
			participants.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		Individual winner = new Individual();
		for	(Individual ind: participants)
		{
			if (winner==null)
			{
				winner = ind.copy();
			}
			else if(ind.fitness<winner.fitness)
			{
				winner = ind.copy();
			}
		}
		
		return winner;
		
	}
	
	private Individual rouletteSelection()
	{
		double sumFitness=0;
		double rouletteFitness=0;
		
		for	(int i =0; i< Parameters.popSize;i++)
		{
			sumFitness+=population.get(i).fitness;
		}
		
		double roulettePos = Parameters.random.nextDouble() * sumFitness;
		
		for	(int i=0; i< Parameters.popSize; i++)
		{
			rouletteFitness += population.get(i).fitness;
			if (rouletteFitness >=roulettePos)
			{
				return population.get(i);
			}
			
		}
		System.out.print("error in rouletteSelection");
		return null;
	}

	/**
	 * Crossover / Reproduction
	 * 
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents. 
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent2.copy());			
		return children;
	} 
	
	private ArrayList<Individual> twoPointCrossover(Individual parent1, Individual parent2)
	{
		ArrayList<Individual> children = new ArrayList<>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		
		int length = parent1.chromosome.length;
		
		int point1 = Parameters.random.nextInt(length);
		int point2 = Parameters.random.nextInt(length-point1)+point1;
		
		for( int i=0; i<length; i++)
		{
			if (i <point1 || i>=point2)
			{
				child1.chromosome[i] = parent1.chromosome[i];
				child2.chromosome[i] = parent2.chromosome[i];
			}
			else {
				child1.chromosome[i] = parent2.chromosome[i];
				child2.chromosome[i] = parent1.chromosome[i];
			}
		}
		children.add(child1);
		children.add(child2);
		return children;
	}
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}

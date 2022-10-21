/*
 * Comparison of for, for-in and lambdas.
 * Copyright (C) 2022  anth64, anth64@tutanota.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Random;

final class JavaLambdaComparison
{
	// Constant for one million.
	static final int ONE_MILLION = 1000000;
	// Average time per iteration.
	static double avgIterTime;
	// Elapsed time.
	static double timeElapsed;
	// Summary variables.
	static double forGrandTotalSec, forOperationTotalSec, forAvgSec, forInGrandTotalSec,
								forInOperationTotalSec, forInAvgSec, lambdaGrandTotalSec, 
								lambdaOperationTotalSec, lambdaAvgSec;
	// Number of runs specified as command line argument.
	static int numberOfRuns = 1000;
	// Time stamps.
	static long start, end, totalStart, totalEnd;
	// Accumulted time and average iteration time.
	static long elapsedTime;
	/*
	 * A comparison of for loops, for-in loops and lambdas to measure time
	 * performance.
	 */
	public static void main(String args[])
	{
		/* 
		 * Check if the user supplies args[1];
		 * User supplies number of iterations per loop as the 1st argument.
		 */
		if(args.length >= 1) {
			// Try catch for input handling.
			try {
				// Convert string to int.
				numberOfRuns = Integer.parseInt(args[0]);
				if(numberOfRuns <= 0) {
					System.out.println("Number of runs must be greater than 0!");
					return;
				}
			} catch(NumberFormatException ex) {
				System.out.println("Invalid number entered, using default 1000.");
				System.out.println("-------------------------------------------");
			}
		}
		// Generate random numbers.
		ArrayList<Integer> numList = generateRandomNums();	
		// Declare even/odd number map.
		HashMap<Integer, Boolean> forEvenOddMap = new HashMap<Integer, Boolean>();
		// Begin total timestamp.
		totalStart = System.nanoTime();
		for(int i = 0; i < numberOfRuns; i++) {
			// Start measuring iteration time.
			start = System.nanoTime();
			// For each number, if it is even, add it to the even number list.
			for(int j = 0; j < ONE_MILLION; j++)
					forEvenOddMap.put(numList.get(j), numList.get(j) % 2 == 0);
			// Get end time.
			end = System.nanoTime();
			// Calculate elapsed time.
			elapsedTime += end - start;
			// Clear map.
			forEvenOddMap.clear();
		}
		// Mark total end timestamp.
		totalEnd = System.nanoTime();
		// Calculate average iteration time.
		avgIterTime = (double) elapsedTime / numberOfRuns;
		// Calculate summary results.
		forGrandTotalSec = 1.0e-9 * (totalEnd - totalStart);
		forOperationTotalSec = 1.0e-9 * elapsedTime;
		forAvgSec = 1.0e-9 * avgIterTime;
		// Reset iteration times for next test.
		resetIterTimes();

		// For in iteration.
		// Declare even/odd number map.
		HashMap<Integer, Boolean> forInEvenOddMap = new HashMap<Integer, Boolean>();
		// Begin timestamp.
		totalStart = System.nanoTime();
		for(int i = 0; i < numberOfRuns; i++) {
			// Start measuring iteratoin time.
			start = System.nanoTime();
			// For each number, if it is even add it to the even number list.
			for(int n : numList)
				forInEvenOddMap.put(n, n % 2 == 0);
			end = System.nanoTime(); // Get end time.
			elapsedTime += end - start; // Calculate elapsed time and accumulate.
			// Clear map.
			forInEvenOddMap.clear();
		}
		totalEnd = System.nanoTime();
		// Calculate average iteration time.
		avgIterTime = (double) elapsedTime / numberOfRuns;
		// Calculate summary results.
		forInGrandTotalSec = 1.0e-9 * (totalEnd - totalStart);
		forInOperationTotalSec = 1.0e-9 * elapsedTime;
		forInAvgSec = 1.0e-9 * avgIterTime;
		// Reset iteration times for next test.
		resetIterTimes();

		// Lambda function.
		// Declare even/odd number map.
		ConcurrentHashMap<Integer, Boolean> lambdaEvenOddMap = new ConcurrentHashMap<Integer, Boolean>();
		totalStart = System.nanoTime();
		for(int i = 0; i < numberOfRuns; i++) {
			// Start measuring iteration time.
			start = System.nanoTime();
			// Put in map if even.
			numList.parallelStream().forEach(n -> {
				lambdaEvenOddMap.put(n, n % 2 == 0);
			});
			// Get end time.
			end = System.nanoTime();
			// Add elapsed to accumulated time.
			elapsedTime += end - start;
			// Clear map.
			lambdaEvenOddMap.clear();
		}
		totalEnd = System.nanoTime();
		// Calculate average iterated time.
		avgIterTime = (double) elapsedTime / numberOfRuns;
		// Calculate summary results.
		lambdaGrandTotalSec = 1.0e-9 * (totalEnd - totalStart);
		lambdaOperationTotalSec = 1.0e-9 * elapsedTime;
		lambdaAvgSec = 1.0e-9 * avgIterTime;
		// Reset iteration times.
		resetIterTimes();

		// Print summary.
		printSummary();
	}

	static void printSummary()
	{
		String forSummary, forInSummary, lambdaSummary;
		// For loop summary.
		forSummary = String.format("--FOR--\n");
		forSummary += String.format("Grand Total: %.8fs\n", forGrandTotalSec);
		forSummary += String.format("Operation Total: %.8fs\n", forOperationTotalSec);
		forSummary += String.format("Average Iteration: %.8fs\n", forAvgSec);
		forSummary += String.format("-------");
		// For-in loop summary	
		forInSummary = String.format("--FOR-IN--\n");
		forInSummary += String.format("Grand Total: %.8fs\n", forInGrandTotalSec);
		forInSummary += String.format("Operation Total: %.8fs\n", forInOperationTotalSec);
		forInSummary += String.format("Average Iteration: %.8fs\n", forInAvgSec);
		forInSummary += String.format("----------");
		// Lambda summary.
		lambdaSummary = String.format("--LAMBDA--\n");
		lambdaSummary += String.format("Grand Total: %.8fs\n", lambdaGrandTotalSec);
		lambdaSummary += String.format("Operation Total: %.8fs\n", lambdaOperationTotalSec);
		lambdaSummary += String.format("Average Iteration: %.8fs\n", lambdaAvgSec);
		lambdaSummary += String.format("-------------");
		System.out.println(String.format("Summary of %d Iterations", numberOfRuns));
		System.out.println(forSummary);
		System.out.println(forInSummary);
		System.out.println(lambdaSummary);
		System.out.println("--------------------------");
	}

	// Resets timing variables.
	static void resetIterTimes()
	{
		avgIterTime = elapsedTime = 0;
	}

	// Generates an array list of 1 million random numbers.
	static ArrayList<Integer> generateRandomNums()
	{
		// Random number generator.
		Random rng = new Random();
		// List of numbers.
		ArrayList<Integer> numList = new ArrayList<Integer>();
		for(int i = 0; i < ONE_MILLION; i++)
			numList.add(rng.nextInt(ONE_MILLION) + 1);
		return numList;
	}
}

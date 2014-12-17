package ai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InteractiveLearner {

	//	static final int K = 1;
	static final int K = 7;

	public enum Clazz {
		GOOD, BAD;
	}

	HashMap<String, Integer> bad = new HashMap<>();
	HashMap<String, Integer> good = new HashMap<>();

	/**
	 * Tokenize input by replacing any form of whitespace with one space, changing upper case letters
	 * to lower case, removing any token which is not a number or a letter and finally splitting up the 
	 * input in words. 
	 * @param input
	 * @return
	 */
	public String[] tokenizer(String input) {
		return input
				.replaceAll("\\s+", " ")
				.toLowerCase()
				.replaceAll("[^a-z0-9 ]", "")
				.split(" ");
	}

	/**
	 * Train classifier by putting each word from the input (train set) in the corresponding hashmap
	 * (good or bad) and set its counter to 1 or increment the counter by 1 when the word is already 
	 * in the hashmap
	 * @param clazz
	 * @param input
	 */
	public void train(Clazz clazz, String input) {
		String[] tokenized = tokenizer(input);

		for (int i = 0; i < tokenized.length; i++) {
			String token = tokenized[i];

			if (clazz == Clazz.GOOD) {
				if (good.get(token) == null) {
					good.put(token, 1);
				} else {
					good.put(token, good.get(token) + 1);
				}
			} else {
				if (bad.get(token) == null) {
					bad.put(token, 1);
				} else {
					bad.put(token, bad.get(token) + 1);
				}
			}
		}
	}

	/**
	 * Load each train set into classifier and give contents of train set to train()
	 */
	public void trainRecipe() {
		File[] filesB = new File("rectrain/B").listFiles();
		File[] filesG = new File("rectrain/G").listFiles();

		for (int i = 0; i < filesB.length; i++) {
			try {
				String data = new String(Files.readAllBytes(Paths.get(filesB[i].toString())));

				train(Clazz.BAD, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < filesG.length; i++) {
			try {
				String data = new String(Files.readAllBytes(Paths.get(filesG[i].toString())));

				train(Clazz.GOOD, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Test classifier by loading test sets, calling classifyBlog() to assign a class (good or bad)
	 * and printing result
	 * @throws IOException
	 */
	public void testRecipe() throws IOException {
		File[] filesB = new File("rectest/B").listFiles();
		File[] filesG = new File("rectest/G").listFiles();

		int testCount = 0;
		int corrCount = 0;

		for (int i = 0; i < filesB.length; i++) {
			Clazz clazz = classifyRecipe(new String(Files.readAllBytes(Paths.get(filesB[i].toString()))));
			boolean corr = clazz == Clazz.BAD;
			System.out.println(String.format("%-30s %6s %5s", filesB[i].toString(), clazz, corr));
			testCount++;
			corrCount += corr ? 1 : 0;
		}

		for (int i = 0; i < filesG.length; i++) {
			Clazz clazz = classifyRecipe(new String(Files.readAllBytes(Paths.get(filesG[i].toString()))));
			boolean corr = clazz == Clazz.GOOD;
			System.out.println(String.format("%-30s %6s %5s", filesG[i].toString(), clazz, corr));
			testCount++;
			corrCount += corr ? 1 : 0;
		}

		double acc = ((double) corrCount) / ((double) testCount);
		System.out.println(String.format("Tests: %d, correct: %d, accuracy %.3f", testCount, corrCount, acc));
	}

	/**
	 * Assign class (good or bad) to data by first tokenizing the input and then comparing every word 
	 * to the words in the hashmaps good and bad and calculating the probability that it belongs to 
	 * one of these classes
	 * @param data
	 * @return Clazz
	 */
	public Clazz classifyRecipe(String data) {
		Clazz result = null;

		String[] tokenized = tokenizer(data);

		Set<String> vocab = new HashSet<>();
		vocab.addAll(bad.keySet());
		vocab.addAll(good.keySet());

		int v = vocab.size();

		int ng = 0;
		for (Integer value : good.values()) {
			ng += value;
		}

		int nb = 0;
		for (Integer value : bad.values()) {
			nb += value;
		}

		double probG = 0d;
		for (int i = 0; i < tokenized.length; i++) {
			int freqWordG = good.get(tokenized[i]) == null ? 0 : good.get(tokenized[i]);
			double probWordG = ((double) (freqWordG + K)) / ((double) (ng + v * K));
			probG += Math.log(probWordG);
		}

		double probB = 0d;
		for (int i = 0; i < tokenized.length; i++) {
			int freqWordB = bad.get(tokenized[i]) == null ? 0 : bad.get(tokenized[i]);
			double probWordB = ((double) (freqWordB + K)) / ((double) (nb + v * K));
			probB += Math.log(probWordB);
		}

		if (probG > probB) {
			result = Clazz.GOOD;
		} else {
			result = Clazz.BAD;
		}

		return result;
	}

	public static void main(String[] args) throws IOException {
		InteractiveLearner test = new InteractiveLearner();
		test.trainRecipe();
		test.testRecipe();
	}
}


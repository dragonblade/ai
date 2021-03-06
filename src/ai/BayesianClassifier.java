package ai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BayesianClassifier {

	//	static final int K = 1;
	static final int K = 7;

	public enum Clazz {
		MALE, FEMALE;
	}

	HashMap<String, Integer> female = new HashMap<>();
	HashMap<String, Integer> male = new HashMap<>();

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
	 * (male or female) and set its counter to 1 or increment the counter by 1 when the word is already 
	 * in the hashmap
	 * @param clazz
	 * @param input
	 */
	public void train(Clazz clazz, String input) {
		String[] tokenized = tokenizer(input);

		for (int i = 0; i < tokenized.length; i++) {
			String token = tokenized[i];

			if (clazz == Clazz.MALE) {
				if (male.get(token) == null) {
					male.put(token, 1);
				} else {
					male.put(token, male.get(token) + 1);
				}
			} else {
				if (female.get(token) == null) {
					female.put(token, 1);
				} else {
					female.put(token, female.get(token) + 1);
				}
			}
		}
	}

	/**
	 * Load each train set into classifier and give contents of train set to train()
	 */
	public void trainBlog() {
		File[] filesF = new File("blogtrain/F").listFiles();
		File[] filesM = new File("blogtrain/M").listFiles();

		for (int i = 0; i < filesF.length; i++) {
			try {
				String data = new String(Files.readAllBytes(Paths.get(filesF[i].toString())));

				train(Clazz.FEMALE, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < filesM.length; i++) {
			try {
				String data = new String(Files.readAllBytes(Paths.get(filesM[i].toString())));

				train(Clazz.MALE, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Test classifier by loading test sets, calling classifyBlog() to assign a class (male or female)
	 * and printing result
	 * @throws IOException
	 */
	public void testBlog() throws IOException {
		File[] filesF = new File("blogtest/F").listFiles();
		File[] filesM = new File("blogtest/M").listFiles();

		int testCount = 0;
		int corrCount = 0;

		for (int i = 0; i < filesF.length; i++) {
			Clazz clazz = classifyBlog(new String(Files.readAllBytes(Paths.get(filesF[i].toString()))));
			boolean corr = clazz == Clazz.FEMALE;
			System.out.println(String.format("%-30s %6s %5s", filesF[i].toString(), clazz, corr));
			testCount++;
			corrCount += corr ? 1 : 0;
		}

		for (int i = 0; i < filesM.length; i++) {
			Clazz clazz = classifyBlog(new String(Files.readAllBytes(Paths.get(filesM[i].toString()))));
			boolean corr = clazz == Clazz.MALE;
			System.out.println(String.format("%-30s %6s %5s", filesM[i].toString(), clazz, corr));
			testCount++;
			corrCount += corr ? 1 : 0;
		}

		double acc = ((double) corrCount) / ((double) testCount);
		System.out.println(String.format("Tests: %d, correct: %d, accuracy %.3f", testCount, corrCount, acc));
	}

	/**
	 * Assign class (male or female) to data by first tokenizing the input and then comparing every word 
	 * to the words in the hashmaps male and female and calculating the probability that it belongs to 
	 * one of these classes
	 * @param data
	 * @return Clazz
	 */
	public Clazz classifyBlog(String data) {
		Clazz result = null;

		String[] tokenized = tokenizer(data);

		Set<String> vocab = new HashSet<>();
		vocab.addAll(female.keySet());
		vocab.addAll(male.keySet());

		int v = vocab.size();

		int nm = 0;
		for (Integer value : male.values()) {
			nm += value;
		}

		int nf = 0;
		for (Integer value : female.values()) {
			nf += value;
		}

		double probM = 0d;
		for (int i = 0; i < tokenized.length; i++) {
			int freqWordM = male.get(tokenized[i]) == null ? 0 : male.get(tokenized[i]);
			double probWordM = ((double) (freqWordM + K)) / ((double) (nm + v * K));
			probM += Math.log(probWordM);
		}

		double probF = 0d;
		for (int i = 0; i < tokenized.length; i++) {
			int freqWordF = female.get(tokenized[i]) == null ? 0 : female.get(tokenized[i]);
			double probWordF = ((double) (freqWordF + K)) / ((double) (nf + v * K));
			probF += Math.log(probWordF);
		}

		if (probM > probF) {
			result = Clazz.MALE;
		} else {
			result = Clazz.FEMALE;
		}

		return result;
	}

	public static void main(String[] args) throws IOException {
		BayesianClassifier test = new BayesianClassifier();
		test.trainBlog();
		test.testBlog();
	}
}

package ai;

import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WekaClassifier {

	private String trainData;

	private String testData;

	private List<String> classes;

	private Set<String> tokens;

	private FastVector attrs;

	private NaiveBayesUpdateable classifier;

	/**
	 * Initialize classifier
	 *
	 * @param trainData name of folder with train data files
	 * @param testData  name of folder with test data files
	 * @param classes   class names
	 */
	public WekaClassifier(String trainData, String testData, List<String> classes) {
		this.trainData = trainData;
		this.testData = testData;
		this.classes = classes;
	}

	/**
	 * Build the classifier and train it with the test data set
	 *
	 * @throws Exception in case of an error
	 */
	public void buildClassifier() throws Exception {
		// Read raw data
		this.tokens = new TreeSet<>();
		Map<String, List<Map<String, Integer>>> rawData = new HashMap<>();
		for (String clazz : classes) {
			rawData.put(clazz, new ArrayList<Map<String, Integer>>());

			File[] files = new File(trainData + "/" + clazz).listFiles();
			for (File file : files) {
				String fileData = new String(Files.readAllBytes(Paths.get(file.toString())));
				Map<String, Integer> tokenMap = buildTokenMap(tokenizer(fileData));
				tokens.addAll(tokenMap.keySet());
				rawData.get(clazz).add(tokenMap);
			}
		}

		// Put rawData in Weka classes
		FastVector classVector = new FastVector();
		for (String clazz : classes)
			classVector.addElement(clazz);
		this.attrs = new FastVector();
		for (String token : tokens)
			attrs.addElement(new Attribute(token));
		attrs.addElement(new Attribute("@@class@@", classVector));
		Instances instances = new Instances("TrainInstances", attrs, 0);
		instances.setClassIndex(instances.numAttributes() - 1);

		String[] tokensArray = tokens.toArray(new String[tokens.size()]);
		for (int i = 0; i < classes.size(); i++) {
			String clazz = classes.get(i);

			for (Map<String, Integer> instanceTokenMap : rawData.get(clazz)) {
				double[] instanceValue = new double[tokens.size() + 1];

				for (int j = 0; j < tokensArray.length; j++)
					if (instanceTokenMap.get(tokensArray[j]) == null)
						instanceValue[j] = 0d;
					else
						instanceValue[j] = (double) instanceTokenMap.get(tokensArray[j]);
				instanceValue[tokens.size()] = (double) i;

				instances.add(new Instance(1d, instanceValue));
			}
		}

		// Build classifier
		classifier = new NaiveBayesUpdateable();
		classifier.buildClassifier(instances);
	}

	/**
	 * Test the classifier and output its accuracy with the test data set. The {@link #buildClassifier()} method has to
	 * have been run before running this method.
	 *
	 * @throws Exception in case of an error
	 */
	private void testClassifier() throws Exception {
		if (tokens == null || classifier == null)
			throw new RuntimeException("Classifier has not been built yet");

		int testCount = 0;
		int corrCount = 0;

		String[] tokensArray = tokens.toArray(new String[tokens.size()]);
		for (String clazz : classes) {
			File[] files = new File(testData + "/" + clazz).listFiles();

			for (File file : files) {
				String fileData = new String(Files.readAllBytes(Paths.get(file.toString())));
				Map<String, Integer> tokenMap = buildTokenMap(tokenizer(fileData));
				double[] instanceValue = new double[tokens.size() + 1];

				for (int j = 0; j < tokensArray.length; j++)
					if (tokenMap.get(tokensArray[j]) == null)
						instanceValue[j] = 0d;
					else
						instanceValue[j] = (double) tokenMap.get(tokensArray[j]);
				instanceValue[tokens.size()] = -1d;

				Instances testInstances = new Instances("TestInstances", this.attrs, 0);
				testInstances.setClassIndex(testInstances.numAttributes() - 1);
				testInstances.add(new Instance(1d, instanceValue));

				int classifIdx = (int) this.classifier.classifyInstance(testInstances.firstInstance());
				String classifClazz = classes.get(classifIdx);
				boolean corr = clazz.equals(classifClazz);
				System.out.println(String.format("%-30s %6s %5s", file.toString(), classifClazz, corr));
				testCount++;
				corrCount += corr ? 1 : 0;
			}
		}

		double acc = ((double) corrCount) / ((double) testCount);
		System.out.println(String.format("Tests: %d, correct: %d, accuracy %.3f", testCount, corrCount, acc));
	}

	/**
	 * Tokenize input string by applying the following transformations:
	 * - Replace all blocks of whitespace with a single space
	 * - Convert all characters to lower case
	 * - Remove all characters that are not alphanumerics or spaces
	 * - Split the string to tokens
	 *
	 * @param input input string
	 * @return array of tokens
	 */
	private String[] tokenizer(String input) {
		return input
				.replaceAll("\\s+", " ")
				.toLowerCase()
				.replaceAll("[^a-z0-9 ]", "")
				.split(" ");
	}

	/**
	 * Builds a frequency map from the given array of tokens
	 *
	 * @param tokens array of tokens
	 * @return frequency map
	 */
	private Map<String, Integer> buildTokenMap(String[] tokens) {
		Map<String, Integer> map = new HashMap<>();

		for (String token : tokens)
			if (map.get(token) == null)
				map.put(token, 1);
			else
				map.put(token, map.get(token) + 1);

		return map;
	}

	public static void main(String[] args) throws Exception {
		WekaClassifier classifier = new WekaClassifier("blogtrain", "blogtest", Arrays.asList("F", "M"));
		classifier.buildClassifier();
		classifier.testClassifier();
	}
}

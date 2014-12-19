package ai;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WekaFilteredClassifier {

	private Class<? extends Classifier> classifierType;

	private String[] classifierOptions;

	private String trainData;

	private String testData;

	private List<String> classes;

	private Instances instances;

	private FilteredClassifier classifier;

	/**
	 * Initialize classifier
	 *
	 * @param trainData name of folder with train data files
	 * @param testData  name of folder with test data files
	 * @param classes   class names
	 */
	public WekaFilteredClassifier(Class<? extends Classifier> classifierType, String[] classifierOptions, String trainData,
								  String testData, List<String> classes) {
		this.classifierType = classifierType;
		this.classifierOptions = classifierOptions;
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
		Map<String, List<String>> rawData = new HashMap<>();
		for (String clazz : classes) {
			rawData.put(clazz, new ArrayList<String>());

			File[] files = new File(trainData + "/" + clazz).listFiles();
			for (File file : files) {
				String fileData = new String(Files.readAllBytes(Paths.get(file.toString())));
				rawData.get(clazz).add(fileData);
			}
		}

		// Put rawData in Weka classes
		FastVector classVector = new FastVector();
		for (String clazz : classes)
			classVector.addElement(clazz);
		FastVector attrs = new FastVector();
		attrs.addElement(new Attribute("content", (FastVector) null));
		attrs.addElement(new Attribute("@@class@@", classVector));

		instances = new Instances("TrainInstances", attrs, 0);

		for (int i = 0; i < classes.size(); i++) {
			String clazz = classes.get(i);

			for (String fileData : rawData.get(clazz)) {
				double[] instanceValue = new double[2];
				instanceValue[0] = instances.attribute(0).addStringValue(fileData);
				instanceValue[1] = i;

				instances.add(new Instance(1.0, instanceValue));
			}
		}

		instances.setClassIndex(instances.numAttributes() - 1);

		// Build classifier
		System.out.println("Building classifier...");
		classifier = new FilteredClassifier();
		StringToWordVector filter = new StringToWordVector();
		filter.setAttributeIndices("first");
		classifier.setClassifier(this.classifierType.newInstance());
		classifier.getClassifier().setOptions(classifierOptions);
		classifier.setFilter(filter);
		classifier.buildClassifier(instances);
	}

	/**
	 * Test the classifier and output its accuracy with the test data set. The {@link #buildClassifier()} method has to
	 * have been run before running this method.
	 *
	 * @throws Exception in case of an error
	 */
	private void testClassifier() throws Exception {
		if (classifier == null)
			throw new RuntimeException("Classifier has not been built yet");

		int testCount = 0;
		int corrCount = 0;

		for (String clazz : classes) {
			File[] files = new File(testData + "/" + clazz).listFiles();

			for (File file : files) {
				String fileData = new String(Files.readAllBytes(Paths.get(file.toString())));

				double[] instanceValue = new double[2];
				instanceValue[0] = instances.attribute(0).addStringValue(fileData);
				instanceValue[1] = 0;
				Instance testInstance = new Instance(1.0, instanceValue);
				testInstance.setDataset(instances);

				int classifIdx = (int) this.classifier.classifyInstance(testInstance);
				String classifClazz = classes.get(classifIdx);
				boolean corr = clazz.equals(classifClazz);
				System.out.println(String.format("%-30s %6s %5s", file.toString(), classifClazz, corr));
				testCount++;
				corrCount += corr ? 1 : 0;
			}
		}

		double acc = ((double) corrCount) / ((double) testCount);
		System.out.println(String.format("Tests: %d, correct: %d, accuracy: %.3f", testCount, corrCount, acc));
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
		WekaFilteredClassifier classifier = new WekaFilteredClassifier(
				NaiveBayes.class, new String[]{},
				"blogtrain", "blogtest", Arrays.asList("F", "M"));
		classifier.buildClassifier();
		classifier.testClassifier();
	}
}

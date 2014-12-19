package ai;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractiveLearner {

	private Class<? extends Classifier> classifierType;

	private String[] classifierOptions;

	private String trainData;

	private List<String> classes;

	private Instances instances;

	private FilteredClassifier classifier;

	/**
	 * Initialize classifier
	 *
	 * @param trainData name of folder with train data files
	 * @param classes   class names
	 */
	public InteractiveLearner(Class<? extends Classifier> classifierType, String[] classifierOptions, String trainData,
							  List<String> classes) {
		this.classifierType = classifierType;
		this.classifierOptions = classifierOptions;
		this.trainData = trainData;
		this.classes = classes;
	}

	/**
	 * Build the train data set into Weka datastructures
	 *
	 * @throws Exception in case of an error
	 */
	public void buildTrainInstances() throws Exception {
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

		instances = new Instances("Instances", attrs, 0);

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
	}

	/**
	 * Build the classifier based on the train dataset
	 *
	 * @throws Exception if an error has occured
	 */
	public void buildClassifier() throws Exception {
		if (instances == null)
			throw new RuntimeException("Train dataset has not been processed");

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
	 * Classify a document and return the expected class of the document
	 *
	 * @param document document to classify
	 * @return
	 * @throws Exception in case of an error
	 */
	public String classifyDocument(String document) throws Exception {
		if (classifier == null)
			throw new RuntimeException("Classifier has not been built");

		double[] instanceValue = new double[2];
		instanceValue[0] = instances.attribute(0).addStringValue(document);
		instanceValue[1] = 0;
		Instance testInstance = new Instance(1.0, instanceValue);
		testInstance.setDataset(instances);

		int result = (int) classifier.classifyInstance(testInstance);
		return this.classes.get(result);
	}

	public void learnDocument(String document, String clazz) throws Exception {
		if (classifier == null)
			throw new RuntimeException("Classifier has not been built");
		if (classes.indexOf(clazz) == -1)
			throw new RuntimeException("Cannot find specified class");

		double[] instanceValue = new double[2];
		instanceValue[0] = instances.attribute(0).addStringValue(document);
		instanceValue[1] = classes.indexOf(clazz);

		instances.add(new Instance(1.0, instanceValue));
		buildClassifier();
	}
}

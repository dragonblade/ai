package ai;

import weka.classifiers.bayes.NaiveBayes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

public class InteractiveLearnerGUI extends JFrame implements ActionListener {
	private JTextArea taRecipe;
	private JButton bClassify;
	private JButton bYes;
	private JButton bNo;
	private JLabel lbResult;
	private JLabel lbQuestion;

	private InteractiveLearner classifier;

	public InteractiveLearnerGUI() {
		try {
			classifier = new InteractiveLearner(NaiveBayes.class, new String[]{}, "rectrain", Arrays.asList("B", "G"));
			classifier.buildTrainInstances();
			classifier.buildClassifier();

			buildGUI();
			pack();
			this.setLocationRelativeTo(null);
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not start GUI");
		}
	}

	public void buildGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		p1.setAlignmentX(LEFT_ALIGNMENT);

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lbRecipe = new JLabel("Recipe:");
		lbRecipe.setAlignmentX(LEFT_ALIGNMENT);
		taRecipe = new JTextArea(30, 80);
		taRecipe.setAlignmentX(LEFT_ALIGNMENT);

		bClassify = new JButton("Classify recipe");
		bClassify.setAlignmentX(LEFT_ALIGNMENT);
		bClassify.addActionListener(this);

		lbResult = new JLabel("");
		lbResult.setAlignmentX(LEFT_ALIGNMENT);
		lbResult.setVisible(false);

		lbQuestion = new JLabel("Do you like this recipe?");
		bYes = new JButton("Yes");
		bYes.setAlignmentX(LEFT_ALIGNMENT);
		bYes.addActionListener(this);
		bNo = new JButton("No");
		bNo.setAlignmentX(LEFT_ALIGNMENT);
		bNo.addActionListener(this);
		lbQuestion.setVisible(false);
		bYes.setVisible(false);
		bNo.setVisible(false);

		p1.add(lbRecipe);
		p1.add(Box.createRigidArea(new Dimension(2, 10)));
		p1.add(taRecipe);
		p1.add(Box.createRigidArea(new Dimension(2, 10)));
		p1.add(bClassify);
		p1.add(Box.createRigidArea(new Dimension(2, 10)));
		p1.add(lbResult);
		p2.add(Box.createRigidArea(new Dimension(2, 10)));
		p2.add(lbQuestion);
		p2.add(Box.createRigidArea(new Dimension(2, 10)));
		p2.add(bYes);
		p2.add(Box.createRigidArea(new Dimension(2, 10)));
		p2.add(bNo);

		Container cc = getContentPane();
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));
		cc.add(p1);
		cc.add(Box.createRigidArea(new Dimension(2, 50)));
		cc.add(p2);
	}

	public void resetGUI() {
		taRecipe.setText("");
		lbResult.setText("");
		lbQuestion.setVisible(false);
		bYes.setVisible(false);
		bNo.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bClassify) {
			try {
				String clazz = classifier.classifyDocument(taRecipe.getText());
				if ("G".equals(clazz))
					lbResult.setText("I think you would like this recipe");
				else
					lbResult.setText("I think you would would not like this recipe");

				lbResult.setVisible(true);
				lbQuestion.setVisible(true);
				bYes.setVisible(true);
				bNo.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Could not classify document");
			}
		}

		if (e.getSource() == bYes) {
			try {
				classifier.learnDocument(taRecipe.getText(), "G");

				resetGUI();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Could not learn document");
			}
		}

		if (e.getSource() == bNo) {
			try {
				classifier.learnDocument(taRecipe.getText(), "B");

				resetGUI();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Could not learn document");
			}
		}

	}

	public static void main(String[] args) throws IOException {
		InteractiveLearnerGUI gui = new InteractiveLearnerGUI();
	}
}

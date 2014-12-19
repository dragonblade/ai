package ai;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

public class InteractiveLearnerGUI extends JFrame implements ActionListener {
	private JTextArea taRecipe;
	private JButton bClassify;
	private JButton bYes;
	private JButton bNo;
	private JLabel lbResult;
	private JLabel lbQuestion;

	private InteractiveLearner il;

	public InteractiveLearnerGUI() {
		il = new InteractiveLearner();
		buildGUI();
		pack();
		this.setLocationRelativeTo(null);
		setVisible(true);
	}

	public void buildGUI() {
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
		p1.add(Box.createRigidArea(new Dimension(2,10)));
		p1.add(taRecipe);
		p1.add(Box.createRigidArea(new Dimension(2,10)));
		p1.add(bClassify);
		p1.add(Box.createRigidArea(new Dimension(2,10)));
		p1.add(lbResult);
		p2.add(Box.createRigidArea(new Dimension(2,10)));
		p2.add(lbQuestion);
		p2.add(Box.createRigidArea(new Dimension(2,10)));
		p2.add(bYes);
		p2.add(Box.createRigidArea(new Dimension(2,10)));
		p2.add(bNo);

		Container cc = getContentPane();
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));
		cc.add(p1);
		cc.add(Box.createRigidArea(new Dimension(2,50)));
		cc.add(p2);
	}

	public void resetGUI() {
		taRecipe.setText("");
		lbQuestion.setVisible(false);
		bYes.setVisible(false);
		bNo.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bClassify) {
			// De tekst die in taRecipe staat moet door de classifier heen
			// gehaald worden

			lbQuestion.setVisible(true);
			bYes.setVisible(true);
			bNo.setVisible(true);
		}

		if (e.getSource() == bYes) {
			// Recipe in taRecipe opslaan als bestand in de trainmap onder
			// submap G

			resetGUI();
		}

		if (e.getSource() == bNo) {
			// Recipe in taRecipe opslaan als bestand in de trainmap onder
			// submap B

			resetGUI();
		}

	}

	public static void main(String[] args) throws IOException {
		InteractiveLearnerGUI gui = new InteractiveLearnerGUI();
	}
}

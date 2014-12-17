package ai;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

public class InteractiveLearnerGUI extends JFrame {
	private JTextArea taRecipe;
	
	public InteractiveLearnerGUI() {
		buildGUI();
		setVisible(true);
	}
	
	public void buildGUI() {
		setSize(600,500);
		
        JPanel p = new JPanel(new FlowLayout());
        p.setLayout(new FlowLayout());

        JLabel lbRecipe = new JLabel("Recipe:");
        taRecipe = new JTextArea("", 20, 50);
        
        JButton bClassify = new JButton("Classify recipe");
        
        JLabel lbResult = new JLabel("");
        lbResult.setVisible(false);
        
        JLabel lbQuestion = new JLabel("Do you like this recipe?");
        JButton bYes = new JButton("Yes");
        JButton bNo = new JButton("No");
        lbQuestion.setVisible(false);
        bYes.setVisible(false);
        bNo.setVisible(false);
       
        p.add(lbRecipe);
        p.add(taRecipe);
        p.add(bClassify);
        p.add(lbResult);
        p.add(lbQuestion);
        p.add(bYes);
        p.add(bNo);
        
        Container cc = getContentPane();
        cc.setLayout(new FlowLayout());
        cc.add(p);
	}
	
	public static void main(String[] args) throws IOException {
		InteractiveLearnerGUI gui = new InteractiveLearnerGUI();
	}
}

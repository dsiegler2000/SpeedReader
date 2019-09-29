package graphics;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class DemoScreen extends JFrame {

	private static final long serialVersionUID = -221001978556368880L;

	public static final String DEMO_TEXT_PATH = "demos/";
	public static final int NUM_DEMOS = 1;

	private static boolean initialized = false;

	public static String[] DEMO_TEXTS = new String[NUM_DEMOS];
	public static List<List<String>> DEMO_TEXTS_QUESTIONS = new ArrayList<>();
	public static List<List<String>> DEMO_TEXTS_ANSWERS = new ArrayList<>();

	private static int textOn;

	private DemoScreen() {
	}

	public static void startDemo() {

		if (!initialized) {

			init();

		}

		JOptionPane.showConfirmDialog(null, "Read this text and answer the questions the best you can",
				"Speed Reading is Cool", JOptionPane.PLAIN_MESSAGE);

		textOn = 0;

		nextText();

	}

	public static void nextText() {

		@SuppressWarnings("unused")
		SpeedReadingScreen c = new SpeedReadingScreen(DEMO_TEXTS[textOn], HomeScreen.WORDS_PER_MINUTE);

	}

	public static void nextQuestions() {
		
		if(textOn >= NUM_DEMOS){
			
			return;
			
		}
		
		float numCorrect = 0;
		float numTotal = DEMO_TEXTS_QUESTIONS.get(textOn).size();

		for (int j = 0; j < DEMO_TEXTS_QUESTIONS.get(textOn).size(); j += 4) {

			String question = DEMO_TEXTS_QUESTIONS.get(textOn).get(j);
			String a1 = "A. " + DEMO_TEXTS_QUESTIONS.get(textOn).get(j + 1);
			String a2 = "B. " + DEMO_TEXTS_QUESTIONS.get(textOn).get(j + 2);
			String a3 = "C. " + DEMO_TEXTS_QUESTIONS.get(textOn).get(j + 3);
			
			final JPanel panel = new JPanel();
			final JLabel questionLabel = new JLabel(question);
			final JRadioButton aButton = new JRadioButton(a1);
			final JRadioButton bButton = new JRadioButton(a2);
			final JRadioButton cButton = new JRadioButton(a3);

			panel.setLayout(new GridLayout(4, 1));
			
			panel.add(questionLabel);
			panel.add(aButton);
			panel.add(bButton);
			panel.add(cButton);

			JOptionPane.showMessageDialog(null, panel);
			
			String answer = DEMO_TEXTS_ANSWERS.get(textOn).get(j / 4);
			
			if((answer.equalsIgnoreCase("A") && aButton.isSelected()) || (answer.equalsIgnoreCase("B") && bButton.isSelected()) || (answer.equalsIgnoreCase("C") && cButton.isSelected())){
				
				numCorrect++;
				
			}

		}
		
		JOptionPane.showMessageDialog(null, "You got " + (int) numCorrect + " / " + (int) numTotal + ", or " + (numCorrect / numTotal) + "%");

		textOn++;

		if (textOn < NUM_DEMOS) {

			nextText();

		}

	}

	private static void init() {

		for (int i = 1; i <= NUM_DEMOS; i++) {

			String path = DEMO_TEXT_PATH + "demoText" + i + ".txt";

			Scanner scan = null;

			try {

				scan = new Scanner(new File(path));

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			}

			StringBuilder text = new StringBuilder();

			while (scan.hasNextLine()) {

				text.append(scan.nextLine() + " ");

			}

			scan.close();

			DEMO_TEXTS[i - 1] = text.toString();

			path = DEMO_TEXT_PATH + "demoQA" + i + ".txt";

			try {

				scan = new Scanner(new File(path));

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			}

			text = new StringBuilder();

			DEMO_TEXTS_QUESTIONS.add(new ArrayList<String>());
			DEMO_TEXTS_ANSWERS.add(new ArrayList<String>());

			while (scan.hasNextLine()) {

				String line = scan.nextLine();

				if (line.length() == 1) {

					DEMO_TEXTS_ANSWERS.get(i - 1).add(line);

				}

				else {

					DEMO_TEXTS_QUESTIONS.get(i - 1).add(line);

				}

			}

			scan.close();

		}

		initialized = true;

	}

}

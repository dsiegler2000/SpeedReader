package graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;

public class CalibrationScreen extends JFrame {

	private static final long serialVersionUID = -2740489105774604650L;

	public CalibrationScreen() {

		JSpinner a = new JSpinner();
		this.add(a, BorderLayout.CENTER);
		JButton enter = new JButton("Enter");

		this.add(enter, BorderLayout.SOUTH);

		this.setTitle("You are probably worse than you thought at reading");
		this.setMinimumSize(new Dimension(200, 100));
		this.setVisible(true);
		
	}

}

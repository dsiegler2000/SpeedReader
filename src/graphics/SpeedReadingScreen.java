package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import algorithm.SentenceParser;

public class SpeedReadingScreen extends JFrame implements KeyListener {
	
	/**
	 * The serial version UUID
	 */
	private static final long serialVersionUID = 45675467678L;
	
	public static final float CALIBRATION_CONSTANT = 0.95f;
	
	private JLabel label;
	
	/*
	 * The text that this screen will be displaying.
	 */
	private String[] text;
	private int wordsPerMinute;
	
	private int wordCurrentlyOn;
	
	private boolean paused;
	
	private Timer timer;
	
	public SpeedReadingScreen(String t, int wordsPerMinute){
		
		super("Speed Reading Screen");
		
		this.text = SentenceParser.algorithm(t, CALIBRATION_CONSTANT);
		
		/*
		 * TODO:
		 * Fix the issue where it will transpose some words (likely due to tree parsing stuff)
		 * Test more to see if preprocessing should be done before or after the algo
		 * Expand preprocessing (stopword stuff and more regexs)
		 * Get a good constant for now 
		 * ORGANIZE CODE
		 */
		this.wordsPerMinute = wordsPerMinute;
		
		this.paused = false;
		
		label = new JLabel("", SwingConstants.CENTER);
		
		label.setFont(label.getFont().deriveFont(100f));
		
		this.add(label, BorderLayout.CENTER);
		
		this.setVisible(true);
		this.setSize(1000, 800);
		this.setMinimumSize(new Dimension(800, 300));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addKeyListener(this);
		
		makeTimer(label);
				
	}
	
	private void makeTimer(JLabel textArea){
		
		this.wordCurrentlyOn = 0;
		
		int wordsPerMilli = (int) (60000f / (float) this.wordsPerMinute);
				
		this.timer = new Timer(wordsPerMilli, new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(wordCurrentlyOn < text.length){
					
					textArea.setText(text[wordCurrentlyOn++]);
					
				}
				
				else{
					
					DemoScreen.nextQuestions();
					dispose();
															
				}
												
			}
			
		});
		
		timer.start();
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		


		if(e.getKeyCode() == KeyEvent.VK_SPACE){
							
			if(!this.paused){
				
				timer.stop();
				this.paused = true;
				this.getContentPane().setBackground(new Color(255, 96, 96));
				
			}
			
			else{
				
				timer.start();
				this.paused = false;
				this.getContentPane().setBackground(Color.WHITE);
				
			}
			
			
		}
		
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			
			if(wordCurrentlyOn < text.length - 1){
								
				this.label.setText(text[wordCurrentlyOn++]);
				
			}
			
		}
		
		else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			
			if(wordCurrentlyOn > 0){
				
				this.label.setText(text[wordCurrentlyOn--]);
				
			}
						
		}
				
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}

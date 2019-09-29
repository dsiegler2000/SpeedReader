package graphics;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HomeScreen extends JFrame {
	
	public static int WORDS_PER_MINUTE = 120;
		
	private JButton loadFromFileButton;
	private JButton inputCustomTextButton;
	private JButton calibrateButton;
	private JButton demoButton;

	private static final long serialVersionUID = 5395682596701L;

	public HomeScreen(){
		
		super("Speed Reader");
		
		this.loadFromFileButton = new JButton("Load from file");
		this.inputCustomTextButton = new JButton("Input Custom text");
		this.calibrateButton = new JButton("Run calibration process (TODO)");
		this.demoButton = new JButton("Demo");
		
		this.loadFromFileButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				File file = getFile();
				
				if(file != null){
					
					StringBuilder text = new StringBuilder();
					
					Scanner scan = null;
					
					try {
						
						scan = new Scanner(file);
						
					} catch (FileNotFoundException e1) {

						e1.printStackTrace();
						
					}
					
					while(scan.hasNextLine()){
						
						text.append(scan.nextLine());
						
					}
										
					dispose();
					
					@SuppressWarnings("unused")
					SpeedReadingScreen speedReadingScreen = new SpeedReadingScreen(text.toString(), WORDS_PER_MINUTE);
					
				}
				
			}
			
		});
		
		this.inputCustomTextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();
				@SuppressWarnings("unused")
				CustomTextInputScreen customTextInputScreen = new CustomTextInputScreen();
				
			}
			
		});
		
		this.calibrateButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				
				// @SuppressWarnings("unused")
				// CalibrationScreen c = new CalibrationScreen();
				// dispose();
				
			}
			
		});
		
		this.demoButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				
				DemoScreen.startDemo();
				dispose();
				
			}
			
		});
		
		JPanel topGroup = new JPanel();
		topGroup.setLayout(new GridLayout(4, 1));

	    topGroup.add(loadFromFileButton);
	    topGroup.add(inputCustomTextButton);
	    topGroup.add(calibrateButton);
	    topGroup.add(demoButton);

	    add(topGroup, BorderLayout.CENTER);
		
		this.setSize(300, 120);
		this.setLocation(800, 600);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	private File getFile(){
		
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt or .text", "txt", "text");
		fileChooser.setFileFilter(filter);
		
		int returnValue = fileChooser.showOpenDialog(this);
		
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			
            File file = fileChooser.getSelectedFile();
            
            return file;

        } 
		
		else {
			
            return null;
            
        }
		
	}
	
}

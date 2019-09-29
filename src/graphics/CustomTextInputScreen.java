package graphics;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

public class CustomTextInputScreen extends JFrame implements KeyListener {

	private JTextArea textArea;
	private JButton okButton;

	private UndoManager manager;
	
	private static final long serialVersionUID = 98237598375945L;

	public CustomTextInputScreen() {

		super("Custom Text Input Screen");

		textArea = new JTextArea();
		textArea.setSize(400, 400);

		textArea.setLineWrap(true);
		textArea.setEditable(true);
		textArea.setVisible(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JPanel buttonPanel = new JPanel();
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();

				@SuppressWarnings("unused")
				SpeedReadingScreen speedReadingScreen = new SpeedReadingScreen(textArea.getText(), HomeScreen.WORDS_PER_MINUTE);

			}

		});

		buttonPanel.add(okButton, BorderLayout.CENTER);
		
		manager = new UndoManager();
		textArea.getDocument().addUndoableEditListener(manager);
		
		textArea.addKeyListener(this);

		this.add(scrollPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		this.setSize(800, 600);
		this.setLocation(800, 600);

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				@SuppressWarnings("unused")
				HomeScreen homeScreen = new HomeScreen();
				dispose();

			}

		});

		this.setVisible(true);

		buttonPanel.addKeyListener(new KeyAdapter(){
			
			@Override
			public void keyPressed(KeyEvent e){
				
				if(e.getKeyCode() == 13){
					
					okButton.doClick();
					
				}
				
			}
			
		});
		
		buttonPanel.setFocusable(true);
		buttonPanel.requestFocus();

	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_Z && (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0 && manager.canUndo()){
			
			manager.undo();
			
		}
		
		if(e.getKeyChar() == KeyEvent.VK_R && (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0 && manager.canRedo()){
			
			manager.redo();
			
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}

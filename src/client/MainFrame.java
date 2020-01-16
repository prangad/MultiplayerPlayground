package client;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import util.Config;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements WindowListener {
	
	public static Config cfg = new Config();
	
	private final String TITLE = Config.cfg.getProperty("window-title");
	private final int WIDTH = Integer.parseInt(Config.cfg.getProperty("window-width"));
	private final int HEIGHT = Integer.parseInt(Config.cfg.getProperty("window-height"));
	
	MainPanel mainPanel;
	
	public MainFrame()
	{
		this.setTitle(TITLE);
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		//this.setAlwaysOnTop(true);
		
		Container contentArea = this.getContentPane();
		contentArea.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainPanel = new MainPanel();
		mainPanel.setFocusable(true);
		contentArea.add(mainPanel);
		mainPanel.requestFocusInWindow();
		
		this.addWindowListener(this);
		
		this.setVisible(true);
		this.pack();
	}
	
	public void windowClosing(WindowEvent e)
	{
		mainPanel.disconnect();
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}

package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;
 
public class Config
{
	public static Config cfg;
	
	private Properties props;
	private File file;
	private FileReader reader = null;
	private FileOutputStream writer = null;
	
	public Config()
	{
		cfg = this;
		file = new File("config.properties");
		
		try {
			if (file.exists())
			{
				reader = new FileReader("config.properties");
				props = new Properties();
				props.load(reader);
				reader.close();
			}
			else
			{
				writer = new FileOutputStream(file);
				props = new Properties();
				props.setProperty("window-title", "Death\'s Playground");
				props.setProperty("window-width", "1000");
				props.setProperty("window-height", "800");
				props.setProperty("max-frame-rate", "60");
				props.setProperty("client-max-buffer-mb", "1024");
				props.setProperty("server-max-buffer-mb", "1024");
				props.setProperty("max-client-rotations-ms", "10");
				props.setProperty("max-client-movement-ms", "10");
				props.setProperty("world-refresh-rate-ms", "1000");
				props.store(writer, "Config Dump (moar leik big dump in my toilet amitire XDDDD)");
				writer.close();
			}
		
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	public String getProperty(String key)
	{
		if (this.props.getProperty(key) != null)
		{
			return this.props.getProperty(key);
		}
		else
		{
			String userInput = JOptionPane.showInputDialog("The config property \"" + key + "\" does not exist.\n"
					+ "Don't worry, an elite group of digital hamster samurais are going to escort\n"
					+ "your value to its proper location.\n"
					+ "What would you like the value to be?");
			try {
				props = new Properties();
				reader = new FileReader("config.properties");
				props.load(reader);
				reader.close();
				
				writer = new FileOutputStream(file);
				props.setProperty(key, userInput);
				props.store(writer, "Config Dump (moar leik big dump in my toilet amitire XDDDD)");
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userInput;
		}
	}
}
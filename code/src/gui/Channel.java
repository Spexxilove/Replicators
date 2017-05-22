/**
 * 
 */
package gui;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

/**
 * gui-representation of a Channel JPanel, which contains a JTextPane (chat)
 * within a JScrollPane
 * @author Severin
 */
public class Channel extends JPanel{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private StyledDocument content;
	private JTextPane chat;
	private Boolean active;
	
	/**
	 * calls {@link #initSubContainer(String)} passing ChannelName
	 * @param ChannelName
	 */
	public Channel(String channelName){
		initSubContainer(channelName);
	}
	/**
	 * calls superconstructor passing layoutManager calls
	 * {@link #initSubContainer(String)} passing channelName
	 * @param arg0
	 * @param ChannelName
	 */
	public Channel(LayoutManager layoutManager, String channelName){
		super(layoutManager);
		initSubContainer(channelName);
	}
	/**
	 * calls superconstructor passing isDoubleBuffered calls
	 * {@link #initSubContainer(String)} passing channelName
	 * @param isDoubleBuffered
	 * @param ChannelName
	 */
	public Channel(boolean isDoubleBuffered, String channelName){
		super(isDoubleBuffered);
		initSubContainer(channelName);
	}
	/**
	 * calls superconstructor passing layoutManager and isDoubleBuffered
	 * calls {@link #initSubContainer(String)} passing channelName
	 * @param layoutManager
	 * @param isDoubleBuffered
	 * @param channelName
	 */
	public Channel(LayoutManager layoutManager, boolean isDoubleBuffered, String channelName){
		super(layoutManager, isDoubleBuffered);
		initSubContainer(channelName);
	}
	/**
	 * sets Layout, builds document and puts chat into JScrollPane
	 * sets name with passed channelName
	 * @param ChannelName
	 */
	private void initSubContainer(String channelName){
		active = new Boolean(false);
		setLayout(new GridLayout());
		name = channelName;
		content = new DefaultStyledDocument();
		chat = new JTextPane();
		chat.setEditable(false);
		chat.setDocument(content);
		JScrollPane scrollableChat = new JScrollPane(chat);
		add(scrollableChat);
	}
	/**
	 * inserts input at the end of the document in chat
	 * @param input
	 */
	/*public void print(String input,TextStyle style){
		StringBuilder Output = new StringBuilder();
		Output.append(input);
		Output.append("\n");
		String text = Output.toString();
		SimpleAttributeSet stylemode=null;
		switch(style){
			case NORMAL:
				stylemode = null;
				break;
			case KEY:
				stylemode=Formatation.key;
				break;
			case GAME:
				stylemode=Formatation.game;
				break;
			case SYSTEM:
				stylemode=Formatation.system;
				break;
			case WISPERED:
				stylemode=Formatation.wispered;
				break;
			case BROADCASTED:
				stylemode=Formatation.broadcasted;
				break;
			case WELCOME:
				stylemode=Formatation.welcome;
				break;
			case BB:
				stylemode=Formatation.bb;
				break;
		}
		print(text,stylemode);
	}*/
	public void println(String sender,String input,SimpleAttributeSet stylemode){
		print(sender,Formatation.player);
		println(input,stylemode);
	}
	public void println(String input,SimpleAttributeSet stylemode){
		print(input+"\n",stylemode);
	}
	public void print(String input,SimpleAttributeSet stylemode){
		try{
			content.insertString(content.getLength(), input, stylemode);
			chat.setCaretPosition(content.getLength());
		}
		catch(BadLocationException e){
			e.printStackTrace();
			System.out.println("Text could not be displayed on Chat");
		}
		chat.setDocument(content);
	}
	
	/**
	 * @return channelName
	 */
	public String getName(){
		return name;
	}
	/**
	 * sets active
	 * determins wheter channel can be left(true) or joined (false)
	 * @param active
	 */
	public void setactiv(Boolean active){
		this.active = active;
	}
	/**
	 * @return active
	 */
	public Boolean getactive(){
		return active;
	}
}

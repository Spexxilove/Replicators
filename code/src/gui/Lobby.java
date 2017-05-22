package gui;

import client.NetworkClient;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Severin
 */
public class Lobby extends JFrame{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	public Lobby lobby = this;
	private static TabbeChats chats;
	private static List<String> channels;
	private static List<String> games;
	private NetworkClient client;
	private ActionListener sender;
	private JPanel contentPane;
	private JPanel content;
	private JPanel leftPanel;
	private JPanel chatPanel;
	private JPanel rightPanel;
	private JMenuBar menu;
	private JTextField input;
	private JButton send;
	private JButton toggleReady;
	private JButton leaveGame;
	private JLabel channelsOverview;
	private JLabel gamesOverview;
	/**
	 * Construct Lobby,
	 * @param Client
	 */
	public Lobby(final NetworkClient Client){
		this.client = Client;
		Formatation formats = new Formatation();
		channels = new ArrayList<String>();
		channels.add("MAIN");
		games = new ArrayList<String>();
		setFrameOptions();
		initContentPane();
		pack();
		setVisible(true);
	}
	/**
	 * modify attributes of Lobby-Frame
	 */
	private void setFrameOptions(){
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setOpacity(1f);
		setBackground(Color.lightGray);
		setName("Lobby");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * creates contentPane, sets ContentPane of Lobby to contentPane calls
	 * {@link #initContent()}
	 */
	private void initContentPane(){
		contentPane = new ImagePanel("/assets/images/hintergrundbig.png");
		contentPane.setFocusable(false);
		contentPane.setBackground(new Color(0, 180, 180));
		contentPane.setBorder(new EmptyBorder(35, 35, 35, 35));
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		setContentPane(contentPane);
		initContent();
	}
	/**
	 * creates content Panel adds it to contentPane initiates construction of
	 * further components
	 */
	private void initContent(){
		content = new JPanel();
		content.setPreferredSize(new Dimension(1000, 600));
		content.setFocusable(false);
		contentPane.add(content);
		GridBagLayout gbl_Content = new GridBagLayout();
		gbl_Content.columnWidths = new int[] { 300, 700, 300 };
		gbl_Content.rowHeights = new int[] { 50, 550, 50 };
		gbl_Content.columnWeights = new double[] { 0.0, Double.MIN_VALUE, 0.0 };
		gbl_Content.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		content.setLayout(gbl_Content);
		initsender();
		addMenu();
		addLeftPanel();
		addChatPanel();
		addRightPanel();
	}
	/**
	 * initialise the ActionListener for actionPerformed, that want to send the
	 * Text from input, manipulates Text according the purpose, clears input
	 */
	private void initsender(){
		sender = new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				String inputText = input.getText();
				StringBuilder message = new StringBuilder();
				if(!inputText.startsWith("/")){
					message.append("@" + chats.getSelectedName() + " ");
				}
				message.append(inputText);
				client.processUserInput(message.toString());
				input.setText("");
			}
		};
	}
	/**
	 * add Menubar as there are no options yet, it is invisible
	 */
	private void addMenu(){
		// TODO remove if not found any purpos...
		// maybe add Option touch and graphic again
		menu = new JMenuBar();
		menu.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints gbc_Menu = new GridBagConstraints();
		gbc_Menu.anchor = GridBagConstraints.NORTHWEST;
		gbc_Menu.fill = GridBagConstraints.BOTH;
		gbc_Menu.gridx = 1;
		gbc_Menu.gridy = 0;
		content.add(menu, gbc_Menu);
		JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mntmNewMenuItem.setBackground(new Color(204, 77, 255));
		menu.add(mntmNewMenuItem);
		menu.setVisible(false);
	}
	/**
	 * creates the left side of the lobby, containing create, join, leave and
	 * ready - Buttons calls addComponent for these Buttons
	 */
	private void addLeftPanel(){
		leftPanel = new JPanel();
		GridBagConstraints gbc_Left = new GridBagConstraints();
		gbc_Left.anchor = GridBagConstraints.NORTHWEST;
		gbc_Left.fill = GridBagConstraints.BOTH;
		gbc_Left.gridx = 0;
		gbc_Left.gridy = 1;
		content.add(leftPanel, gbc_Left);
		GridBagLayout gbl_Left = new GridBagLayout();
		gbl_Left.columnWidths = new int[] { 150, 150 };
		gbl_Left.rowHeights = new int[] { 70, 70, 70, 70 };// 550 max
		gbl_Left.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_Left.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		leftPanel.setLayout(gbl_Left);
		addJoinChannel();
		addJoinGame();
		addCreateGame();
		addCreateChannel();
		addLeaveGame();
		addLeaveChannel();
		addToggleReady();
	}
	/**
	 * adds CreateChannel Button, opens {@link InputDialog} with method
	 * "Channel"
	 */
	private void addCreateChannel(){
		JButton createChannel = new JButton("Create Channel");
		createChannel.setMaximumSize(new Dimension(27, 150));
		createChannel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				new InputDialog(client, "Channel");
			}
		});
		GridBagConstraints gbc_CreateChannel = new GridBagConstraints();
		gbc_CreateChannel.anchor = GridBagConstraints.NORTHWEST;
		gbc_CreateChannel.gridx = 0;
		gbc_CreateChannel.gridy = 0;
		gbc_CreateChannel.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(createChannel, gbc_CreateChannel);
	}
	/**
	 * adds CreateGame Button, opens {@link InputDialog} with method "Game"
	 */
	private void addCreateGame(){
		JButton createGame = new JButton("Create Game");
		createGame.setMaximumSize(new Dimension(27, 150));
		createGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				new InputDialog(client, "Game");
			}
		});
		GridBagConstraints gbc_CreateGame = new GridBagConstraints();
		gbc_CreateGame.anchor = GridBagConstraints.NORTHWEST;
		gbc_CreateGame.gridx = 1;
		gbc_CreateGame.gridy = 0;
		gbc_CreateGame.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(createGame, gbc_CreateGame);
	}
	/**
	 * adds JoinChannel Button, opens either {@link JOptionPane} telling there
	 * is no joinable Channel or {@link ChoseDialog} creating a dialog to enter
	 * a Channel
	 */
	private void addJoinChannel(){
		JButton joinChannel = new JButton("Join Channel");
		joinChannel.setMaximumSize(new Dimension(27, 150));
		joinChannel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if(getjoinableChannels().size() == 0){
					JOptionPane
							.showMessageDialog(
									lobby,
									"<HTML><B>Sorry</B><Br>,"
											+ " there is no Channel to be found that you could join.<Br>"
											+ " But you are free to open a new one with the "
											+ "<B>Create Channel</B></HTML>",
									"No Channel found",
									JOptionPane.INFORMATION_MESSAGE);
				}else{
					new ChoseDialog(getjoinableChannels(), client, "Channel");
				}
			}
		});
		GridBagConstraints gbc_JoinChannel = new GridBagConstraints();
		gbc_JoinChannel.anchor = GridBagConstraints.NORTHWEST;
		gbc_JoinChannel.gridx = 0;
		gbc_JoinChannel.gridy = 1;
		gbc_JoinChannel.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(joinChannel, gbc_JoinChannel);
	}
	/**
	 * adds JoinGame Button, opens either {@link JOptionPane} telling there is
	 * no joinable Game (games.length == 50 ist defaultwert) or
	 * {@link ChoseDialog} creating a dialog to enter a Game, which will cause
	 * leaving the current Game
	 */
	private void addJoinGame(){
		JButton JoinGame = new JButton("Join Game");
		JoinGame.setMaximumSize(new Dimension(27, 150));
		JoinGame.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				if(games.isEmpty()){
					JOptionPane
							.showMessageDialog(
									lobby,
									"<HTML><B>Sorry</B><Br>"
											+ "There is no Game to be found that you could join.<Br>"
											+ "But you are free to open a new one with the "
											+ "<B>Create Game</B>",
									"No Game found",
									JOptionPane.INFORMATION_MESSAGE);
				}else{
					new ChoseDialog(games, client, "Game");
				}
			}
		});
		GridBagConstraints gbc_JoinGame = new GridBagConstraints();
		gbc_JoinGame.anchor = GridBagConstraints.NORTHWEST;
		gbc_JoinGame.gridx = 1;
		gbc_JoinGame.gridy = 1;
		gbc_JoinGame.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(JoinGame, gbc_JoinGame);
	}
	/**
	 * adds LeaveChannel Button, opens either {@link JOptionPane} telling there
	 * is no leaveable (active) Channel or {@link ChoseDialog} creating a dialog
	 * to leave one of the active Channels
	 */
	private void addLeaveChannel(){
		JButton leaveChannel = new JButton("Leave Channel");
		leaveChannel.setMaximumSize(new Dimension(27, 150));
		leaveChannel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(getactiveChannels().size() == 0
						|| getactiveChannels() == null){
					// TODO this optionpane should now be useless, remove after
					// begin of june 2014 if still here
					JOptionPane
							.showMessageDialog(
									lobby,
									"<HTML><B>Sorry</B><Br>"
											+ "There is no Channel to be found that you could leave.<Br>"
											+ "But you are free to open a new one with the <B>Create Channel</B>. <Br>"
											+ "Note: the <B>Main</B> Channel cannot be left but by closing the programm.</HTML>",
									"No Channel found",
									JOptionPane.INFORMATION_MESSAGE);
				}else{
					new ChoseDialog(getactiveChannels(), client, "leaveChannel");
				}
			}
		});
		GridBagConstraints gbc_LeaveChannel = new GridBagConstraints();
		gbc_LeaveChannel.anchor = GridBagConstraints.NORTHWEST;
		gbc_LeaveChannel.gridx = 0;
		gbc_LeaveChannel.gridy = 2;
		gbc_LeaveChannel.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(leaveChannel, gbc_LeaveChannel);
	}
	/**
	 * adds LeaveGame Button, opens either {@link JOptionPane} telling there is
	 * no leaveable (active) Game or {@link ChoseDialog} creating a dialog to
	 * leave the active Game
	 */
	private void addLeaveGame(){
		leaveGame = new JButton("Leave Game");
		leaveGame.setMaximumSize(new Dimension(27, 150));
		leaveGame.setEnabled(false);
		leaveGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				String game = client.gameName;
				if(game.trim().equals("")){
					JOptionPane
							.showMessageDialog(
									lobby,
									"<HTML><B>Sorry</B><Br>,"
											+ " you cannot leave any Game because you are in none"
											+ ".", "You are not in a Game",
									JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				client.processUserInput("/leaveGame " + game);
			}
		});
		GridBagConstraints gbc_LeaveGame = new GridBagConstraints();
		gbc_LeaveGame.anchor = GridBagConstraints.NORTHWEST;
		gbc_LeaveGame.gridx = 1;
		gbc_LeaveGame.gridy = 2;
		gbc_LeaveGame.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(leaveGame, gbc_LeaveGame);
	}
	/**
	 * adds ToggleReady Button, which will only be enabled, when there is an
	 * active Game in {@link chats} the Text displayed will show whether the
	 * User is ready or not
	 */
	private void addToggleReady(){
		toggleReady = new JButton("<HTML>Click to signal <B>Ready</B></HTML>");
		toggleReady.setMaximumSize(new Dimension(27, 300));
		toggleReady.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				client.processUserInput("/toggleReady");
			}
		});
		toggleReady.setEnabled(false);
		//TODO here a JXButton shall be used
		//toggleReady.setContentAreaFilled(false);;
		//toggleReady.setOpaque(true);
		GridBagConstraints gbc_toggleReady = new GridBagConstraints();
		gbc_toggleReady.anchor = GridBagConstraints.NORTHWEST;
		gbc_toggleReady.gridx = 0;
		gbc_toggleReady.gridy = 3;
		gbc_toggleReady.gridwidth = 2;
		gbc_toggleReady.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(toggleReady, gbc_toggleReady);
	}
	/**
	 * initialises the Panel, that contains the tabbed channels ({@link chats},
	 * {@link input }and {@link send}
	 */
	private void addChatPanel(){
		chatPanel = new JPanel();
		chatPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		GridBagLayout gbl_ChatPanel = new GridBagLayout();
		gbl_ChatPanel.columnWidths = new int[] { 550, 150 };
		gbl_ChatPanel.rowHeights = new int[] { 500, 100 };
		gbl_ChatPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_ChatPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		chatPanel.setLayout(gbl_ChatPanel);
		GridBagConstraints gbc_ChatPanel = new GridBagConstraints();
		gbc_ChatPanel.fill = GridBagConstraints.BOTH;
		gbc_ChatPanel.gridx = 1;
		gbc_ChatPanel.gridy = 1;
		content.add(chatPanel, gbc_ChatPanel);
		addChats();
		addTextFieldInput();
		addSend();
	}
	/**
	 * creates and adds the tabbed channels container
	 */
	private void addChats(){
		chats = new TabbeChats();
		chats.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_Chats = new GridBagConstraints();
		gbc_Chats.insets = new Insets(8, 8, 5, 8);
		gbc_Chats.ipady = 20;
		gbc_Chats.anchor = GridBagConstraints.NORTHWEST;
		gbc_Chats.fill = GridBagConstraints.BOTH;
		gbc_Chats.gridx = 0;
		gbc_Chats.gridy = 0;
		gbc_Chats.gridwidth = 2;
		chatPanel.add(chats, gbc_Chats);
	}
	/**
	 * creates and adds the input (TextField) and adds sender
	 */
	private void addTextFieldInput(){
		input = new JTextField();
		input.addActionListener(sender);
		input.setMinimumSize(new Dimension(100, 30));
		input.setFont(new Font("Tahoma", Font.PLAIN, 11));
		input.setPreferredSize(new Dimension(6, 30));
		GridBagConstraints gbc_Input = new GridBagConstraints();
		gbc_Input.insets = new Insets(0, 8, 0, 5);
		gbc_Input.fill = GridBagConstraints.HORIZONTAL;
		gbc_Input.gridx = 0;
		gbc_Input.gridy = 1;
		chatPanel.add(input, gbc_Input);
	}
	/**
	 * creates and adds the send (Button) and adds sender
	 */
	private void addSend(){
		send = new JButton("Send");
		send.addActionListener(sender);
		GridBagConstraints gbc_Send = new GridBagConstraints();
		gbc_Send.gridx = 1;
		gbc_Send.gridy = 1;
		chatPanel.add(send, gbc_Send);
	}
	/**
	 * adds {@link #rightPanel} to {@link #content} on the right side of the
	 * Frame sets a GridBackLayout calls {@link #gamesOverview} and
	 * {@link #channelsOverview}
	 */
	private void addRightPanel(){
		rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(35, 35, 35, 35));
		GridBagConstraints gbc_Right = new GridBagConstraints();
		gbc_Right.anchor = GridBagConstraints.NORTHWEST;
		gbc_Right.fill = GridBagConstraints.BOTH;
		gbc_Right.gridx = 2;
		gbc_Right.gridy = 1;
		content.add(rightPanel, gbc_Right);
		GridBagLayout gbl_Right = new GridBagLayout();
		gbl_Right.columnWidths = new int[] { 250 };
		gbl_Right.rowHeights = new int[] { 300, 250 };// 550 max
		gbl_Right.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_Right.rowWeights = new double[] { 0.5, 0.5 };
		rightPanel.setLayout(gbl_Right);
		addGamesOverview();
		addChannelsOverview();
	}
	/**
	 * adds {@link #channelsOverview} to {@link #rightPanel} on top
	 */
	private void addChannelsOverview(){
		channelsOverview = new JLabel("this is the channeloverview");
		channelsOverview.setOpaque(true);
		GridBagConstraints gbc_ChannelsOverview = new GridBagConstraints();
		gbc_ChannelsOverview.anchor = GridBagConstraints.NORTH;
		gbc_ChannelsOverview.gridx = 0;
		gbc_ChannelsOverview.gridy = 0;
		rightPanel.add(channelsOverview, gbc_ChannelsOverview);
	}
	/**
	 * adds {@link #gamesOverview} to {@link #rightPanel} on bottom
	 */
	private void addGamesOverview(){
		gamesOverview = new JLabel("this ist the gamesoverview");
		gamesOverview.setOpaque(true);
		GridBagConstraints gbc_GamesOverview = new GridBagConstraints();
		gbc_GamesOverview.anchor = GridBagConstraints.NORTH;
		gbc_GamesOverview.gridx = 0;
		gbc_GamesOverview.gridy = 1;
		rightPanel.add(gamesOverview, gbc_GamesOverview);
	}
	/**
	 * adds passed createdChannel to channels and to chats when the User is the
	 * Creator of the Channel
	 * @param CreatedChannel
	 * @param sender
	 */
	public void addChannel(String createdChannel, String sender){
		games.contains(createdChannel);
		if(channels.contains(createdChannel)){}else{
			if(!createdChannel.equals("Global")){
				channels.add(createdChannel);
			}
		}
		if(sender.equals(client.getClientName())){
			chats.addChannel(createdChannel);
		}
	}
	/**
	 * returns a List of inactive Channels
	 * @return joinableChannels
	 */
	public List<String> getjoinableChannels(){
		List<String> joinableChannels = chats.findinactivChannels(channels);
		return joinableChannels;
	}
	/**
	 * returns a List of the active Channels without Main!
	 * @return activeChannels
	 */
	public List<String> getactiveChannels(){
		List<Channel> activeChannels = chats.getleaveableChannels(games);
		List<String> activeStrings = new ArrayList<String>();
		for(int i = 0; i < activeChannels.size(); i++){
			String temp = activeChannels.get(i).getName();
			if(!(temp == "MAIN")){
				activeStrings.add(temp);
			}
		}
		return activeStrings;
	}
	/**
	 * @return {@link games}
	 */
	public List<String> getGames(){
		return games;
	}
	/**
	 * returns a new List containing all Games and Channels of which the Lobby
	 * knows about
	 * @return
	 */
	public static List<String> getAllChannels(){
		List<String> allChannels = new ArrayList<String>();
		allChannels.addAll(games);
		allChannels.addAll(channels);
		return allChannels;
	}
	/**
	 * adds newgame to games
	 * @param newGame
	 */
	public void addGame(String newGame){
		if((!newGame.trim().equals("")) && !games.contains(newGame)){
			games.add(newGame);
		}
	}
	/**
	 * splits message on / for channels and then on whitespace for players and
	 * adds them to the Overview Label
	 * @param message
	 */
	public void processChannelList(String message, String sender){
		// message looks like:
		// aaa Severin/MAIN Severin Severin1/asdaffsdlkfj /holymoly
		// Severin/shitload /akdsfaasassss
		StringBuilder channelsText = new StringBuilder();
		StringBuilder gamesText = new StringBuilder();
		channelsText.append("<HTML><B>Channels</B><Br>");
		gamesText.append("<HTML><B>Games</B><Br>");
		String tab = new String(
				"&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
		String channelsplit[] = message.split("/");
		String supsplit[];
		String names[];
		for(int i = 0; i < channelsplit.length; i++){
			supsplit = channelsplit[i].trim().split(":");// TODO choce saver
															// spliterstring
			if(games.contains(supsplit[0])){
				gamesText.append(tab + supsplit[0] + "<Br>");
				if(supsplit.length > 1){
					names = supsplit[1].split(" ");
					for(int g = 0; g < names.length; g++){
						gamesText.append(tab + tab + names[g] + "<Br>");
					}
				}
			}else{
				addChannel(supsplit[0], "Admin");
				channelsText.append(tab + supsplit[0] + "<Br>");
				if(supsplit.length > 1){
					names = supsplit[1].split(" ");
					for(int c = 0; c < names.length; c++){
						channelsText.append(tab + tab + names[c] + "<Br>");
					}
				}
			}
		}
		channelsText.append("</HTML>");
		gamesText.append("</HTML>");
		channelsOverview.setText(channelsText.toString());
		gamesOverview.setText(gamesText.toString());
	}
	/**
	 * calls addChannel with parameters sends "has entered" message to new
	 * opened or reactivated Channel
	 * @param channel
	 * @param sender
	 */
	public void processjoinChannel(String channel, String sender){
		addChannel(channel, sender);
		String textChannel = channel;
		if(channel.equals("MAIN")){
			textChannel = "the Global Chatroom";
		}
		if(sender.equals(client.getClientName())){
			chats.jumptoChannel(channel);
			print("Welcome to " + textChannel + " " + sender, channel,
					Formatation.welcome);
			return;
		}
		print(sender + " has entered " + textChannel, channel,
				Formatation.welcome);
	}
	/**
	 * if User is joining, the Game will be added ({@link #addGame(String)} and
	 * {@link TabbeChats#addGame(String)}) calls infromReady() and prints
	 * "has joined" message
	 * @param game
	 * @param sender
	 */
	public void processjoinGame(String game, String sender){
		if(sender.equals(client.getClientName())){
			addGame(game);
			chats.addGame(game);
			chats.jumptoChannel(game);
			informReady();
		}
		informLeaveGame();
		print(sender + " has joined Game: " + game, game, Formatation.game);
	}
	/**
	 * prints "has left" message if User is leaving, call
	 * {@link TabbeChats#leaveGame(String)}
	 * @param game
	 * @param sender
	 */
	public void processleaveGame(String game, String sender){
		print(sender + " has left ", game, Formatation.game);
		if(client.getClientName().equals(sender)){
			chats.leaveGame(game);
			informReady();
			informLeaveGame();
		}
	}
	/**
	 * prints "left channel" message if User is leaving call
	 * {@link TabbeChats#leaveChannel(String)}
	 * @param channel
	 * @param sender
	 */
	public void processleaveChannel(String channel, String sender){
		print("user " + sender + " has left the channel " + channel, channel,
				Formatation.system);
		if(sender.equals(client.getClientName())){
			chats.leaveChannel(channel);
		}
	}
	/**
	 * adds newGame if User created it, call
	 * {@link TabbeChats#addChannel(String)}, afterwards call
	 * {@link #informReady()} and print "has entered" message
	 * @param gameName
	 * @param sender
	 */
	public void processnewGame(String newGame, String sender){
		addGame(newGame);
		if(sender.equals(client.getClientName())){
			chats.addChannel(newGame);
			print(sender + " has entered " + newGame, newGame, Formatation.game);
			informReady();
			informLeaveGame();
		}
	}
	/**
	 * updates the Text of LeaveGame, active Game will be displayed
	 */
	private void informLeaveGame(){
		String activeGame = chats.getactiveGame();
		if(activeGame.equals("")){
			leaveGame.setText("leave Game");
			leaveGame.setEnabled(false);
		}else{
			String gamename =chats.getactiveGame();
			if(gamename.length()>10){
				leaveGame.setText("leave Game:" + gamename.substring(0, 7)+"...");
			}else{
				leaveGame.setText("leave Game:" + gamename);
			}
			
			leaveGame.setEnabled(true);
		}
	}
	/**
	 * sets Enabled of {@link #toggleReady} should always be called after
	 * changing actigeGame
	 */
	private void informReady(){
		if(chats.hasactivGame()){
			toggleReady.setEnabled(true);
			toggleReady.setForeground(Color.RED);
		}else{
			toggleReady.setEnabled(false);
			toggleReady.setForeground(null);
		}
	}
	/**
	 * forwards print command to chats with same parameters
	 * @param message
	 * @param channel
	 */
	public void print(String message, String channel, SimpleAttributeSet stylemode){
		try{
			chats.print(message, channel, stylemode);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void print(String sender,String message, String channel, SimpleAttributeSet stylemode){
		try{
			chats.print(sender,message, channel, stylemode);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * sets ReadyButton displayence to given Boolean
	 * @param readyState
	 */
	public void setReadyState(Boolean readyState){
		if(readyState){
			toggleReady.setText("You are Ready");
			toggleReady.setForeground(Color.GREEN);
		}else{
			toggleReady.setText("<HTML>Click to signal <B>Ready</B></HTML>");
			toggleReady.setForeground(Color.RED);
		}
	}
	public void cleanup(){
		informReady();
		setReadyState(false);
	}
}

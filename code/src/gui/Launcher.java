package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;
import java.net.InetAddress;

public class Launcher extends JFrame{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	public JPanel choiceDetails;
	public CardLayout detailsLayout;
	public Details details;
	public Boolean isdone = false;
	private JTextField targetPort;
	private JTextField serverPort;
	private JTextField name;
	private JTextField serverIP;
	private JComboBox<String> selectedChoice;
	private JButton start;
	private ChosenStartup chosenStartOption = ChosenStartup.Client;
	private JPanel contentPane;
	private JPanel left;
	private ActionListener starter;
	private ActionListener actualiser;
	private JCheckBox touchOption;
	private JCheckBox reducedGraphicsOption;
	public Launcher(Details details){
		this.details = details;
		initgui();
		checkDetailsValidation();
	}
	private void initgui(){
		configLauncher();
		createStarter();
		createActualiser();
		initContentPane();
		Boolean isready = checkDetailsValidation();
	}
	private void configLauncher(){
		setTitle("Launcher");
		setResizable(false);
		setName("Launcher");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
	}
	/**
	 * ActionListener for Textfields and Startbutton calls {@link #start()}
	 */
	private void createStarter(){
		starter = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		};
	}
	private void createActualiser(){
		actualiser = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				checkDetailsValidation();
			}
		};
	}
	/**
	 * creates contenPane and filles it with {@link #left} and
	 * {@link #choiceDetails} adds cancel and ok Button
	 */
	private void initContentPane(){
		contentPane = new JPanel();
		configContentPane();
		addleft();
		addChoiceDetails();
		addStart();
		addCancel();
	}
	private void configContentPane(){
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 200, 100, 100, 100 };
		gbl_contentPane.rowHeights = new int[] { 260, 10, 30 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, 0.0 };
		contentPane.setLayout(gbl_contentPane);
	}
	/**
	 * adds Panel Left, which contains {@link #selectedChoice} as well as
	 * {@link #touchOption} and {@link #reducedGraphicsOption}
	 */
	private void addleft(){
		left = new JPanel();
		GridBagLayout gbl_left = new GridBagLayout();
		gbl_left.columnWidths = new int[] { 30, 170 };
		gbl_left.rowHeights = new int[] { 160, 50, 50 };
		gbl_left.columnWeights = new double[] { 0.15, 0.85 };
		gbl_left.rowWeights = new double[] { 1.0, 0.0 };
		left.setLayout(gbl_left);
		addChoice();
		addTouchoption();
		addTouchinfo();
		addreducedGraphicsOption();
		addreducedGraphicsInfo();
		GridBagConstraints gbc_left = new GridBagConstraints();
		gbc_left.weighty = 0.8;
		gbc_left.weightx = 0.1;
		gbc_left.insets = new Insets(0, 0, 5, 5);
		gbc_left.gridx = 0;
		gbc_left.gridy = 0;
		contentPane.add(left, gbc_left);
	}
	/**
	 * adds ComboBox with three Options: Client, Server, Singleplayer selected
	 * entrance dictates {@link #chosenStartOption} and brings corresponding
	 * Card in {@link #choiceDetails} to front
	 */
	private void addChoice(){
		String choiceoptions[] = { "Client", "Server", "Singleplayer" };
		JComboBox<String> choice = new JComboBox<String>(choiceoptions);
		choice.setPreferredSize(new Dimension(150, 50));
		choice.setMinimumSize(new Dimension(120, 30));
		choice.addActionListener(new ActionListener(){
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent arg0){
				selectedChoice = (JComboBox<String>) arg0.getSource();
				chosenStartOption = ChosenStartup
						.valueOf((String) selectedChoice.getSelectedItem());
				switch(chosenStartOption){
					case Client:{
						detailsLayout.show(choiceDetails, "Client");
						break;
					}
					case Server:{
						detailsLayout.show(choiceDetails, "Server");
						break;
					}
					case Singleplayer:{
						detailsLayout.show(choiceDetails, "Singleplayer");
						break;
					}
					default:
						System.out
								.println("ChosenStartupOption in Launcher contained an invalid value");
						break;
				}
				checkDetailsValidation();
			}
		});
		GridBagConstraints gbc_choice = new GridBagConstraints();
		gbc_choice.weighty = 0.8;
		gbc_choice.weightx = 0.1;
		gbc_choice.insets = new Insets(0, 0, 5, 5);
		gbc_choice.gridx = 0;
		gbc_choice.gridy = 0;
		gbc_choice.gridwidth = 2;
		left.add(choice, gbc_choice);
	}
	/**
	 * adds CheckBox for touch
	 */
	private void addTouchoption(){
		touchOption = new JCheckBox();
		GridBagConstraints gbc_touchOption = new GridBagConstraints();
		gbc_touchOption.insets = new Insets(0, 0, 5, 5);
		gbc_touchOption.gridx = 0;
		gbc_touchOption.gridy = 1;
		left.add(touchOption, gbc_touchOption);
	}
	/**
	 * adds Information for touch
	 */
	private void addTouchinfo(){
		JLabel touchInfo = new JLabel();
		touchInfo
				.setText("<HTML><font color=#990099>Selecting this Option will enable Touchscreenmode</font></HTML>");
		GridBagConstraints gbc_touchInfo = new GridBagConstraints();
		gbc_touchInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_touchInfo.insets = new Insets(0, 0, 5, 5);
		gbc_touchInfo.gridx = 1;
		gbc_touchInfo.gridy = 1;
		left.add(touchInfo, gbc_touchInfo);
	}
	/**
	 * adds CheckBox for setting reducedGraphics
	 */
	private void addreducedGraphicsOption(){
		reducedGraphicsOption = new JCheckBox();
		GridBagConstraints gbc_reducedGraphicsOption = new GridBagConstraints();
		gbc_reducedGraphicsOption.insets = new Insets(0, 0, 5, 5);
		gbc_reducedGraphicsOption.gridx = 0;
		gbc_reducedGraphicsOption.gridy = 2;
		left.add(reducedGraphicsOption, gbc_reducedGraphicsOption);
	}
	/**
	 * adds Information for reducedGraphics
	 */
	private void addreducedGraphicsInfo(){
		JLabel reducedGraphicsInfo = new JLabel();
		reducedGraphicsInfo
				.setText("<HTML><font color=#990099>Fullscreen instead of Borderless, improves Graphics-Performence<Br>(adviced for Laptops)</font></HTML>");
		GridBagConstraints gbc_reducedGraphicsInfo = new GridBagConstraints();
		gbc_reducedGraphicsInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_reducedGraphicsInfo.insets = new Insets(0, 0, 5, 5);
		gbc_reducedGraphicsInfo.gridx = 1;
		gbc_reducedGraphicsInfo.gridy = 2;
		left.add(reducedGraphicsInfo, gbc_reducedGraphicsInfo);
	}
	/**
	 * add ChoideDetails creates three cards
	 * ClientDetails,ServerDetails,SinglePlayerDetails
	 */
	private void addChoiceDetails(){
		choiceDetails = new JPanel();
		GridBagConstraints gbc_ChoiceDetails = new GridBagConstraints();
		gbc_ChoiceDetails.weighty = 0.8;
		gbc_ChoiceDetails.weightx = 0.9;
		gbc_ChoiceDetails.insets = new Insets(0, 0, 5, 0);
		gbc_ChoiceDetails.gridwidth = 3;
		gbc_ChoiceDetails.fill = GridBagConstraints.BOTH;
		gbc_ChoiceDetails.anchor = GridBagConstraints.NORTHWEST;
		gbc_ChoiceDetails.gridx = 1;
		gbc_ChoiceDetails.gridy = 0;
		detailsLayout = new CardLayout(0, 0);
		choiceDetails.setLayout(detailsLayout);
		addClientDetails();
		addServerDetails();
		addSinglePlayerDetails();
		contentPane.add(choiceDetails, gbc_ChoiceDetails);
	}
	/**
	 * adds ClientDetails to choiceDetails (first Card) provides default
	 * settings for Details used for clientstart settings can be changed by user
	 * and will be read after starter is called
	 */
	private void addClientDetails(){
		JPanel ClientDetails = new JPanel();
		ClientDetails.setLayout(new GridLayout(3, 2, 0, 0));
		JLabel ServerIPDescription = new JLabel("Server-IP:");
		ServerIPDescription.setHorizontalAlignment(SwingConstants.CENTER);
		ClientDetails.add(ServerIPDescription);
		serverIP = new JTextField();
		serverIP.addActionListener(starter);
		serverIP.setHorizontalAlignment(SwingConstants.CENTER);
		serverIP.setText("Localhost");
		serverIP.setColumns(10);
		ClientDetails.add(serverIP);
		JLabel ServerPortDescription = new JLabel("Server-Port:");
		ServerPortDescription.setHorizontalAlignment(SwingConstants.CENTER);
		ClientDetails.add(ServerPortDescription);
		serverPort = new JTextField();
		serverPort.addActionListener(starter);
		serverPort.setText("25565");
		serverPort.setHorizontalAlignment(SwingConstants.CENTER);
		serverPort.setColumns(10);
		ClientDetails.add(serverPort);
		JLabel NameDescription = new JLabel("Login-Name");
		NameDescription.setHorizontalAlignment(SwingConstants.CENTER);
		ClientDetails.add(NameDescription);
		name = new JTextField();
		name.setHorizontalAlignment(SwingConstants.CENTER);
		name.setText(details.LoginName);
		name.addActionListener(starter);
		name.setColumns(10);
		ClientDetails.add(name);
		choiceDetails.add(ClientDetails, "Client");
	}
	/**
	 * adds ServerDetails to choiceDetails (second Card) contains label stating
	 * users own Ip (local net) adds targetport for input (adds starter)
	 */
	private void addServerDetails(){
		JPanel ServerDetails = new JPanel();
		ServerDetails.setLayout(new GridLayout(2, 2, 0, 0));
		JLabel IPDescription = new JLabel("<HTML>Your local IP:</HTML>");
		// <Br><small>Be aware! This IP is from your local network, others are
		// probably not able to connect to you using this IP. To get your
		// Internet-IP visite: <a
		// href=\"http://www.whatismyip.com/\">www.whatismyip.com</a></small>
		IPDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
		IPDescription.setHorizontalTextPosition(SwingConstants.CENTER);
		IPDescription.setHorizontalAlignment(SwingConstants.CENTER);
		ServerDetails.add(IPDescription);
		String ownIp = "Your IP could not be read";
		try{
			InetAddress iP = InetAddress.getLocalHost();
			ownIp = iP.getHostAddress().toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		JLabel iPoutput = new JLabel(ownIp);
		iPoutput.setHorizontalAlignment(SwingConstants.CENTER);
		iPoutput.setAlignmentX(Component.CENTER_ALIGNMENT);
		ServerDetails.add(iPoutput);
		JLabel PortDescription = new JLabel("Serverport wählen:");
		PortDescription.setHorizontalAlignment(SwingConstants.CENTER);
		ServerDetails.add(PortDescription);
		targetPort = new JTextField();
		targetPort.setHorizontalAlignment(SwingConstants.CENTER);
		targetPort.setText("25565");
		targetPort.setColumns(10);
		targetPort.addActionListener(starter);
		ServerDetails.add(targetPort);
		choiceDetails.add(ServerDetails, "Server");
	}
	/**
	 * adds infoSinglePlayer to choiceDetails (third Card)
	 */
	private void addSinglePlayerDetails(){
		JPanel singlePlayerDetails = new JPanel();
		singlePlayerDetails.setLayout(new GridLayout(1, 0, 0, 0));
		JLabel infosingleplayer = new JLabel(
				"<HTML>Für diesen Modus wurden noch <Br>keine Optionen Implementiert</HTML>");
		infosingleplayer.setHorizontalAlignment(SwingConstants.CENTER);
		infosingleplayer.setToolTipText("Einfach starten ;)");
		singlePlayerDetails.add(infosingleplayer);
		choiceDetails.add(singlePlayerDetails, "Singleplayer");
	}
	/**
	 * adds StartButton it uses ActionListener {@link #starter}
	 */
	private void addStart(){
		start = new JButton("Start");
		start.addActionListener(starter);
		start.setMaximumSize(new Dimension(99, 35));
		start.setMinimumSize(new Dimension(99, 35));
		start.setPreferredSize(new Dimension(99, 35));
		start.setSize(new Dimension(99, 35));
		start.setEnabled(false);
		GridBagConstraints gbc_Start = new GridBagConstraints();
		gbc_Start.fill = GridBagConstraints.BOTH;
		gbc_Start.weighty = 0.05;
		gbc_Start.weightx = 0.05;
		gbc_Start.insets = new Insets(0, 0, 0, 5);
		gbc_Start.gridx = 2;
		gbc_Start.gridy = 2;
		contentPane.add(start, gbc_Start);
	}
	/**
	 * adds CancelButton
	 */
	private void addCancel(){
		JButton cancel = new JButton("Abbrechen");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				System.exit(0);
			}
		});
		cancel.setSize(new Dimension(99, 35));
		cancel.setPreferredSize(new Dimension(99, 35));
		cancel.setMinimumSize(new Dimension(99, 35));
		cancel.setMaximumSize(new Dimension(150, 35));
		GridBagConstraints gbc_Cancel = new GridBagConstraints();
		gbc_Cancel.fill = GridBagConstraints.BOTH;
		gbc_Cancel.weighty = 0.05;
		gbc_Cancel.weightx = 0.05;
		gbc_Cancel.gridx = 3;
		gbc_Cancel.gridy = 2;
		contentPane.add(cancel, gbc_Cancel);
	}
	/**
	 * checks Details and enables start if everything is alright
	 */
	private Boolean checkDetailsValidation(){
		Boolean valide = details.setIP(serverIP.getText())
				&& details.setServerPort(serverPort.getText())
				&& details.setTargetPort(targetPort.getText());
		if(details.setLoginName(name.getText())){
			start.setEnabled(valide);
			return valide;
		}else{
			JOptionPane
					.showMessageDialog(
							this,
							"<HTML>You must chose a name without withespaces, it must not be <b>SYSTEM</b> nor can it be empty</HTML>",
							"invalide name", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}
	/**
	 * if input is valide, {@link #details} will be filled with them. calls
	 * {@link #writeConfig(Boolean, Boolean)}
	 */
	private void start(){
		if(checkDetailsValidation()){
			details.Choice = chosenStartOption;
			isdone = true;
			writeConfig(touchOption.isSelected(),
					reducedGraphicsOption.isSelected());
		}else{}
	}
	/**
	 * sets Options (influences mouseinputhandling and several parts of the
	 * rendering in {@link Game})
	 * @param touch
	 * @param reducedGraphics
	 */
	private void writeConfig(Boolean touch, Boolean reducedGraphics){
		try{
			engine.Config.ISTOUCHSCREEN.isOn = touch;
			engine.Config.FULLSCREEN.isOn = reducedGraphics;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

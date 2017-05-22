package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ServerInterface extends JFrame{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	public static Channel verlauf;
	private JPanel contentPane;
	private JTextField message;
	private ActionListener sender;
	public ServerInterface(){
		initialize();
	}
	private void initialize(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setName("Server");
		setLocation(100, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 200, 400, 300 };
		gbl_contentPane.rowHeights = new int[] { 350, 150 };
		gbl_contentPane.columnWeights = new double[] { 0.5, 0.5 };
		gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, 0.0 };
		contentPane.setLayout(gbl_contentPane);
		GridBagConstraints VerlaufOption = new GridBagConstraints();
		VerlaufOption.fill = GridBagConstraints.BOTH;
		VerlaufOption.gridx = 0;
		VerlaufOption.gridy = 0;
		VerlaufOption.gridwidth = 3;
		verlauf = new Channel("SERVER");
		verlauf.setPreferredSize(new Dimension(200, 200));
		contentPane.add(verlauf, VerlaufOption);
		addCancel();
		addBroadcast();
		pack();
		setVisible(true);
	}
	/**
	 * add Cancel Button
	 */
	private void addCancel(){
		JButton Cancel = new JButton("Cancel");
		GridBagConstraints Layout = new GridBagConstraints();
		Layout.gridx = 2;
		Layout.gridy = 1;
		Cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				System.exit(0);
			}
		});
		contentPane.add(Cancel, Layout);
	}
	/**
	 * adds Textfield and Button to recive Userinput
	 */
	private void addBroadcast(){
		initsender();
		JPanel Broadcast = new JPanel();
		GridBagLayout GridBag = new GridBagLayout();
		GridBag.columnWidths = new int[] { 300, 100 };
		Broadcast.setLayout(GridBag);
		message = new JTextField();
		GridBagConstraints MessageConstraints = new GridBagConstraints();
		MessageConstraints.fill = GridBagConstraints.HORIZONTAL;
		message.addActionListener(sender);
		Broadcast.add(message, MessageConstraints);
		JButton Send = new JButton("Broadcast");
		Send.addActionListener(sender);
		Send.setEnabled(false);
		Broadcast.add(Send);
		GridBagConstraints Layout = new GridBagConstraints();
		Layout.fill = GridBagConstraints.HORIZONTAL;
		Layout.gridx = 1;
		Layout.gridy = 1;
		contentPane.add(Broadcast, Layout);
	}
	/**
	 * init sender ActionListener which sends Broadcasts to Clients
	 */
	private void initsender(){
		sender = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// TODO Broadcast if implemented set Button to enabled
				// TODO maybe add additional Options for Server
				// --- Debug ---System.out.println(message.getText());
			}
		};
	}
	/**
	 * appends message in verlauf
	 * @param message
	 */
	public static void print(String message){
		verlauf.println(message,Formatation.normal);
	}
}

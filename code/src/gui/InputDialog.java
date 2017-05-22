package gui;

import client.NetworkClient;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

/**

 * Creates a JDialog with a TextField for Input of either a Game- or a Channel-name depending on the Mode passed in the Constructor
 * which will be sent to {@link NetworkClient#processUserInput(String)}
 * @author Severin
 */
public class InputDialog extends JDialog {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public JTextField Input;
	public InputDialog me = this;

	private String Modus;
	private NetworkClient Client;

	public InputDialog(NetworkClient NetClient,String Mode) {
		Client = NetClient;
		Modus = Mode;
		
		setTitle("create a new " + Mode);
		
		setBounds(400, 200, 300, 180);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel instruction = new JLabel("<HTML>you can enter the name here, "
					+ "you may not <Br>use whitespaces, "
					+ "whitespaces will be removed</HTML>");
			contentPanel.add(instruction);
		}
		{
			Input = new JTextField();
			contentPanel.add(Input);
			Input.setColumns(20);
		}
		{
			JButton CreateButton = new JButton("Create "+Mode);
			contentPanel.add(CreateButton);
			CreateButton.setActionCommand("OK");
			CreateButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					
					String newChannel = Input.getText().replace("/", "").replace(" ", "");
					if(newChannel.trim().equals("")){
						JOptionPane.showMessageDialog(me,
								"<HTML>The name must contain at least on character</HTML>",
								"invalide name",JOptionPane.INFORMATION_MESSAGE);
					}else{
						if(Lobby.getAllChannels().contains(newChannel)){
							JOptionPane.showMessageDialog(me,
									"<HTML>The name "+newChannel+" is already in use</HTML>",
									"name already in use",JOptionPane.INFORMATION_MESSAGE);
						}else{
							String Command = "";
							if(Modus=="Channel"){	
								Command = "/join ";
							}else{
								if(Modus=="Game"){
									Command = "/createGame ";
								}
							}
							Client.processUserInput(Command + newChannel);
							me.dispose();
						}
					}
				}
			});
			getRootPane().setDefaultButton(CreateButton);
		}
		{
			

			
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						me.dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
}

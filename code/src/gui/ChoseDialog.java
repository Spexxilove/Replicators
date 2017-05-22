package gui;

import client.NetworkClient;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.util.List;
import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates a JDialog with a Combobox for choicing either a Game- or a
 * Channel-name, depending on the Mode passed in the Constructor, from the List
 * ,also passed in Constructor to be either left or joined, result of choice
 * will be sent to {@link NetworkClient#processUserInput(String)} containing the
 * Modus
 * @author Severin
 */
public class ChoseDialog extends JDialog{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public JComboBox<String> Selection;
	public NetworkClient Client;
	public ChoseDialog me = this;
	public static String activGame;
	public String Modus;
	public ChoseDialog(List<String> Channels, NetworkClient NetClient,
			String Mode){
		Client = NetClient;
		Modus = Mode;
		setResizable(false);
		setBounds(400, 200, 250, 150);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			String[] Options = new String[Channels.size()];
			for(int i = Channels.size() - 1; i >= 0; i--){
				if(!Channels.get(i).equals("MAIN")){
					Options[i] = Channels.get(i);
				}
			}
			Selection = new JComboBox<String>(Options);
			Selection.setPreferredSize(new Dimension(100, 25));
			contentPanel.add(Selection);
		}
		{
			JButton okButton = new JButton("join");
			if(Modus == "leaveChannel" || Modus == "leaveGame"){
				okButton.setText("leave");
			}
			contentPanel.add(okButton);
			okButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					String SelectedChannel = (String) Selection
							.getSelectedItem();
					String Command = "";
					switch(Modus){
						case "Channel":
							Command = "/join ";
							break;
						case "Game":
							Command = "/joinGame ";
							break;
						case "leaveChannel":
							Command = "/leave ";
							break;
						case "leaveGame":
							Command = "/leaveGame ";
							break;
					}
					Client.processUserInput(Command + SelectedChannel);
					me.dispose();
				}
			});
			getRootPane().setDefaultButton(okButton);
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

package network;

public class NetMessage {
	public final Prefix PREFIX;
	public final String CHANNEL;
	public final String SENDER;
	public final Commands COMMAND;
	public final String MESSAGE;
	private final String BCHANNELB = "/channel";
	private final String BSENDERB = "/sender";
	private final String BCOMMANDB = "/command";
	private final String BMESSAGEB = "/message";
	public final static String BATTRIBUTESPLITB = "/";
	
	/**
	 * creates NetMessage
	 * @param PREFIX
	 * @param CHANNEL
	 * @param SENDER
	 * @param COMMAND
	 * @param MESSAGE
	 */
	public NetMessage(Prefix PREFIX, String CHANNEL,
			String SENDER,Commands COMMAND,String MESSAGE){
		this.PREFIX = PREFIX;
		this.CHANNEL = CHANNEL;
		this.SENDER = SENDER;
		this.COMMAND = COMMAND;
		this.MESSAGE = MESSAGE;
		
	}
	
	/**
	 * build from network string
	 * @param line
	 */
	public NetMessage(String line){
		this.PREFIX = Prefix.valueOf(line.split(BCHANNELB)[0].trim()); 		
				line = line.split(BCHANNELB)[1].trim();
		this.CHANNEL= line.split(BSENDERB)[0].trim(); 		
				line = line.split(BSENDERB)[1].trim();
		this.SENDER = line.split(BCOMMANDB)[0].trim(); 		
				line = line.split(BCOMMANDB)[1].trim();
		this.COMMAND = Commands.valueOf(line.split(BMESSAGEB)[0].trim());
		
		if(line.split(BMESSAGEB).length != 2)
			this.MESSAGE = "";
		else
			this.MESSAGE = line.split(BMESSAGEB)[1].trim();
	}
	
	/**
	 * builds netmessage as string to ship over network
	 */
	@Override
	public String toString(){
		return encode(PREFIX, CHANNEL, SENDER, COMMAND, MESSAGE);
	}

	/**
	 * build networkmessage for shipping as string from input
	 * @param prefix
	 * @param channel
	 * @param sender
	 * @param command
	 * @param message
	 * @return
	 */
	private String encode(Prefix prefix,String channel,String sender,Commands command,String message){
		String out;
		out = 	prefix.toString() + 
				" " + BCHANNELB + " " + channel.trim() + 
				" " + BSENDERB + " " + sender.trim() + 
				" " + BCOMMANDB + " " + command.toString() + 
				" " + BMESSAGEB + " " + message.trim(); 
		return out;
	}

}

package network;

public abstract class Command {
	protected int id = 0;
	protected int playerId = -1;
	public Command(int id,int playerId){
		this.id = id;
		this.playerId = playerId;
	}
	public int getId(){return id;}
	public int getPlayerId(){return playerId;}
	public String toString(){
		String out = "round: "+id;
		out+= " playerid: ";
		out+= playerId;
		out+= " target: ";
		return out;
	}
}

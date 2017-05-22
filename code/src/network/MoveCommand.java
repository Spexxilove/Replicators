package network;

import org.lwjgl.util.vector.Vector3f;

public class MoveCommand extends Command{
	private int unitIds[] = null;
	private Vector3f moveTarget = null;
	public MoveCommand(int id,int playerId,int unitIds[],Vector3f moveTarget){
		super(id,playerId);
		this.id = id;
		this.unitIds = unitIds;
		this.moveTarget = moveTarget;
		this.playerId = playerId;
	}
	public int getId(){return id;}
	public int getPlayerId(){return playerId;}
	public Vector3f getMoveTarget(){return moveTarget;}
	public int[] getUnitIds(){return unitIds;}
	public String toString(){
		String out = "round: "+id;
		out+= " playerid: ";
		out+= playerId;
		out+= " unitIds: ";
		for(int i=0;i<unitIds.length;++i){
			out+= unitIds[i]+" ";
		}
		out+= " target: ";
		out+= moveTarget.toString();
		out+= " ";
		return out;
	}
}

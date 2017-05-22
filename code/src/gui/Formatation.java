package gui;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Formatation{
	
	public static SimpleAttributeSet key;
	public static SimpleAttributeSet game;
	public static SimpleAttributeSet system;
	public static SimpleAttributeSet wispered;
	public static SimpleAttributeSet broadcasted;
	public static SimpleAttributeSet welcome;
	public static SimpleAttributeSet bb;
	public static SimpleAttributeSet player;
	public static SimpleAttributeSet normal;
	
	public Formatation(){
	key = new SimpleAttributeSet();
	StyleConstants.setForeground(key, Color.YELLOW);
	StyleConstants.setBackground(key,new Color(100,50,100));
	game = new SimpleAttributeSet();
	StyleConstants.setForeground(game, new Color(0,125,255));
	system = new SimpleAttributeSet();
	StyleConstants.setForeground(system,new Color(255,125,0));
	StyleConstants.setBackground(system, new Color(255,230,255));
	wispered = new SimpleAttributeSet();
	StyleConstants.setForeground(wispered,new Color(125,0,255));
	broadcasted = new SimpleAttributeSet();
	StyleConstants.setForeground(broadcasted,Color.RED);
	welcome = new SimpleAttributeSet();
	StyleConstants.setForeground(welcome,new Color(50,150,50));
	bb = new SimpleAttributeSet();
	StyleConstants.setForeground(bb, Color.ORANGE);
	player = new SimpleAttributeSet();
	StyleConstants.setForeground(player, new Color(0,0,255));
	StyleConstants.setBackground(player, new Color(255,255,170));
	normal = new SimpleAttributeSet();
	}
}

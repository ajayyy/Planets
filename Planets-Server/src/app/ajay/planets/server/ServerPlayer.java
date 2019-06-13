package app.ajay.planets.server;

import app.ajay.planets.base.Player;

public class ServerPlayer extends Player {

	/** The frame this payer joined on */
	long startFrame;
	
	public ServerPlayer(int id, float x, float y, long startFrame) {
		super(id, x, y);
		
		this.startFrame = startFrame;
	}

}

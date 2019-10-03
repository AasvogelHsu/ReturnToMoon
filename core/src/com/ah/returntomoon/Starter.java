package com.ah.returntomoon;

import com.badlogic.gdx.Game;


public class Starter extends Game {
	//轉至splashScreen
	@Override
	public void create() {

		setScreen(new splashscreen(this));

	}
}

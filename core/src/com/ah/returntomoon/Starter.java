package com.ah.returntomoon;

import com.badlogic.gdx.Game;


public class Starter extends Game {

	private splashscreen splashscreen;

	//轉至splashScreen
	@Override
	public void create() {
		splashscreen = new splashscreen();
		setScreen(splashscreen);

	}
}

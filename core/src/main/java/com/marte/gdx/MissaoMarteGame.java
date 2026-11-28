package com.marte.gdx;

import com.badlogic.gdx.Game;
import com.marte.domain.Dificuldade;

public class MissaoMarteGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this, Dificuldade.MEDIO, 5));
    }
}

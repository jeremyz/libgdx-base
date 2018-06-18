package ch.asynk.base;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import ch.asynk.base.game.GameScreen;

public class MyGdxBase extends Game
{
    public static final String DOM = "MyGdx";

    private enum State
    {
        NONE,
        GAME
    }
    private State state;

    @Override public void create()
    {
        this.state = State.NONE;
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        debug(String.format("create() [%d;%d] %f", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getDensity()));
        switchToGame();
    }

    @Override public void dispose()
    {
        getScreen().dispose();
        this.state = State.NONE;
    }

    public static void error(String msg)
    {
        Gdx.app.error(DOM, msg);
    }

    public static void debug(String msg)
    {
        Gdx.app.debug(DOM, msg);
    }

    public static void debug(String from, String msg)
    {
        Gdx.app.debug(DOM, String.format("%s : %s", from, msg));
    }

    public void switchToGame()
    {
        if (state != State.NONE) {
            getScreen().dispose();
        }
        setScreen(new GameScreen(this));
        this.state = State.GAME;
    }
}

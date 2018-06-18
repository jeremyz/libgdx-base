package ch.asynk.base.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

import ch.asynk.base.MyGdxBase;

public class GameScreen implements Screen
{
    private final MyGdxBase base;
    private final SpriteBatch batch;
    private final Texture img;
    private boolean paused;

    public GameScreen(final MyGdxBase base)
    {
        this.base = base;
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        paused = false;
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        if (paused) return;
        MyGdxBase.debug("GameScreen", String.format("resize (%d,%d)",width, height));
    }

    @Override public void dispose()
    {
        MyGdxBase.debug("GameScreen", "dispose()");
        batch.dispose();
        img.dispose();
    }

    @Override public void show()
    {
        MyGdxBase.debug("GameScreen", "show()");
    }

    @Override public void hide()
    {
        MyGdxBase.debug("GameScreen", "hide()");
    }

    @Override public void pause()
    {
        paused = true;
        MyGdxBase.debug("pause() ");
    }

    @Override public void resume()
    {
        MyGdxBase.debug("resume() ");
        paused = false;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}

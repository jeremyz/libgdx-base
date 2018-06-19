package ch.asynk.base.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.base.MyGdxBase;

public class GameScreen implements Screen
{
    private static final float INPUT_DELAY = 0.1f;              // filter out touches after gesture
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;

    private final MyGdxBase base;
    private final SpriteBatch batch;
    private final Texture map;
    private final Texture hud;

    private GameCamera camera;
    private Vector2 dragPos = new Vector2();
    private Vector3 mapTouch = new Vector3();
    private Vector3 hudTouch = new Vector3();

    private boolean paused;
    private float inputDelay;
    private boolean inputBlocked;

    public GameScreen(final MyGdxBase base)
    {
        this.base = base;
        this.batch = new SpriteBatch();
        this.hud = new Texture("hud.png");
        this.map = new Texture("map_00.png");
        this.camera = new GameCamera(10, map.getWidth(), map.getHeight(), 1.0f, 0.3f, false);
        Gdx.input.setInputProcessor(getMultiplexer());
        this.paused = false;
        this.inputDelay = 0f;
        this.inputBlocked = false;
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        if (inputBlocked) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                inputBlocked = false;
        }

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.applyMapViewport();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(map, 0, 0);
        batch.end();

        camera.applyHudViewport();
        batch.setProjectionMatrix(camera.getHudMatrix());
        batch.begin();
        int right = camera.getHudLeft() + camera.getHudWidth() - hud.getWidth();
        int top = camera.getHudBottom() + camera.getHudHeight() - hud.getHeight();
        batch.draw(hud, 0, 0);
        batch.draw(hud, right, 0);
        batch.draw(hud, 0, top);
        batch.draw(hud, right, top);
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        if (paused) return;
        MyGdxBase.debug("GameScreen", String.format("resize (%d,%d)",width, height));
        camera.updateViewport(width, height);
    }

    @Override public void dispose()
    {
        MyGdxBase.debug("GameScreen", "dispose()");
        batch.dispose();
        map.dispose();
        hud.dispose();
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
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }

    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean scrolled(int amount)
            {
                camera.zoom(amount * ZOOM_SCROLL_FACTOR);
                return true;
            }
            @Override public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (inputBlocked) return true;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    camera.unproject(x, y, mapTouch);
                    camera.unprojectHud(x, y, hudTouch);
                    MyGdxBase.debug("touchDown MAP : " + mapTouch);
                    MyGdxBase.debug("touchDown HUD : " + hudTouch);
                }
                return true;
            }
            @Override public boolean touchDragged(int x, int y, int pointer)
            {
                int dx = (int) (dragPos.x - x);
                int dy = (int) (dragPos.y - y);
                dragPos.set(x, y);
                camera.translate(dx, dy);
                return true;
            }
        });
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override public boolean zoom(float initialDistance, float distance)
            {
                if (initialDistance > distance)
                    camera.zoom(ZOOM_GESTURE_FACTOR);
                else
                    camera.zoom(-ZOOM_GESTURE_FACTOR);
                inputBlocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));

        return multiplexer;
    }
}

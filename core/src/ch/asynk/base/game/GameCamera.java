package ch.asynk.base.game;

import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;

public class GameCamera extends OrthographicCamera
{
    private static final float ZEROF = 0.01f;

    private int padding;
    private float worldWidth;
    private float worldHeight;
    private float worldAspectRatio;
    private float zoomMax;
    private float zoomMin;
    private int screenWidth;
    private int screenHeight;
    private float widthFactor;
    private float heightFactor;
    private Rectangle viewport;

    public GameCamera(int padding, float worldWidth, float worldHeight, float zoomMax, float zoomMin)
    {
        super(worldWidth, worldHeight);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.padding = padding;
        this.worldAspectRatio = (worldWidth / worldHeight);
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.viewport = new Rectangle();
    }

    public void updateViewport(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float screenAvailableWidth = screenWidth - (2 * padding);
        float screenAvailableHeight = screenHeight - (2 * padding);
        float screenAspectRatio = (screenWidth / (float) screenHeight);
        float diff = (worldAspectRatio - screenAspectRatio);

        if (diff <= -ZEROF) {
            // screen wider than world : use max height, width grows up until max available on zooming
            viewport.height = screenAvailableHeight;
            viewport.width = java.lang.Math.min((screenAvailableHeight * worldAspectRatio /  zoom), screenAvailableWidth);
            viewport.x = padding + ((screenAvailableWidth - viewport.width) / 2f);
            viewport.y = padding;
            // camera aspect ratio must follow viewport aspect ration
            viewportHeight = worldHeight;
            viewportWidth = (viewportHeight * (viewport.width / viewport.height));
        } else if (diff > ZEROF) {
            // word is wider than screen : use max width, height grows up until max available on zooming
            viewport.width = screenAvailableWidth;
            viewport.height = java.lang.Math.min((screenAvailableWidth / worldAspectRatio / zoom), screenAvailableHeight);
            viewport.y = padding + ((screenAvailableHeight - viewport.height) / 2f);
            viewport.x = padding;
            // camera aspect ratio must follow viewport aspect ration
            viewportWidth = worldWidth;
            viewportHeight = (viewportWidth * (viewport.height / viewport.width));
        }

        // ratio viewport -> camera
        widthFactor = (viewportWidth / viewport.width);
        heightFactor = (viewportHeight / viewport.height);

        clampPosition();
        update(true);
    }

    public void applyMapViewport()
    {
        HdpiUtils.glViewport((int)viewport.x, (int)viewport.y, (int)viewport.width, (int)viewport.height);
    }

    public void centerOnWorld()
    {
        position.set((worldWidth / 2f), (worldHeight / 2f), 0f);
    }

    public void zoom(float dz)
    {
        zoom += dz;
        clampZoom();
        updateViewport(screenWidth, screenHeight); // zoom impacts viewport
    }

    public void clampZoom()
    {
        zoom = MathUtils.clamp(zoom, zoomMin, zoomMax);
    }

    public void translate(float dx, float dy)
    {
        float deltaX = (dx * zoom * widthFactor);
        float deltaY = (dy * zoom * heightFactor);
        translate(deltaX, -deltaY, 0);
        clampPosition();
        update(true);
    }

    public void clampPosition()
    {
        float cameraWidth = (viewportWidth * zoom);
        float cameraHeight = (viewportHeight * zoom);

        // on each axis, clamp on [ cameraDim/2 ; worldDim - cameraDim/2 ]

        if ((worldWidth - cameraWidth) > ZEROF) {
            cameraWidth /= 2f;
            position.x = MathUtils.clamp(position.x, cameraWidth, (worldWidth - cameraWidth));
        } else {
            position.x = (worldWidth / 2f);
        }

        if ((worldHeight - cameraHeight) > ZEROF) {
            cameraHeight /= 2f;
            position.y = MathUtils.clamp(position.y, cameraHeight, (worldHeight - cameraHeight));
        } else {
            position.y = (worldHeight / 2f);
        }
    }

    public void unproject(int x, int y, Vector3 v)
    {
        unproject(v.set(x, y, 0), viewport.x, viewport.y, viewport.width, viewport.height);
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public int getViewportLeft()
    {
        return (int) viewport.x;
    }

    public int getViewportBottom()
    {
        return (int) viewport.y;
    }

    public int getViewportWidth()
    {
        return (int) viewport.width;
    }

    public int getViewportHeight()
    {
        return (int) viewport.height;
    }
}

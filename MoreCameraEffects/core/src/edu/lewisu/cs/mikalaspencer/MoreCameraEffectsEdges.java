package edu.lewisu.cs.mikalaspencer;

/**
 * Mikala Spencer
 * 2020-11-04
 * This program utilizes camera effects and edges.
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

abstract class CameraEffect 
{
    protected OrthographicCamera cam;
    protected int duration, progress;
    protected ShapeRenderer renderer;
    protected SpriteBatch batch;

    // Constructor
    public CameraEffect(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer)
    {
        this.cam = cam;
        this.duration = duration;
        this.batch = batch;
        this.renderer = renderer;
        progress = duration;
    }

    public boolean isActive()
    {
        // Returns if the camera is active or not
        return (progress < duration);
    }

    public void updateCamera()
    {
        // Update the camera
        cam.update();

        if (renderer != null)
        {
            // Update renderer
            renderer.setProjectionMatrix(cam.combined);
        }

        if (batch != null)
        {
            // Update batch
            batch.setProjectionMatrix(cam.combined);
        }
    }

    public void start()
    {
        // How a CameraEffect starts
        progress = 0;
    }

    

}

public class MoreCameraEffectsEdges extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    Texture background;
    float imgX, imgY;
    float imgWidth, imgHeight;
    float WIDTH, HEIGHT;
    OrthographicCamera cam;
    float WORLDWIDTH, WORLDHEIGHT;
    LabelStyle labelStyle;
    Label label;

    public void setupLabelStyle() 
    {
        labelStyle = new LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/gameFont1030*.fnt"));
    }

    @Override
    public void create () 
    {
        batch = new SpriteBatch();
        img = new Texture("mikan.png");
        background = new Texture("map.jpg");

        // Viewport or screen
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        
        // Of the world
		WORLDWIDTH = 2*WIDTH;
        WORLDHEIGHT = 2*HEIGHT;
        
        imgX = 0;
        imgY = 0;

        imgWidth = img.getWidth();
        imgHeight = img.getHeight();

        cam = new OrthographicCamera(WIDTH,HEIGHT);
        cam.translate(WIDTH/2,HEIGHT/2);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        System.out.println(cam.position.x + " " + cam.position.y);
        
        // Set up label
        setupLabelStyle();
        // Create the label
        label = new Label("Welcome!", labelStyle);
        // World coordinates == Screen coordinates at the beginning
        label.setPosition(20,400); 
    }

    public void handleInput() 
    {
        if (Gdx.input.isKeyPressed(Keys.A)) 
        {
            imgX-=10;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) 
        {
            imgX+=10;
        }
        if (Gdx.input.isKeyPressed(Keys.W)) 
        {
            imgY+=10;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) 
        {
            imgY-=10; 
        }
        if (Gdx.input.isKeyJustPressed(Keys.J))
        {
            // Jailed in region 1.5x bigger than character on center of screen
            lockCoordinatesJail(WIDTH, HEIGHT);
        }
        if (Gdx.input.isKeyJustPressed(Keys.U))
        {
            // Unlocked from jailed region
            WIDTH = Gdx.graphics.getWidth();
            HEIGHT = Gdx.graphics.getHeight();
        }
        if (Gdx.input.isKeyJustPressed(Keys.SPACE))
        {
            // Camera Effect
        }
    }
    
    public Vector2 getViewPortOrigin() 
    {
		return new Vector2(cam.position.x-WIDTH/2, cam.position.y - HEIGHT/2);
    }
    
    public Vector2 getScreenCoordinates() 
    {
		Vector2 viewportOrigin = getViewPortOrigin();
		return new Vector2(imgX-viewportOrigin.x, imgY-viewportOrigin.y);
    }
    
    public void panCoordinates(float border) 
    {
        Vector2 screenPos = getScreenCoordinates();

        if (screenPos.x > WIDTH - imgWidth - border) 
        {  // About to go off viewport
            if (imgX + imgWidth > WORLDWIDTH - border) 
            {  
                // Out of real estate in potisive x direction
                wrapCoordinates(WORLDWIDTH, WORLDHEIGHT);
            } 
            else 
            {   
                // Just pan the camera because I have more world to explore
                cam.position.x = cam.position.x + screenPos.x - WIDTH + imgWidth + border;
                System.out.println(cam.position.x);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        } 

        if (screenPos.x < border) 
        {   
            if (imgX < -WORLDWIDTH + border) 
            {  
                // Out of real estate in negative x direction
                wrapCoordinates(WORLDWIDTH, WORLDHEIGHT);
            } 
            else 
            {   
                // Just pan the camera because I have more world to explore
                cam.position.x = cam.position.x - (border - screenPos.x);
                System.out.println(cam.position.x);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }

        if (screenPos.y > HEIGHT - imgHeight - border) 
        {   // Go off viewport vertically
            if (imgY + imgHeight > WORLDHEIGHT - border) 
            {  
                // Out of real estate in positive y direction
                lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
            }
            else 
            {   
                // Keep panning we have more room
                cam.position.y = cam.position.y + screenPos.y - HEIGHT + imgHeight + border;
                System.out.println(cam.position.y);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }

        if (screenPos.y < border) 
        {
            if (imgY < -WORLDHEIGHT + border) 
            {  
                // Out of real estate in neagtive y direction
                lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
            }
            else 
            {  
                // Keep panning we have more room
                cam.position.y = cam.position.y - (border - screenPos.y);
                System.out.println(cam.position.y);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }
    }

    public void wrapCoordinates(float targetWidth, float targetHeight) {
        if (imgX > targetWidth) 
        {
            imgX = -targetWidth;
        } 
        else if (imgX < -targetWidth -imgWidth) 
        {
            imgX = targetWidth;
        }
        
        if (imgY > targetHeight) 
        {
            imgY = -targetHeight;
        } 
        else if (imgY < -targetHeight -imgHeight) 
        {
            imgY = targetHeight;
        }
    }

    public void wrapCoordinates() 
    {
        wrapCoordinates(WIDTH, HEIGHT);
    }

    public void lockCoordinatesJail(float targetWidth, float targetHeight)
    {
        // When pressed J
        WIDTH = imgWidth * 1.5f;
        HEIGHT = imgHeight * 1.5f;
    }

    public void lockCoordinatesJail() 
    {
        lockCoordinatesJail(WIDTH, HEIGHT);
    }

    public void lockCoordinates(float targetWidth, float targetHeight) 
    {
        if (imgX > targetWidth - imgWidth) 
        {
            imgX = targetWidth - imgWidth;
        } 
        else if (imgX < -targetWidth) 
        {
            imgX = -targetWidth;
        }

        if (imgY > targetHeight - imgHeight) 
        {
            imgY = targetHeight - imgHeight;
        } 
        else if (imgY < -targetHeight)
        {
            imgY = -targetHeight;
        }   
    }

    public void lockCoordinates() 
    {
        lockCoordinates(WIDTH, HEIGHT);
    }

    @Override
    public void render () 
    {
        Gdx.gl.glClearColor(0, 41/255, 38/255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        panCoordinates(20);

        label.setText("X = " + imgX + ", Y = " + imgY);
        /*
        * Update the label position to ensure that it stays at the same place on 
        * The screen as the camera moves.
        */
        label.setPosition(20+(cam.position.x-WIDTH/2),400+cam.position.y-HEIGHT/2);

        batch.begin();
        batch.draw(background,-1024,-768);
        batch.draw(img, imgX, imgY);
        label.draw(batch,1);
        batch.end();
    }
    
    @Override
    public void dispose () 
    {
        batch.dispose();
        img.dispose();
    }
}
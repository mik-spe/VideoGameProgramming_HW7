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
    protected float imgX, imgY;
    protected ShapeRenderer renderer;
    protected SpriteBatch batch;

    // Constructor
    public CameraEffect(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, float imgX, float imgY)
    {
        this.cam = cam;
        this.duration = duration;
        this.batch = batch;
        this.renderer = renderer;
        this.imgX = imgX;
        this.imgY = imgY;
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

// Camera Effect of moving the camera
class CameraMove extends CameraEffect
{
    private int intensity;
    private int speed;
    private float imgX;
    private float imgY;

    public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, float imgX, float imgY) 
    {
        super(cam, duration, batch, renderer, imgX, imgY);
    }

    public float getImgX()
    {
        return imgX;
    }
    public void setImgX(float imgX)
    {
        this.imgX = imgX;
    }

    public float getImgY()
    {
        return imgY;
    }
    public void setImgY(float imgY)
    {
        this.imgY = imgY;
    }

    public int getIntensity()
    {
        return intensity;
    }
    public void setIntensity(int intensity)
    {
        if (intensity < 0)
        {
            this.intensity = 0;
        }
        else
        {
            this.intensity = intensity;
        }
    }

    public int getSpeed()
    {
        return speed;
    }
    public void setSpeed(int speed)
    {
        if (speed < 0)
        {
            speed = 0;
        }
        else
        {
            if (speed > duration)
            {
                speed = duration / 2;
            }
            else
            {
                this.speed = speed;
            }
        }
    }

    @Override
    public boolean isActive()
    {
        return super.isActive() && speed > 0;
    }

    public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, int intensity, int speed, float imgX, float imgY) 
	{
        super(cam,duration,batch,renderer, imgX, imgY);
        setIntensity(intensity);
        setSpeed(speed);
        setImgX(imgX);
        setImgY(imgY);
	}

    public void play()
    {
        if (isActive())
        {
            if (progress % speed == 0)
            {
                cam.translate(imgX,imgY);
            }

            progress++;
            
            if (!isActive())
            {
                cam.translate(-imgX,-imgY);
            }

            updateCamera();
        }
    }

    public void start()
    {
        super.start();
        cam.translate(imgX,imgY);
        updateCamera();
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
    CameraMove mover;

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
        background = new Texture("mapIsland.jpg");

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

        // Camera Effect
        mover = new CameraMove(cam, 100, batch, null, 10, 2, 10, 10);
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
            // Moves camera to upper right diagonally
            mover.start();
        }
        mover.play();
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
                // Pan the camera
                cam.position.x = cam.position.x + screenPos.x - WIDTH + imgWidth + border;
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
                // Pan the camera
                cam.position.x = cam.position.x - (border - screenPos.x);
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
                // Keep panning
                cam.position.y = cam.position.y + screenPos.y - HEIGHT + imgHeight + border;
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
                // Keep panning
                cam.position.y = cam.position.y - (border - screenPos.y);
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
        // When pressed J, lock the character in center of screen
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

        mover.play();

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
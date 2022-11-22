package com.oshewo.panic.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oshewo.panic.PiazzaPanic;
import com.oshewo.panic.scenes.Hud;
import com.oshewo.panic.sprites.Chef;

public class PlayScreen implements Screen {
    private  PiazzaPanic game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthoCachedTiledMapRenderer renderer;
    private World world;
    private Box2DDebugRenderer b2dr;
    private Chef player;



    public PlayScreen(PiazzaPanic game){
        this.game = game;
        hud = new Hud(game.batch);
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(PiazzaPanic.V_WIDTH,PiazzaPanic.V_HEIGHT, gameCam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("piazza.tmx");
        renderer = new OrthoCachedTiledMapRenderer(map);
        gameCam.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);
        world = new World(new Vector2(0,0),true);
        b2dr = new Box2DDebugRenderer();
        player = new Chef(world);

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight()/2);

            body = world.createBody(bdef);

            shape.setAsBox(rectangle.getWidth()/2,rectangle.getHeight()/2);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight()/2);

            body = world.createBody(bdef);

            shape.setAsBox(rectangle.getWidth()/2,rectangle.getHeight()/2);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }


    public void handleInput(float dt){
        if(Gdx.input.isKeyPressed(32)){
            player.b2body.applyLinearImpulse(new Vector2(4f,0),player.b2body.getWorldCenter(),true);
        }
        if(Gdx.input.isKeyPressed(29)){
            player.b2body.applyLinearImpulse(new Vector2(-4f,0),player.b2body.getWorldCenter(),true);
        }
        if(Gdx.input.isKeyPressed(47)){
            player.b2body.applyLinearImpulse(new Vector2(0,-4f),player.b2body.getWorldCenter(),true);
        }
        if(Gdx.input.isKeyPressed(51)){
            player.b2body.applyLinearImpulse(new Vector2(0,4f),player.b2body.getWorldCenter(),true);
        }
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f,6,2);

        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

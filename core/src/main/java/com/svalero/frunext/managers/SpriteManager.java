package com.svalero.frunext.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.svalero.frunext.FruNext;
import com.svalero.frunext.characters.Enemy;
import com.svalero.frunext.characters.Platform;
import com.svalero.frunext.characters.Player;

public class SpriteManager {
    public FruNext game;
    private Batch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    OrthogonalTiledMapRenderer mapRenderer;
    public Player player;
    public Music music;
    public LevelManager levelManager;
    private boolean levelCleared = false;

    // HUD/Zoom
    private static final float VIEW_W = 640f;
    private static final float VIEW_H = 360f;

    // Sprite de fruta
    private TextureRegion fruitRegion;

    public SpriteManager(FruNext game) {
        this.game = game;
        levelManager = new LevelManager();
        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_W, VIEW_H);
        camera.zoom = 0.85f;
        camera.update();
        Gdx.gl.glCullFace(GL20.GL_CULL_FACE);
        loadCurrentLevel();
    }

    public void loadCurrentLevel() {
        levelCleared = false;
        levelManager.loadCurrentMap();
        mapRenderer = new OrthogonalTiledMapRenderer(levelManager.map);
        batch = mapRenderer.getBatch();

        // Crea jugador
        player = new Player(this);

        // Spawn
        int tw = levelManager.map.getProperties().get("tilewidth", Integer.class);
        int th = levelManager.map.getProperties().get("tileheight", Integer.class);
        int spawnTileX = 2;
        float spawnX = spawnTileX * tw;
        float spawnY = findSpawnY(spawnTileX, tw, th);
        player.position.set(spawnX, spawnY);

        // Reset estados
        Player.stuckPlatform = null;
        player.velocity.set(0, 0);
        player.isRunning = false;
        player.isJumping = false;
        player.canJump = false;

        // Frutas/cofre
        TextureRegion fr = ResourceManager.getAtlas("items/items.pack").findRegion("fruit");
        if (fr == null) fr = ResourceManager.getAtlas("items/items.pack").findRegion("chest");
        fruitRegion = fr;

        // Música
        music = ResourceManager.getMusic("sounds/levels.mp3");
        music.setLooping(true);
        if (ResourceManager.musicEnabled) music.play();
    }

    private float findSpawnY(int tileX, int tw, int th) {
        TiledMapTileLayer layer = TiledMapManager.collisionLayer;
        int h = layer.getHeight();
        for (int y = 1; y < h; y++) {
            Cell above = layer.getCell(tileX, y);
            Cell below = layer.getCell(tileX, y - 1);
            boolean aboveEmpty = (above == null) || !above.getTile().getProperties().containsKey(TiledMapManager.BLOCKED);
            boolean belowSolid = (below != null) && below.getTile().getProperties().containsKey(TiledMapManager.BLOCKED);
            if (aboveEmpty && belowSolid) return y * th;
        }
        return 2 * th + 16;
    }

    public void update(float dt) {
        handleInput();
        if (game.paused) return;

        player.update(dt);

        for (Enemy e : levelManager.enemies) {
            if (!camera.frustum.pointInFrustum(new Vector3(e.position.x, e.position.y, 0))) continue;
            if (e.isAlive) e.update(dt);
        }
        for (Platform p : levelManager.platforms) p.update(dt);

        checkCollisions();
    }

    public void draw() {
        camera.position.set(player.position.x + Player.WIDTH / 2f, VIEW_H / 2f, 0);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();

        if (fruitRegion != null) {
            for (Rectangle f : levelManager.fruits) {
                batch.draw(fruitRegion, f.x, f.y, f.width, f.height);
            }
        }

        player.render(batch);
        for (Enemy e : levelManager.enemies) if (e.isAlive) e.render(batch);
        for (Platform p : levelManager.platforms) p.render(batch);

        float left = camera.position.x - camera.viewportWidth * 0.5f;
        float top = camera.position.y + camera.viewportHeight * 0.5f;
        float x = left + 6;
        float y = top - 6;
        font.getData().setScale(0.9f);

        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("life"), x, y - 16, 16, 16);
        font.draw(batch, ": " + levelManager.currentLives, x + 20, y - 4);
        y -= 22;
        int totalCoinsShown = levelManager.totalCoins + levelManager.currentCoins;
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("coin"), x, y - 16, 16, 16);
        font.draw(batch, ": " + totalCoinsShown, x + 20, y - 4);
        y -= 22;
        font.draw(batch, "LEVEL: " + levelManager.currentLevel, x, y - 4);

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            Player.stuckPlatform = null;
            player.velocity.x = Player.WALKING_SPEED;
            player.state = Player.State.RUNNING_RIGHT;
            if (!player.isJumping) player.isRunning = true;
        } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            Player.stuckPlatform = null;
            player.velocity.x = -Player.WALKING_SPEED;
            player.state = Player.State.RUNNING_LEFT;
            if (!player.isJumping) player.isRunning = true;
        } else {
            if (player.isRunning) {
                player.state = (player.state == Player.State.RUNNING_LEFT) ? Player.State.IDLE_LEFT : Player.State.IDLE_RIGHT;
            }
            player.isRunning = false;
            player.velocity.x = 0;
        }

        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            player.tryJump();
        }
    }

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);

        // Enemigos
        for (Enemy e : levelManager.enemies) {
            if (!e.isAlive) continue;
            Rectangle r = new Rectangle(e.position.x, e.position.y, e.width, e.height);
            if (r.overlaps(playerRect)) {
                if (player.position.y > (e.position.y + 5)) {
                    if (ResourceManager.sfxEnabled) ResourceManager.getSound("sounds/explosion.wav").play();
                    e.dispose();
                    player.tryJump();
                } else {
                    playerDie(); // quita 1 vida como siempre
                    return;
                }
            }
        }

        // Plataformas móviles
        boolean stuck = false;
        for (Platform p : levelManager.platforms) {
            Rectangle pr = new Rectangle(p.position.x, p.position.y, p.width, p.height);
            if (pr.overlaps(playerRect)) {
                if (player.velocity.y < 0 && player.position.y > pr.y) {
                    player.position.y = pr.y + pr.height;
                    player.canJump = true;
                    player.isJumping = false;
                    Player.stuckPlatform = p;
                    stuck = true;
                }
            }
        }
        if (!stuck) Player.stuckPlatform = null;

        // Fruta (objetivo)
        if (!levelCleared) {
            for (Rectangle f : levelManager.fruits) {
                if (f.overlaps(playerRect)) {
                    levelCleared = true;
                    if (music != null) music.stop();
                    if (ResourceManager.musicEnabled)
                        ResourceManager.getMusic("sounds/level_clear.mp3").play();

                    if (levelManager.advanceToNextLevelIfExists()) {
                        mapRenderer.dispose();
                        loadCurrentLevel();
                    } else {
                        Gdx.app.postRunnable(() -> game.setScreen(new com.svalero.frunext.screens.MainMenuScreen(game)));
                    }
                    break;
                }
            }
        }
    }

    public void playerDie() {
        player.velocity.x = player.velocity.y = 0;
        if (music != null) music.stop();
        if (ResourceManager.sfxEnabled)
            ResourceManager.getSound("sounds/player_down.wav").play(0.5f);

        if (levelManager.currentLives == 1) {
            com.badlogic.gdx.Gdx.app.postRunnable(() -> game.setScreen(new com.svalero.frunext.screens.MainMenuScreen(game)));
        } else {
            levelManager.restartCurrentLevel();
            mapRenderer.dispose();
            loadCurrentLevel();
        }
    }

    public void resize(int w, int h) {
        camera.viewportWidth = VIEW_W;
        camera.viewportHeight = VIEW_H;
        camera.zoom = 1.0f;
        camera.update();
    }

    public void dispose() {
        if (music != null) { music.stop(); music.dispose(); }
        font.dispose();
        if (batch != null) batch.dispose();
        levelManager.clearCharactersCurrentLevel();
    }
}

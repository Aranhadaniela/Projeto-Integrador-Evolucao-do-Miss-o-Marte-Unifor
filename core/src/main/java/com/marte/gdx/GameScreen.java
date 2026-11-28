package com.marte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marte.application.Missao;
import com.marte.domain.Asteroide;
import com.marte.domain.Astronauta;
import com.marte.domain.Dificuldade;
import com.marte.domain.Engenheiro;
import com.marte.domain.Inimigo;
import com.marte.domain.Nave;
import com.marte.domain.Passageiro;
import com.marte.domain.Professor;
import java.util.Random;

public class GameScreen extends ScreenAdapter {
    private static final float TILE_SIZE = 44f;

    private final MissaoMarteGame game;
    private final Dificuldade dificuldade;
    private final int radius;
    private final Random random = new Random();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Texture tileTex;
    private final Texture padTex;
    private final Texture shipTex;
    private final Texture paxTex;
    private final Texture astTex;
    private final Texture enemyTex;

    private Missao missao;
    private int score;
    private String hudStatus;
    private boolean finished;

    public GameScreen(MissaoMarteGame game, Dificuldade dificuldade, int radius) {
        this.game = game;
        this.dificuldade = dificuldade;
        this.radius = radius;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(960, 640, camera);
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);

        this.tileTex = box(Color.valueOf("1b2238"), Color.valueOf("283252"));
        this.padTex = box(Color.valueOf("1d6f5f"), Color.valueOf("50f2c4"));
        this.shipTex = box(Color.valueOf("3b82f6"), Color.WHITE);
        this.paxTex = box(Color.valueOf("f59e0b"), Color.valueOf("fef3c7"));
        this.astTex = box(Color.valueOf("6b7280"), Color.valueOf("374151"));
        this.enemyTex = box(Color.valueOf("ef4444"), Color.valueOf("fecaca"));

        resetMatch();
    }

    private Texture box(Color fill, Color border) {
        Pixmap pm = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pm.setColor(fill);
        pm.fill();
        pm.setColor(border);
        pm.drawRectangle(0, 0, 32, 32);
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    private void resetMatch() {
        this.missao = new Missao(new Nave("A-1", 5));
        this.score = (dificuldade == Dificuldade.FACIL) ? 30 : (dificuldade == Dificuldade.DIFICIL ? 45 : 20);
        this.hudStatus = "WASD/Setas: mover | C: embarcar | R: reiniciar";
        this.finished = false;

        int totalPax = (dificuldade == Dificuldade.FACIL) ? 4 : 5;
        int hazards = (dificuldade == Dificuldade.FACIL) ? 1 : (dificuldade == Dificuldade.DIFICIL ? 3 : 2);

        while (missao.getPassageiros().size() < totalPax) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(x, y)) continue;
            int i = missao.getPassageiros().size();
            if (i % 5 == 4) missao.addPassageiro(new Astronauta("Ast. Maria", x, y));
            else if (i % 2 == 1) missao.addPassageiro(new Engenheiro("Eng. " + i, x, y));
            else missao.addPassageiro(new Professor("Prof. " + i, x, y));
        }
        while (missao.getAsteroides().size() < hazards) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }
        while (missao.getInimigos().size() < hazards) {
            int x = random.nextInt(radius * 2 + 1) - radius;
            int y = random.nextInt(radius * 2 + 1) - radius;
            if ((x == 0 && y == 0) || occupied(x, y)) continue;
            missao.addInimigo(new Inimigo(x, y));
        }
    }

    private boolean occupied(int x, int y) {
        Nave n = missao.getNave();
        if (n.getX() == x && n.getY() == y) return true;
        for (Passageiro p : missao.getPassageiros()) if (p.getX() == x && p.getY() == y) return true;
        for (Asteroide a : missao.getAsteroides()) if (a.getX() == x && a.getY() == y) return true;
        for (Inimigo i : missao.getInimigos()) if (i.getX() == x && i.getY() == y) return true;
        return false;
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetMatch();
            return;
        }
        if (finished) return;

        Nave nave = missao.getNave();
        char dir = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) dir = 'w';
        else if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) dir = 's';
        else if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) dir = 'a';
        else if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) dir = 'd';

        boolean acted = false;
        if (dir != 0) {
            nave.moverComLimites(dir, -radius, radius, -radius, radius);
            score--;
            acted = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Passageiro p = missao.passagemNaPosicao();
            if (p != null && missao.embarcarPassageiroNaPosicao()) {
                score += p.getPontuacao();
                hudStatus = "Resgatou " + p.getNome() + " (+" + p.getPontuacao() + " pts)";
            } else {
                hudStatus = "Sem passageiro aqui ou nave cheia.";
            }
            acted = true;
        }

        if (acted) {
            missao.moverInimigos(random, -radius, radius, -radius, radius);
            if (missao.verificaColisao()) {
                nave.perderVida();
                hudStatus = "Colisao! Vidas restantes: " + nave.getVidas();
            }
            if (nave.getVidas() <= 0 || score <= 0) {
                finished = true;
                hudStatus = "Derrota! Pressione R para tentar de novo.";
            } else if (missao.vitoriaAlcancada()) {
                finished = true;
                hudStatus = "Vitoria em (0,0)! Pressione R para jogar de novo.";
            } else if (missao.todosEmbarcados()) {
                hudStatus = "Todos a bordo! Volte para (0,0).";
            }
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        Gdx.gl.glClearColor(0.06f, 0.08f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float startX = (viewport.getWorldWidth() - (radius * 2 + 1) * TILE_SIZE) / 2f;
        float startY = (viewport.getWorldHeight() - (radius * 2 + 1) * TILE_SIZE) / 2f - 20f;

        for (int gx = -radius; gx <= radius; gx++) {
            for (int gy = -radius; gy <= radius; gy++) {
                float px = startX + (gx + radius) * TILE_SIZE;
                float py = startY + (radius - gy) * TILE_SIZE;
                batch.draw(tileTex, px, py, TILE_SIZE - 2, TILE_SIZE - 2);
                if (gx == 0 && gy == 0) batch.draw(padTex, px, py, TILE_SIZE - 2, TILE_SIZE - 2);
            }
        }
        for (Asteroide a : missao.getAsteroides()) drawObj(a.getX(), a.getY(), startX, startY, astTex);
        for (Passageiro p : missao.getPassageiros()) drawObj(p.getX(), p.getY(), startX, startY, paxTex);
        for (Inimigo i : missao.getInimigos()) drawObj(i.getX(), i.getY(), startX, startY, enemyTex);
        Nave nave = missao.getNave();
        drawObj(nave.getX(), nave.getY(), startX, startY, shipTex);

        font.setColor(Color.WHITE);
        font.draw(batch, String.format("Dif: %s | Pts: %d | Vidas: %d | A bordo: %d/%d",
                dificuldade, score, nave.getVidas(), nave.getPassageiros().size(), nave.getCapacidade()),
                24f, viewport.getWorldHeight() - 24f);

        font.setColor(Color.GOLD);
        font.draw(batch, hudStatus, 24f, 34f);
        batch.end();
    }

    private void drawObj(int gx, int gy, float startX, float startY, Texture tex) {
        batch.draw(tex, startX + (gx + radius) * TILE_SIZE + 4f,
                startY + (radius - gy) * TILE_SIZE + 4f, TILE_SIZE - 10f, TILE_SIZE - 10f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        tileTex.dispose();
        padTex.dispose();
        shipTex.dispose();
        paxTex.dispose();
        astTex.dispose();
        enemyTex.dispose();
    }
}

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	//Variaveis
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture logo;
	private Texture ring;
	private Texture emerald;
	private Texture coletavelAtual;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle circuloRing;
	private Circle circuloEmerald;
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima = 0;
	private boolean passouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;
	private float posicaoRingx = 0;
	private float posicaoRingy = 0;
	private float valorRing = 5;
	private float valorEmerald = 10;
	private int speed = 200;
	private float escalaColetavel = 0.5f;


	BitmapFont textoPontucao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somColetavel;

	Preferences preferencias;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720; //720
	private final float VIRTUAL_HEIGHT = 1280; //1280

	//Iniciar Texturas e os objetos
	@Override
	public void create() {
		inicializarTexturas();
		inicializaObjetos();
	}
	//
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}
	//iniciar todas os sprits do jogo
	private void inicializarTexturas() {
		passaros = new Texture[3];
		passaros[0] = new Texture("Sonic1-removebg-preview.png");
		passaros[1] = new Texture("Sonic2-removebg-preview.png");
		passaros[2] = new Texture("Sonic3-removebg-preview.png");

		fundo = new Texture("fundo.png");
		logo = new Texture("img.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		ring = new Texture("Ring.png");
		emerald = new Texture("Esmeraldas.png");

		coletavelAtual = emerald;
	}

	private void inicializaObjetos() {
		batch = new SpriteBatch();
		random = new Random();

		//Alocar valores nas variaveis
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoRingx = posicaoCanoHorizontal + larguraDispositivo/2;
		posicaoRingy = larguraDispositivo/2;
		espacoEntreCanos = 350;

		//Configurar a fonte da pontuação
		textoPontucao = new BitmapFont();
		textoPontucao.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		textoPontucao.getData().setScale(5);

		//Configurar fonte  para reiniciar jogo
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(com.badlogic.gdx.graphics.Color.GREEN);
		textoReiniciar.getData().setScale(2);

		//Configurar fonte do record
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(com.badlogic.gdx.graphics.Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloRing = new Circle();
		circuloEmerald = new Circle();

		//Iniciar sons
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somColetavel = Gdx.audio.newSound(Gdx.files.internal("Sonic_Moeda.mp3"));

		preferencias = Gdx.app.getPreferences("FlappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}
	//Função que muda o estado do jogo
	private void verificarEstadoJogo() {
		boolean toqueTela = Gdx.input.justTouched();
		//Estado 0: Toque para iniciar o jogo
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
			//Estado 1: Jogo
		} else if (estadoJogo == 1) {
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();

			}
			//fazer os canos aparecer em lugares aleatorios e fazendo eles andarem
			posicaoRingx -= Gdx.graphics.getDeltaTime() * speed;
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * speed;
			if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			if(posicaoRingx < -coletavelAtual.getWidth()/2 * escalaColetavel){
				resetColetavel();
			}
			//Responsavel por fazer o passaro dar os pulinhos
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;

			if(pontos > 20 && pontos < 40){
				speed = 400;
			}else if (pontos > 40){
				speed = 600;
			}

			if (posicaoInicialVerticalPassaro < 0 || posicaoInicialVerticalPassaro > alturaDispositivo) {
				estadoJogo = 2;
			}


			//Estado 2: Tela de game over
		} else if (estadoJogo == 2) {
			if (pontos > pontuacaoMaxima) {
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			//Reiniciar jogo
			if (toqueTela) {
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoHorizontal = larguraDispositivo;
				resetColetavel();
				speed = 200;
			}

		}
	}
	private void detectarColisoes()
	{
		//criando colisão do passaro
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2);

		//criando colisão dos canos
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal, alturaDispositivo /2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight());

		//criando colisão do coletavel
		circuloRing.set(
				posicaoRingx - ((coletavelAtual.getWidth()*escalaColetavel)/2),
				posicaoRingy - ((coletavelAtual.getWidth()*escalaColetavel)/2),
				(coletavelAtual.getWidth()*escalaColetavel)/2
		);
		//boolean para detectar se o player colidiu com algo
		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuRing = Intersector.overlaps(circuloPassaro,circuloRing);
		boolean colidiuEmerald = Intersector.overlaps(circuloPassaro,circuloEmerald);

		//caso colida com um coletavel verificar qual coletavel é e somar os pontos
		if(colidiuRing == true || colidiuEmerald == true){
			if(coletavelAtual == emerald) pontos +=valorEmerald;
			else pontos += valorRing;
			posicaoRingy = alturaDispositivo * 2;
			somColetavel.play();
		}
		//caso colida com um cano o jogo acaba
		if (colidiuCanoCima || colidiuCanoBaixo) {
			if (estadoJogo ==1) {
				somColisao.play();
				estadoJogo = 2;
			}
		}
	}
//
	private void desenharTexturas()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//Cria o fundo
		batch.draw(fundo, 0 , 0 , larguraDispositivo, alturaDispositivo);
		//Desenha o passaro e as variaveis
		batch.draw(passaros[(int) variacao],
				50 + posicaoHorizontalPassaro,posicaoInicialVerticalPassaro);
		//Desenha os canos
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);
		textoPontucao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo -110);

		//Desenhar o logo do jogo no começo
		if(estadoJogo == 0)
		{
			batch.draw(logo, larguraDispositivo / 2 - logo.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para iniciar!", larguraDispositivo/2 -140, alturaDispositivo /2 - gameOver.getHeight()/2);
		}
		//Desenhar tela de game over
		if(estadoJogo == 2)
		{
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para reiniciar!", larguraDispositivo/2 -140, alturaDispositivo /2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é: "+ pontuacaoMaxima+" pontos", larguraDispositivo/2 -140,alturaDispositivo/2 - gameOver.getHeight());

		}
		//Desenhar as moedas durante o jogo
		if(estadoJogo == 1){
			batch.draw(coletavelAtual,
					posicaoRingx - (coletavelAtual.getWidth()*escalaColetavel),
					posicaoRingy - (coletavelAtual.getHeight()*escalaColetavel),
					coletavelAtual.getWidth()*escalaColetavel,
					coletavelAtual.getHeight()*escalaColetavel);
		}
		batch.end();
	}

	private void validarPontos()
	{
		//Validar pontos ao passaro passar cano
		if(posicaoCanoHorizontal < 50-passaros[0].getWidth())
		{
			if(!passouCano)
			{
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}
		variacao += Gdx.graphics.getDeltaTime()*10;

		if(variacao > 3)
			variacao = 0;
	}
	private void resetColetavel(){
		//apagar a moeda quando sair da tela caso o jogador não colete
	posicaoRingx = posicaoCanoHorizontal + canoBaixo.getWidth() + coletavelAtual.getWidth() + random.nextInt((int)(larguraDispositivo - (coletavelAtual.getWidth()*escalaColetavel)));
	posicaoRingy = coletavelAtual.getHeight()/2 + random.nextInt((int) alturaDispositivo - coletavelAtual.getHeight() /2);

	//ramdomizar as moedas q vão aparecer
	int randomColetavelNova = random.nextInt(100);
	if(randomColetavelNova < 30){
		coletavelAtual = emerald;
	}
	else{
		coletavelAtual = ring;
	}
	}

	@Override
	//adaptar a tela do jogo para o tamanho do dispositivo
	public void resize(int width, int height)
	{viewport.update(width,height);}

	@Override
	public void dispose()
	{

	}
}

package dam.gala.damgame.model;

import androidx.annotation.NonNull;
import dam.gala.damgame.activities.GameActivity;
import dam.gala.damgame.data.DataBaseHelper;
import dam.gala.damgame.data.DatabaseManager;
import dam.gala.damgame.utils.GameUtil;
import dam.gala.damgame.views.CrashView;
import dam.gala.damgame.scenes.CityScene;
import dam.gala.damgame.views.QuestionView;
import dam.gala.damgame.scenes.Scene;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * Jugada, recoge las principales características del juego
 * @author 2º DAM - IES Antonio Gala
 * @version 1.0
 */
public class Play {
    private final int SCORE_POINTS = 10;
    private final int SCORE_LIFES = 11;
    private final int SCORE_ANSWERS = 12;
    private LocalDateTime starDateTime;
    private LocalDateTime endDateTime;
    private Scene scene;
    private Player player;
    private GameConfig config;
    private int questionsCaptured = 0;
    private int questionsCreated = 0;
    private int crashBlockCreated = 0;
    private int points;
    private int lifes;
    private int level;
    private GameActivity gameActivity;
    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<QuestionView> questionViews = new ArrayList<>();
    private ArrayList<CrashView> crashViews = new ArrayList<>();
    private boolean hasWonLife = false;

    /**
     * Constructor privado para la factoría
     */
    private Play(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
        this.lifes = 3;
    }
    public boolean isHasWonLife() {
        return hasWonLife;
    }
    /**
     * Crea la jugada a partir de la escena elgida. Es un método de factoría.
     * @param gameActivity Actividad principal del juego
     * @param sceneCode Escena elegida, código equivalente
     * @return La jugada actual del juego (Play)
     */
    public static Play createGameMove(@NonNull GameActivity gameActivity, int sceneCode) {
        Play play = new Play(gameActivity);
        play.questions = new ArrayList<>();
        switch (sceneCode) {
            case GameUtil.TEMA_DESIERTO:
                break;
            case GameUtil.TEMA_ESPACIO:
                break;
            case GameUtil.TEMA_CIUDAD:
                play.scene = new CityScene(gameActivity);
                break;
            case GameUtil.TEMA_HIELO:
                break;
            case GameUtil.TEMA_SELVA:
                break;
            case GameUtil.TEMA_SUBMARINO:
                break;
            case GameUtil.TEMA_VOLCANES:
                break;
            default:
                play.scene = new CityScene(gameActivity);
                break;
        }
        return play;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos la escena del juego
    //-----------------------------------------------------------------------------------------
    public Scene getScene() {
        return scene;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos la creación de preguntas
    //-----------------------------------------------------------------------------------------
    public int getQuestionsCreated() {
        return questionsCreated;
    }
    public void setQuestionsCreated(int questionsCreated) {
        this.questionsCreated = questionsCreated;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos para propiedades de estadísticas de juego
    //-----------------------------------------------------------------------------------------
    public int getQuestionsCaptured() {
        return questionsCaptured;
    }
    public void setQuestionsCaptured(int questionsCaptured) {
        this.questionsCaptured = questionsCaptured;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public LocalDateTime getStarDateTime() {
        return starDateTime;
    }
    public void setStarDateTime(LocalDateTime starDateTime) {
        this.starDateTime = starDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos para configuración del juego
    //-----------------------------------------------------------------------------------------
    public GameConfig getConfig() {
        return config;
    }
    public void setConfig(GameConfig config) {
        this.config = config;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos para preguntas
    //-----------------------------------------------------------------------------------------
    public ArrayList<QuestionView> getQuestionViews() {
        return questionViews;
    }

    public int getCrashBlockCreated() {
        return crashBlockCreated;
    }

    public void setCrashBlockCreated(int crashBlockCreated) {
        this.crashBlockCreated = crashBlockCreated;
    }
    public ArrayList<CrashView> getCrashViews() {
        return crashViews;
    }
    //-----------------------------------------------------------------------------------------
    //Métodos getter y setters para la vida del OGP
    //-----------------------------------------------------------------------------------------
    public int getLifes() {
        return lifes;
    }
    public void setLifes(int lifes) {
        this.lifes = lifes;
        this.updateScore(SCORE_LIFES, this.getLifes());
    }
    //-----------------------------------------------------------------------------------------
    //Métodos getter y setters para la vida del OGP
    //-----------------------------------------------------------------------------------------
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    //-----------------------------------------------------------------------------------------
    //Estado de finalización del juego
    //-----------------------------------------------------------------------------------------
    public boolean isFinished() {
         return this.lifes == 0 || (this.scene.getNextImgIndex() < this.scene.getCurrentImgIndex()
                && this.scene.getNextImgIndex() == 0);
    }
    /**
     * Actualiza los datos de puntuación, vidas y respuestas
     * @param what  Lo que se quiere actualizar
     * @param value Valor de actualización
     */
    private void updateScore(final int what, final Object value) {
        this.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (what) {
                    case SCORE_ANSWERS:
                        break;
                    case SCORE_LIFES:
                        Play.this.gameActivity.updateLifes((Integer) value);
                        break;
                    case SCORE_POINTS:
                        break;
                }
            }
        });
    }

    /**
     * Este método accede a la base de datos, toma el número de preguntas que vayamos a precisar
     * para la partida de forma aleatoria y las introduce en el array que irá proporcionandole al
     * jugador las preguntas a contestar
     */
    private void getAllQuestions(){
        DataBaseHelper databaseHelper = new DataBaseHelper(this.gameActivity,null,null, 1);
        DatabaseManager databaseManager = new DatabaseManager(databaseHelper);
        ArrayList<Question> questions = databaseManager.getQuestions();
        ArrayList<Question> selected = new ArrayList<>();
        Random random = new Random();
        while(selected.size() < this.config.getQuestions()){
            int rndNum = random.nextInt(questions.size());
            if(!selected.contains(rndNum))
                selected.add(questions.get(rndNum));
        }
        this.questions = selected;
    }
    public ArrayList<Question> getQuestions(){
        return this.questions;
    }
}

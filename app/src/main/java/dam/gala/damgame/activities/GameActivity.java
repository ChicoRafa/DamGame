package dam.gala.damgame.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import dam.gala.damgame.R;
import dam.gala.damgame.controllers.AudioController;
import dam.gala.damgame.data.DataBaseHelper;
import dam.gala.damgame.data.DatabaseManager;
import dam.gala.damgame.fragments.QuestionDialogFragment;
import dam.gala.damgame.interfaces.InterfaceDialog;
import dam.gala.damgame.model.GameConfig;
import dam.gala.damgame.model.Play;
import dam.gala.damgame.model.Question;
import dam.gala.damgame.scenes.Scene;
import dam.gala.damgame.utils.GameUtil;
import dam.gala.damgame.views.GameView;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Actividad principal
 *
 * @author 2º DAM - IES Antonio Gala
 * @version 1.0
 */
public class GameActivity extends AppCompatActivity implements InterfaceDialog {
    private final int SETTINGS_ACTION = 1;
    private Play gameMove;
    private int sceneCode;
    private GameView gameView;
    private GameConfig config;
    private Scene scene;
    private AudioController audioController;
    //Vista para la puntuación;
    private TextView tvPoints;
    private ImageView ivPoints;
    //Array para las vidas;
    private ArrayList<ImageView> lifes;
    //Vista para las respuestas;
    private TextView tvAnswers;
    private ImageView ivAnswers;

    /**
     * Método de callback del ciclo de vida de la actividad, llamada anterior a que la actividad
     * pasé al estado 'Activa'
     *
     * @param savedInstanceState Contenedor para paso de parámetros y guardar información entre
     *                           distintos estados de la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTema();
        setContentView(R.layout.activity_main);

        Button btnComenzar = findViewById(R.id.btIniciar);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preferences = new Intent(GameActivity.this, SettingsActivity.class);
                startActivityForResult(preferences, SETTINGS_ACTION);
            }
        });
      /*  super.onCreate(savedInstanceState);
        setTema();
        setContentView(R.layout.activity_main);

        Button btIniciar = findViewById(R.id.btIniciar);
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }

        });
*/
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                GameActivity.this.gameView.endGame(true);
                finish();
                System.exit(0);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    /**
     * Inicio del juego
     */
    private void startGame() {
        //configuración general
        this.sceneCode = Integer.parseInt(getDefaultSharedPreferences(this).
                getString("ambient_setting", String.valueOf(GameUtil.TEMA_CIUDAD)));
        this.gameMove = Play.createGameMove(this, this.sceneCode);
        this.scene = this.gameMove.getScene();
        this.config = new GameConfig(this.scene);
        this.getPlay().setConfig(this.config);
        //contenido del layout
        setContentView(R.layout.activity_game);
        //vista dle juego
        this.gameView = findViewById(R.id.svGame);
        //Esconder interfaz de usuario
        hideSystemUI();
        // audio
        this.audioController = this.gameView.getAudioController();
        this.audioController.startSceneAudioPlay();
        //puntuación
        this.loadScoreComponents();
    }

    /**
     * Carga las imágenes del marcador de vidas, puntos y respuestas
     */
    private void loadScoreComponents() {
        //vidas
        this.lifes = new ArrayList<>();
        this.lifes.add(findViewById(R.id.ivBouncy1));
        this.lifes.add(findViewById(R.id.ivBouncy2));
        this.lifes.add(findViewById(R.id.ivBouncy3));
        //puntuación de preguntas
        this.ivAnswers = findViewById(R.id.ivAnswers);
        this.ivAnswers.setImageBitmap(this.scene.getScoreAnswers());
        //puntucación general
        this.ivPoints = findViewById(R.id.ivPoints);
        this.ivPoints.setImageBitmap(this.scene.getScorePoints());
    }
    /**
     * Muestra el cuadro de diálogo de la pregunta
     */
   /* private void showQuestionDialog(){
        //código para probar el cuadro de diálogo
        Button btPregunta = findViewById(R.id.btIniciar);

        MediaPlayer mediaPlayerJuego = MediaPlayer.create(this, R.raw.my_street);
        mediaPlayerJuego.setLooping(true);
        mediaPlayerJuego.start();

        btPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showQuestionDialog();
                startGame();
            }
        });
        //Prueba commit
        //aquí habrá que obtener aleatoriamente una pregunta
        //del repositorio
        CharSequence[] respuestas = new CharSequence[3];
        String enunciado = "Selecciona la respuesta correcta a 2+2";
        respuestas[0]="5";
        respuestas[1]="2";
        respuestas[2]="4";
        int[] respuestasCorrectas = new int[]{1};
        Question question = new Question(enunciado, GameUtil.PREGUNTA_COMPLEJIDAD_ALTA,
                GameUtil.PREGUNTA_SIMPLE,respuestas,respuestasCorrectas,20);

        QuestionDialogFragment qdf = new QuestionDialogFragment(question, GameActivity.this);
        qdf.setCancelable(false);
        qdf.show(getSupportFragmentManager(),null);
    }*/

    /**
     * Establece el tema seleccionado en las preferencias, en este caso, el tema será el de la ciudad
     */
    private void setTema() {
        this.sceneCode = Integer.parseInt(getDefaultSharedPreferences(this).
                getString("theme_setting", String.valueOf(GameUtil.TEMA_CIUDAD)));
        switch (this.sceneCode) {
            case GameUtil.TEMA_CIUDAD:
                setTheme(R.style.City_DamGame);
                break;
            default:
                setTheme(R.style.City_DamGame);
                break;
        }

    }

    /**
     * Se oculta interfaz de usuario y el foco de la ventana en el movil
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /**
     * Elimina la barra de acción y deja el mayor área posible de pantalla libre
     */
    public void hideSystemUI() {
        // Activa el modo inmersivo normal.
        // para el modo "lean back", elimine SYSTEM_UI_FLAG_IMMERSIVE.
        // o para "sticky immersive," reemplacelo por SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // establece el contenido de forma que aparezca bajo las barras de sistema,
                        // con el fin de que no se redimensionen al esconder y mostrar las barras
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // esconde barra de navegación y de estado
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * Obtiene la jugada actual
     *
     * @return Devuelve la jugada actual (Play)
     */
    public Play getPlay() {
        return this.gameMove;
    }

    /**
     * Obtiene la configuración del juego
     *
     * @return Devuelve la configuración del juego (GameConfig)
     */
    public GameConfig getGameConfig() {
        return this.config;
    }

    /**
     * Obtiene el controlador audio
     *
     * @return Devuelve el controlador de audio del juego (AudioController)
     */
    public AudioController getAudioController() {
        return this.audioController;
    }

    @Override
    public void setRespuesta(String respuesta) {
        Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show();
        //si la respuesta es correcta hay que actualizar los puntos y las preguntas respondidas
        this.gameView.setStopGame(false);
        this.gameView.restart();
    }

    /**
     * Menú principal de la aplicación
     *
     * @param menu Menú de aplicación
     * @return Devuelve true si se ha creado el menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Evento de selección de elemento de menú
     *
     * @param item Item de menú
     * @return Devuelve true si se ha tratado el evento recibido
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.imSettings:
                Intent preferences = new Intent(GameActivity.this, SettingsActivity.class);
                startActivityForResult(preferences, SETTINGS_ACTION);
                break;
        }
        return true;
    }

    /**
     * Método de callback para recibir el resultado de una intención llamada para devolver un
     * resultado
     *
     * @param requestCode Código de la petición (int)
     * @param resultCode  Código de respuesta (int)
     * @param data        Intención que devuelve el resultado, la que produce el callback
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
    }

    /**
     * Actualiza las imágenes de las vidas disponibles, oculta la última imagen de las vidas
     * @param index Índice la imagen a ocultar
     */
    /**
     * Actualiza las imágenes de las vidas disponibles, oculta la última imagen de las vidas
     *
     * @param index Índice la imagen a ocultar
     */
    public void updateLifes(Integer index) {
        if (index >= 0)
            this.lifes.get(index).setVisibility(View.INVISIBLE);
    }
}
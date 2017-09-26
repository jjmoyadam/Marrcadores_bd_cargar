package jjmoya.marrcadores_bd_cargar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


//clases que se van implementando
import java.io.IOException;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity_Main extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button bt_mostrar;
    private Button bt_ocultar;
    private ProgressDialog pDialog = null; // barra de progreso (mostrada mientras se conecta a la BD)

    private List lmarcadores;
    private List lborrarmarcadores;

    //rutas para consulta de datos
    private final String servidor = "http://192.168.1.135";
    private final String rutaPhpConsultaTotal = "/00ejemplos/coordenadas/selectAll.php";

    /**
     * Carga de la actividad e inicializacion de las variables
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps__main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        //inicializamos el array de los marcadores
        lmarcadores = new ArrayList();

        //asociacion del boton mostrar

        bt_mostrar = (Button) findViewById(R.id.buttonmostrar);
        bt_ocultar = (Button) findViewById(R.id.buttonOcultar);

        //onclick mostrar
        bt_mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new WebService_Mostrar_Coordenadas(MapsActivity_Main.this).execute();

            }
        });

        //OnClick ocultar botones
        bt_ocultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                borrarPuntos();

                Toast tostada = Toast.makeText(getApplicationContext(), "Ocultar Marcadores", Toast.LENGTH_LONG);
                tostada.show();
            }
        });
    }

    /**
     * Carga del mapa
     * para los marcadores
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //lo creamos hibrido
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Marcador de posicion Las Coordenadas son latitud y longitud
        LatLng huecija = new LatLng(36.9678433, -2.6096244000000297);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huecija, 15f));


    }

    /**
     * Metodo que hace una consulta la base de datos para mostrar los puntos
     *
     * @return String de un json para mostrar marcadores
     */
    private String mostrar() {
        String resquest = "";
        HttpClient httpclient;
        HttpPost httpPost;
        httpclient = new DefaultHttpClient();

        //direccion para la inserccion de los datos en la base de datos
        httpPost = new HttpPost(servidor + rutaPhpConsultaTotal);


        //ahora recogemos los datos de la bd para mostrarlo por pantalla
        try {
            //creacion del manejador para la respuesa.
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            //ejecutamos la consulta
            resquest = httpclient.execute(httpPost, responseHandler);


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return resquest;

    }

    /**
     * Metodo que filtra los datos para recurerar los datos de un json y cargar los datos en un array de objetos marcador
     *
     * @return Array de objetos marcador.
     */
    private boolean filtrarDatos() {

        //boolean para el resultado
        boolean resul = false;

        //limpiamos marcadores
        lmarcadores.clear();


        //recogemos el json de los marcadores
        String respuesta = mostrar();

        //si mostrar no esta vacio
        if (!respuesta.equalsIgnoreCase("")) {

            //creacion del objeto json
            JSONObject json;

            //variable para controlar el error del json
            boolean error_json = false;

            try {

                //instanciamos el json y le pasamos la respuesta llenarlo
                json = new JSONObject(respuesta);

                //creamos un array json para recoger los datos del array coordenadas
                JSONArray jsonArray = json.optJSONArray("coordenadas");

                //for para recorrer los datos de cada una de las filas del jsonArray
                for (int i = 0; i < jsonArray.length(); i++) {

                    //Creacion del objeto
                    Coordenadas coordenadas1 = new Coordenadas();

                    //obtenemos el elemento i del json array para cargarlo en el objeto coordenadas
                    JSONObject jsonArraychild = jsonArray.getJSONObject(i);

                    //guardamos los datos en un objeto de tipo
                    coordenadas1.setLatitud(jsonArraychild.optDouble("latitud"));
                    coordenadas1.setLongitud(jsonArraychild.optDouble("longitud"));
                    coordenadas1.setNombre(jsonArraychild.optString("descripcion"));

                    //y añadimos a un arraylist de objetos de coordenadas
                    lmarcadores.add(coordenadas1);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }

            //devolvemos el objeto json si no hay error en la creacion del objeto json

            if (error_json)
                resul = false;
            else
                resul = true;

        } else
            resul = false;

        return resul;
    }

    /**
     * metodo para Borrar los marcadores  las coordenadas del mapa
     */
    private void borrarPuntos() {

        //y borramos el marcador
        mMap.clear();


    }


    /**
     * metodo para mostar las coordenadas del mapa
     */
    private void mostarPuntos() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //recorremos el arraylist de coordenadas
                for (int i = 0; i < lmarcadores.size(); i++) {

                    //recogemos del arraylist cada objeto
                    Coordenadas coordenadas2 = (Coordenadas) lmarcadores.get(i);

                    //Cargamos los datos de los marcadores
                    String descripcion = coordenadas2.getNombre().toString();
                    Double latitud = coordenadas2.getLatitud().doubleValue();
                    Double longitud = coordenadas2.getLongitud().doubleValue();

                    //dibujamos la marca

                    LatLng marca = new LatLng(latitud, longitud);
                    mMap.addMarker(new MarkerOptions().position(marca).title(descripcion));

                }

            }
        });

    }

    /**
     * Metodo para mostrar mensajes
     *
     * @param mensaje
     */
    public void tostada(String mensaje) {
        Toast toast1 = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT);
        toast1.show();
    }


    //MOSTRAR................ASYNTASK.............
    class WebService_Mostrar_Coordenadas extends AsyncTask<String, String, String> {

        //Activity
        private Activity context;


        WebService_Mostrar_Coordenadas(Activity context) {

            this.context = context;

        }

        // ANTES DE EJECUTAR mostramos una barra de progreso para cargar los datos si es necesario
        @Override
        protected void onPreExecute() {

            // Crea la barra de progreso si es necesario
            if (pDialog == null)
                pDialog = new ProgressDialog(MapsActivity_Main.this);
            pDialog.setMessage("Conectando a la Base de Datos....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        // EJECUTA Tarea a realizar en segundo plano (con otro hilo que no está en el Interfaz de Usuario)
        // por lo tanto esta tarea no puede interaccionar con el usuario
        @Override
        protected String doInBackground(String... strings) {

            String resultado = "ERROR";

            //filtramos los datos para mostrarlos
            if (filtrarDatos()) {

                resultado = "OK";

            } else

                resultado = "ERROR";

            return resultado;

        }

        /**
         * mostramos los datos en la UI si el metodo filtrar datos nos devuelve OK
         * Mediante el metodo Consulta_datos persona
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            //borramos la barra de dialogo
            pDialog.dismiss();

            // si el resultado del if anterior es ok mostramos los datos
            if (result.equals("OK")) {
                //mostramos los puntos del mapa
                mostarPuntos();

            } else {

                tostada("NO SE PUEDEN MOSTRAR MARCADORES");

            }

        }
    }


}

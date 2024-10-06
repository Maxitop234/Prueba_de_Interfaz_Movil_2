package com.example.prueba1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.google.android.material.snackbar.Snackbar;

import java.util.List;


import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
     SensorEvent event;
     EditText Texto_Nombre, Texto_Rut, Texto_Incidente;
     Button Grabar,Cerrar;
     Snackbar grabadoSnackbar;
     boolean isDialogShowing = false; // El booleano que salvo al programa

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!sensors.isEmpty()) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }

        Calendar obtener_fecha_y_hora = Calendar.getInstance(); //objeto calendario con el objetivo de obtener la fecha y hora del dispositivo
        SimpleDateFormat formato_fecha = new SimpleDateFormat("dd-MM-yyyy"); //se formatea la fehca
        //   SimpleDateFormat formato_hora = new SimpleDateFormat("HH:mm"); inutil debido al descubrimiento del elemento ;-;
        String fecha = formato_fecha.format(obtener_fecha_y_hora.getTime());
        // String hora = formato_hora.format(obtener_fecha_y_hora.getTime()); inutil debido al descubrimiento del elemento ;-;
        TextView textView = findViewById(R.id.textView);
        textView.setText("Fecha: " + fecha);

        // Resulta que hay un elemento xml para mostrar la hora actual del sistema entonces esto fue innecesarioTextView textView2 = findViewById(R.id.textView2);
        // lo que paso arriba textView2.setText("Hora: " + hora);
        Texto_Nombre = findViewById(R.id.EditText2);
        Texto_Rut = findViewById(R.id.EditText1);
        Texto_Incidente = findViewById(R.id.EditText3);
        Grabar = findViewById(R.id.button);
        Cerrar = findViewById(R.id.button2);



        TextWatcher refrescador_de_error = new TextWatcher() { //Este objeto se va a utilizar para actualizar el estado de error, si el usuario mientras escribe lo deja en blanco sale el error
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EditText objeto_a_cambiar = (EditText) Texto_Nombre.getRootView().findFocus(); // rootview obtiene el layout de la view y find focus encuentra el elemento que el usuario esta usando y se le asigna al objeto
                if (charSequence.length() == 0) {
                    // Set error message if text is empty
                    objeto_a_cambiar.setError("Campo Esta Vacio"); // gracias a getrootview and findfocus se puede aplicar dinamicamente a cualquier elemento Edit Text (se supone)
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        Texto_Nombre.addTextChangedListener(refrescador_de_error);
        Texto_Incidente.addTextChangedListener(refrescador_de_error);
        TextWatcher para_el_rut = new TextWatcher() { //ya que el RUT debe de utilizar la logica de validacion se debe de utilizar un TextWatcher distinto
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               String rutInput = Texto_Rut.getText().toString();
               if(!validarRut(rutInput) || rutInput.length() == 6) { //Por alguna razon el algoritmo detecta el RUT valido tambein si
                   Texto_Rut.setError("Rut Invalido");
               } else {
                   Texto_Rut.setError(null);
               }



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        Texto_Rut.addTextChangedListener(para_el_rut); //cuando se cambie el texto se llama al TextWatcher para que ejecute la validacion
        grabadoSnackbar = Snackbar.make(findViewById(R.id.main), "Datos Grabados", Snackbar.LENGTH_LONG); // Al snackbar se le dan los atributos con los que va a apracer
        Grabar.setOnClickListener(new View.OnClickListener() { // en caso de que el boton sea presionado se ejecuta el codigo
            @Override
            public void onClick(View view) {
                if (!validarError(Texto_Incidente) && !validarError(Texto_Nombre) && !validarError(Texto_Rut)
                        && !Texto_Incidente.getText().toString().isEmpty() && !Texto_Nombre.getText().toString().isEmpty() && !Texto_Rut.getText().toString().isEmpty()) { // el if mas largo de toda mi vida
                    new AlertDialog.Builder(MainActivity.this) // se crea un dialogo de alerta de confirmacion (descubrir que esto existe 4 horas despues del inicio fue divertido)
                            .setTitle("Confirmacion")
                            .setMessage("Estas seguro de grabar los datos?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() { // se le asignan los atributos a mostrar
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    grabadoSnackbar.show(); // se muestra el snackbar si presiona si
                                }
                            })
                            .setNegativeButton("No",null)
                            .show(); //se vuelve para atras si no

                }
            }
        });
        Cerrar.setOnClickListener(new View.OnClickListener() { // Salir de la Aplicacion
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static boolean validarRut(String rut) { // logica para validar el rut, cortesia de JohnMcClane de StackOverFlow

        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) { // no se como eliminar estos catch, pero si se corre en modo debug colapsa todo asi que mejor correr usando el boton play
        }
        return validacion;
    }
    public static boolean validarError(EditText objeto) {
        return objeto.getError() != null;

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        this.event = event;
        if (!validarError(Texto_Incidente) && !validarError(Texto_Nombre) && !validarError(Texto_Rut) &&
                event.values[SensorManager.DATA_Y] >= 9
                && !Texto_Incidente.getText().toString().isEmpty() && !Texto_Nombre.getText().toString().isEmpty() && !Texto_Rut.getText().toString().isEmpty()
                && !isDialogShowing) { //misma logica que con el boton pero se añadio un boolean para evitar que el sistema colapse de manera catastrofica debido a spam
            isDialogShowing = true; // Set the flag to true

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de grabar los datos?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            grabadoSnackbar.show();
                            isDialogShowing = false; // Reset the flag after showing the Snackbar
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isDialogShowing = false; // Reset the flag if the dialog is dismissed
                        }
                    })
                    .show();
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

}
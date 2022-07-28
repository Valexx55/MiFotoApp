package edu.val.mifotoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Uri uri_foto; //la ruta de la foto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void pedirPermisosFoto() {
        String[] apermisos = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(apermisos, 36);
        }
    }


    public void tomarFoto(View view) {
        pedirPermisosFoto();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("ETIQUETA_LOG", "Permisos FOTO concedido");
            lanzarCamara();

        } else {
            Log.d("ETIQUETA_LOG", "Permisos FOTO denegado");
            Toast.makeText(this, "PERMISO TOMAR FORO DENEGADO", Toast.LENGTH_LONG).show();
        }
    }


    private Uri obtenerRutaFichero ()
    {
        Uri uri = null;

        Date fecha_actual = new Date();
        String momemento_actual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(fecha_actual);
        String nombre_fichero = "CURSO_SEPE_" + momemento_actual + ".jpg";
        //vamos a guardar la foto - se va a crear el archivo en el directorio de la Cámara de Fotos
        String ruta_foto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()+"/"+nombre_fichero;

        Log.d("ETIQUETA_LOG", "Ruta foto destino = " + ruta_foto);
        File file = new File(ruta_foto);

        try {
            file.createNewFile();
            //generamos la ruta "logica" desde la ruta física (file) para evitar la excepción - File Provider
            uri = FileProvider.getUriForFile(this, "edu.val.mifotoapp", file);
            Log.d("ETIQUETA_LOG", "Ruta uri destino = " + uri);

        } catch (Exception e)
        {
            Log.e("ETIQUETA_LOG", "Error al crear el fichero destino de la foto", e);
        }



        return uri;
    }

    private void lanzarCamara() {
        this.uri_foto = obtenerRutaFichero();
        if (uri_foto!=null)
        {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_foto);
            startActivityForResult(intent, 656);

        } else {
            Toast.makeText(this, "NO ES POSIBLE LANZAR LA CÁMARA", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK)
        {
            Log.d("ETIQUETA_LOG", "La foto ha ido bien");
            ImageView imageView = findViewById(R.id.foto_tomada);
            imageView.setImageURI(this.uri_foto);
        } else {
            Log.d("ETIQUETA_LOG", "La foto ha ido mal");
            Toast.makeText(this, "FOTO CANCELADA", Toast.LENGTH_LONG).show();
        }
    }
}
package mx.tecnm.tepic.ladm_practica2_archivosmemoriainternayexterna

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    var radio = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnguardar.setOnClickListener {
            var data = lafrase.text.toString()
            var mensaje = ""

            if (etnombrearchivo.text.toString()=="") {
                AlertDialog.Builder(this).setTitle("Error")
                        .setMessage("ESCRIBE NOMBRE DE ARCHIVO")
                        .setPositiveButton("ok") { d, i ->
                            d.dismiss()
                        }
                        .show()

            }else if(memoriainterna.isChecked){
            if (guardarEnMemoriaInterna(data) == true) {
               // mensaje = "SE GUARDO CON EXITO EN MEMORIA INTERNA"
                }
            }else if(memoriaexterna.isChecked){
                guardarEnMemoriaExterna()
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
                }

            }
            etnombrearchivo.setText("")
        }

        btnabrir.setOnClickListener {
            if(memoriainterna.isChecked) {
                var contenido = abrirDesdeMemoriaInterna()

                if (contenido.isEmpty() == true) {
                    Toast.makeText(this,"ERROR ARCHIVO NO ENCONTRADO",Toast.LENGTH_LONG).show()
                } else {
                    lafrase.setText(contenido)
                    Toast.makeText(this,"SE LEYO ARCHIVO DESDE MEMORIA INTERNA",Toast.LENGTH_LONG).show()

                }

            }else if(memoriaexterna.isChecked){if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

            }
                abrirEnMemoriaExterna()
            }
        }

    }
        private fun abrirEnMemoriaExterna(){
            try{
                //1 no hay tarjeta SD
                if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED){
                    AlertDialog.Builder(this).setMessage("NO HAY SD").show()
                    return
                }
                var rutaSD = getExternalFilesDir(null)!!.absolutePath
                var archivoSD = File(rutaSD,etnombrearchivo.text.toString() + ".txt")
                val fIn = FileInputStream(archivoSD)
                val archivo = InputStreamReader(fIn)
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea + "\n")
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
                lafrase.setText(todo)

                Toast.makeText(this,"SE LEYO ARCHIVO DESDE MEMORIA EXTERNA",Toast.LENGTH_LONG).show()
            }catch(io:IOException){
                Toast.makeText(this,"ERROR ARCHIVO NO ENCONTRADO",Toast.LENGTH_LONG).show()
            }
        }
    private fun abrirDesdeMemoriaInterna():String {
        var data= ""

        try {
            var archivo = BufferedReader(InputStreamReader(openFileInput(etnombrearchivo.getText().toString()+".txt")))
            var linea = archivo.readLine()
            val todo = StringBuilder()
            while (linea != null) {
                todo.append(linea + "\n")
                linea = archivo.readLine()
            }
            archivo.close()
            lafrase.setText(todo)
            data=todo.toString()
        }catch (io:IOException){

        }
        return data
    }

    private fun guardarEnMemoriaExterna() {
      // if (radio == "externa") {
           try {
               //1 no hay tarjeta SD
               if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                   AlertDialog.Builder(this).setMessage("NO HAY SD").show()
               }

               //2 enrutamiento de la memoria sd para crear el archivo
               var rutaSD = getExternalFilesDir(null)!!.absolutePath
               var archivoEnSD = File(rutaSD, etnombrearchivo.text.toString() + ".txt")
               var flujoSalida = OutputStreamWriter(FileOutputStream(archivoEnSD))

               flujoSalida.write(lafrase.text.toString())
               flujoSalida.flush()
               flujoSalida.close()

               lafrase.setText("")

               Toast.makeText(this, "SE PUDO GUARDAR EN MEMORIA EXTERNA", Toast.LENGTH_LONG).show()
           } catch (io: IOException) {
               Toast.makeText(this, "NO SE GUARDO EN MEMORIA EXTERNA", Toast.LENGTH_LONG).show()
           }
    }

    private fun guardarEnMemoriaInterna(data: String): Boolean {
            try {
                var flujoSalida = OutputStreamWriter(openFileOutput(etnombrearchivo.text.toString() + ".txt", Context.MODE_PRIVATE))
                flujoSalida.write(data)
                flujoSalida.flush()
                flujoSalida.close()

                Toast.makeText(this, "SE PUDO GUARDAR EN MEMORIA INTERNA", Toast.LENGTH_LONG).show()

            } catch (io: IOException) {
                Toast.makeText(this, "NO SE GUARDO EN MEMORIA INTERNA", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }


    }

package com.example.locationapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.locationapp.ui.theme.LocationAppTheme
import android.Manifest
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel=viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                   // Greeting("Android")
                    MyApp(viewModel)
                }
            }
        }
    }
}
@Composable
fun MyApp(viewModel: LocationViewModel){
    val context= LocalContext.current
    val locationUtils=LocationUtils(LocalContext.current)
    LocationDisplay(locationUtils = locationUtils, viewModel,context)
}
@Composable
fun LocationDisplay(locationUtils: LocationUtils,
                      viewModel: LocationViewModel,
                      context: Context){
    val messaggio= remember { mutableStateOf("")  }
    val location=viewModel.location.value

    val requestPermissionLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult ={permissions->
            if((permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)&&
                (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true)) {
                    //Localizzazione possibile
                Toast.makeText(context,"Permessi ottenuti"
                    ,Toast.LENGTH_LONG).show()
                locationUtils.requestLocationUpdates(viewModel)
            }else{
                   // Spiega perchè vuoi ottenere questi permessi
                messaggio.value="Non è possibile la localizzazione"
                val rationaleRequired=ActivityCompat.
                                      shouldShowRequestPermissionRationale(
                                         context as MainActivity,
                                          Manifest.permission.ACCESS_FINE_LOCATION
                                      )||
                                      ActivityCompat.
                                        shouldShowRequestPermissionRationale(
                                              context as MainActivity,
                                              Manifest.permission.ACCESS_COARSE_LOCATION
                                       )
                if(rationaleRequired){
                    Toast.makeText(context,"Questi permessi sono necessari" +
                            "              per poter far funzionare l'applicazione"
                                           ,Toast.LENGTH_LONG).show()

                }else{
                    // questo succede una volta che l'utente nega i permessi
                    Toast.makeText(context,"Abilita " +
                            " manualmente i permessi dalla impostazione del telefono"
                        ,Toast.LENGTH_LONG).show()
                }

            }

        } )
    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        if(location!=null){
            Text(text = "Posizione gps ${location.latitude} ${location.longitude}")
        }else{ Text(text ="Posizione gps non disponibile")}

        Button(onClick = {

            if( locationUtils.hasLocationPermission(context)){
                messaggio.value="Localizzazione è possibile"
                Toast.makeText(context,"Permessi ottenuti"
                    ,Toast.LENGTH_LONG).show()
                // Text(text = "Localizzazione è possibile")
                locationUtils.requestLocationUpdates(viewModel)
            }else{
              requestPermissionLauncher.launch(
                  arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                      Manifest.permission.ACCESS_FINE_LOCATION)
              )
            }

        }) {
            Text(text = "Ottieni la localizzazione telefono")

        }
    }
}
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.myapplication.R
import java.io.File

const val PICK_IMAGE_REQUEST_CODE = 1001
const val CAPTURE_IMAGE_REQUEST_CODE = 1002

@Composable
fun HomeScreen(navController: NavHostController, activity: Activity) { // Ajouter un paramètre d'activité
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Fonction pour naviguer vers GPSScreen avec l'URI de l'image sélectionnée
    fun navigateToGPSScreen() {
        selectedImageUri?.let { uri ->
            navController.navigate("gps_screen/${uri.toString()}")
        }
    }

    // Mettez à jour la fonction handleActivityResult pour stocker l'URI de l'image sélectionnée
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Code existant...
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Bouton pour insérer une photo parmi les photos disponibles dans l'application
            Button(
                onClick = {
                    pickImageFromGallery(activity) // Appel de pickImageFromGallery lorsque le bouton est cliqué
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.insert_photo_button_label))
            }

            Button(
                onClick = {
                    captureImage(activity) // Appel de captureImage lorsque le bouton est cliqué
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.take_photo_button_label))
            }
        }
    }
}

// Fonctions d'extension pour Activity
fun pickImageFromGallery(activity: Activity) {
    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
}

fun captureImage(activity: Activity) {
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    activity.startActivityForResult(captureIntent, CAPTURE_IMAGE_REQUEST_CODE)
}


// Fonction pour gérer les résultats des activités (choix d'image et capture d'image)
fun Activity.handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, navController: NavHostController) {
    if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
        // Récupérer l'URI de l'image sélectionnée depuis la galerie
        val selectedImageUri: Uri? = data.data
        // Faire quelque chose avec l'URI de l'image sélectionnée, par exemple l'afficher dans une autre composable
        navController.navigate("image_detail_screen/${selectedImageUri.toString()}")
    } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
        // Récupérer le fichier image capturé
        val capturedImageFile: File? = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
        // Faire quelque chose avec le fichier image capturé, par exemple l'afficher dans une autre composable
        navController.navigate("image_detail_screen/${capturedImageFile?.path}")
    }
}

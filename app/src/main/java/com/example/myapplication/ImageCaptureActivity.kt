import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MapsActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import kotlinx.*

class ImageCaptureActivity : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient
    private lateinit var labeler: FirebaseVisionImageLabeler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)

        // Initialize Places API client
        placesClient = Places.createClient(this)

        // Initialize Firebase ML Kit labeler
        labeler = FirebaseVision.getInstance().onDeviceImageLabeler

        // Capture image button click listener
        captureImageButton.setOnClickListener {
            // Start camera to capture image
            startCamera()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val image: FirebaseVisionImage = FirebaseVisionImage.fromFilePath(this, data?.data!!)
            processImage(image)
        }
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun processImage(image: FirebaseVisionImage) {
        // Process the captured image to detect labels
        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                // Extract relevant information from the ML Kit results (e.g., landmark names or object labels)
                val detectedLabels = labels.map { it.text }
                // Get location information using Google Places API
                getLocationInformation(detectedLabels)
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur during processing
            }
    }

    private fun getLocationInformation(labels: List<String>) {
        // Use the Google Places API to get location information based on the detected labels
        val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.LAT_LNG))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->
                val likelyPlaces = response.placeLikelihoods
                if (!likelyPlaces.isNullOrEmpty()) {
                    val location = likelyPlaces[0].place.latLng
                    // Proceed to the next step: Display Location on Google Maps
                    displayLocationOnMap(location)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the request
            }
    }

    private fun displayLocationOnMap(location: LatLng) {
        // Assuming you have a MapsActivity where you display the map
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("latitude", location.latitude)
        intent.putExtra("longitude", location.longitude)
        startActivity(intent)
    }
}
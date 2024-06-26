
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sirius.model.TypeUser
import com.example.sirius.ui.theme.Green1
import com.example.sirius.view.components.FloatingButton
import com.example.sirius.view.screens.ShelterFormDialog
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SquareImage(imageRes: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.extraSmall)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraSmall)
        )
    }
}

@Composable
fun LocationCard(location: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(width = 1.dp, color = Green1),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Reduce vertical space
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.headlineMedium,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Green1)
            ) {
                AddGoogleMap(location)
            }
        }
    }
}

@Composable
fun AddGoogleMap(location : String) {
    val output = location.split(';').map { it.toDouble() }
    val sirius = LatLng(output[0], output[1])
    val marker = MarkerState(position = sirius)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(sirius,15f)
    }
    GoogleMap (
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = marker,
            title = "Sirius Canarias Animal Shelter"
        )
    }
}


@SuppressLint("DiscouragedApi")
@Composable
fun AboutUsScreen(id: Int? = 1 , shelterViewModel: ShelterViewModel, userViewModel: UserViewModel) {
    val shelterImages = listOf("shelter1", "shelter2", "shelter3", "shelter4")
    val context = LocalContext.current
    val user by remember { mutableStateOf(userViewModel.getAuthenticatedUser()) }

    val shelter by shelterViewModel.getShelterById(id ?: 0).collectAsState(initial = null)

    val showDialogAdd = remember { mutableStateOf(false) }
    val shelterId = user?.let { userViewModel.getShelterByUserId(it.id).collectAsState(emptyList()).value }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            SectionTitle("About Us")
            shelter?.let { JustifiedText(it.aboutUs) }
        }

        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (image in shelterImages) {
                    val resourceId = context.resources.getIdentifier(
                        image, "drawable", context.packageName
                    )
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                            SquareImage(resourceId)
                        }
                    }
                }
            }
        }

        item {
            shelter?.let { LocationCard(it.location) }
        }

        item {
            SectionTitle("Schedule")
            shelter?.let { JustifiedText(it.schedule) }

        }

        item {
            SectionTitle("Shelter's Data")
            shelter?.let { JustifiedText(it.sheltersData) }
        }

        item {
            SectionTitle("Contact Information")
            JustifiedText("Email: " + (shelter?.email ?: "") +"\nPhone: " + (shelter?.phone ?: ""))

        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {

        if(user?.role == TypeUser.admin || (user?.role == TypeUser.owner && shelterId?.get(0) ?: 1 == id)){
            FloatingButton(Icons.Default.Edit, Modifier.align(Alignment.BottomEnd) ){
                showDialogAdd.value = true
            }
        }



    }

    if( showDialogAdd.value){
        ShelterFormDialog(shelter, showDialogAdd = showDialogAdd , shelterViewModel)
    }
}



@Composable
fun JustifiedText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        textAlign = TextAlign.Justify
    )
    Spacer(modifier = Modifier.padding(10.dp))
}
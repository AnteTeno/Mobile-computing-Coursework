package com.example.coursework.screens

import android.R.id.input
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import com.example.coursework.data.FoodData
import com.example.coursework.ui.theme.CourseworkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.coursework.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.coursework.database.DatabaseProvider

import java.io.File
import com.example.coursework.database.entities.User


@Composable
fun ProfileScreen(
    navController: NavController
) {

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var username by remember { mutableStateOf("") }

    val db = DatabaseProvider.getDatabase(context)
    val userDao = db.userDao()

    LaunchedEffect(Unit) {
        val savedUser = userDao.getUser()

        if (savedUser != null) {
            username = savedUser.username
            selectedImageUri = Uri.fromFile(File(savedUser.profilePicturePath))
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            context.contentResolver.openInputStream(uri).use { input ->
                val file = File(context.filesDir, "profile_picture.jpg")
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
                selectedImageUri = Uri.fromFile(file)
            }
        }
    }


    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        IconButton( onClick = { navController.popBackStack() }
        ) {
            Icon(
                painter = painterResource(R.drawable.backicon),
                contentDescription = "Back",
                modifier = Modifier.size(22.dp)
            )
        }

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(15.dp)
    ) {

        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile_picture_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        Button(
            modifier = Modifier.size(width = 110.dp, height = 30.dp),
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text(
                text = "Pick Image",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row() {
            TextField(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                value = username,
                onValueChange = {username = it},
                label = {Text("Username")}
            )
        }
        Button(
            modifier = Modifier.size(width = 80.dp, height = 30.dp),
            onClick = {
                val imagePath = File(context.filesDir, "profile_picture.jpg").absolutePath
                val user = User(
                    uid = 1,
                    username = username,
                    profilePicturePath = imagePath
                )
                userDao.insertUser(user)
            }

        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.labelSmall
            )
        }

    }
}


@Preview
@Composable
fun PreviewProfileScreen() {
    CourseworkTheme() {
       ProfileScreen(navController = rememberNavController())
    }
}
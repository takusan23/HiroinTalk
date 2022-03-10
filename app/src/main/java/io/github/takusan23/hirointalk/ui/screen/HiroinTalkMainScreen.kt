package io.github.takusan23.hirointalk.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import io.github.takusan23.hirointalk.R
import io.github.takusan23.hirointalk.ui.theme.HiroinTalkTheme
import io.github.takusan23.hirointalk.ui.tool.SetNavigationBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiroinTalkMainScreen() {
    val context = LocalContext.current
    val recognizer = remember { TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build()) }
    // 解析結果を入れる
    val resultText = remember { mutableStateOf<Text?>(null) }
    val selectUri = remember { mutableStateOf<Uri?>(null) }
    // 画像選択結果
    val imageSelect = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument(), onResult = { uri ->
        selectUri.value = uri
    })

    // Uriが変わったら解析する
    LaunchedEffect(key1 = selectUri.value, block = {
        if (selectUri.value != null) {
            // 画像認識用のデータへ変換
            val inputImage = InputImage.fromFilePath(context, selectUri.value!!)
            // 解析する
            recognizer.process(inputImage).addOnSuccessListener { resultText.value = it }
        }
    })

    HiroinTalkTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

            // ステータスバーの色を変える
            SetNavigationBarColor(color = MaterialTheme.colorScheme.background)

            Scaffold(
                topBar = {
                    SmallTopAppBar(title = { Text(text = "ML Kit 文字起こし") })
                },
                floatingActionButton = {
                    LargeFloatingActionButton(onClick = { imageSelect.launch(arrayOf("image/*")) }) {
                        Icon(painter = painterResource(id = R.drawable.ic_outline_image_search_24), contentDescription = null)
                    }
                },
                content = {
                    Column() {
                        Image(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .aspectRatio(1.7f),
                            painter = rememberImagePainter(data = selectUri.value),
                            contentDescription = null
                        )
                        Divider()
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = resultText.value?.text ?: "未解析",
                            fontSize = 18.sp
                        )
                    }
                }
            )

        }
    }
}
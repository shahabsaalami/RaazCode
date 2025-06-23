package raazcode.iran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold // Material3 Scaffold
import androidx.compose.material3.Text // Material3 Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import raazcode.iran.ui.theme.RaazCodeTheme

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button // Material3 Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme // Material3 MaterialTheme
import androidx.compose.material3.OutlinedTextField // Material3 OutlinedTextField
import androidx.compose.material3.Surface // Material3 Surface
import androidx.compose.material3.Tab // Material3 Tab
import androidx.compose.material3.TabRow // Material3 TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaazCodeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Using Material3 color scheme
                ) {
                    RaazCodeApp() // Your app's main Composable, now named RaazCodeApp
                }
            }
        }
    }
}

@Composable
fun RaazCodeApp() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    // Farsi tab titles
    val tabTitles = listOf("کدگذاری", "کدگشایی") // Encode, Decode

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTabIndex,
                modifier = Modifier.padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> KodgozariScreen() // Farsi screen name
                1 -> KodgoshaeiScreen() // Farsi screen name
            }
        }
    }
}

@Composable
fun KodgozariScreen() { // Renamed from EncodeScreen
    var encodeInput by remember { mutableStateOf("") }
    val encodeOutput by remember {
        derivedStateOf {
            encodeText(encodeInput)
        }
    }
    var copyButtonText by remember { mutableStateOf("کپی") } // Copy
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)
       ) {
        OutlinedTextField(
            value = encodeInput,
            onValueChange = { encodeInput = it },
            label = { Text("متن برای کدگذاری") }, // Text to encode
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 200.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 5,
            trailingIcon = {
                IconButton(
                    onClick = {
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val pasteData = clipboardManager.primaryClip?.getItemAt(0)?.text
                        pasteData?.let {
                            encodeInput = it.toString()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_content_paste_24),
                        contentDescription = "Paste"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = encodeOutput,
            onValueChange = {}, // Read-only
            label = { Text("خروجی کدگذاری شده") }, // Encoded Output
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 200.dp),
            readOnly = true,
            singleLine = false,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                copyToClipboard(context, encodeOutput)
                copyButtonText = "کپی شد!" // Copied!
                coroutineScope.launch {
                    delay(3000)
                    copyButtonText = "کپی" // Copy
                }
            },
            enabled = encodeOutput.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(copyButtonText)
        }
    }
}

@Composable
fun KodgoshaeiScreen() { // Renamed from DecodeScreen
    var decodeInput by remember { mutableStateOf("") }
    val decodeOutput by remember {
        derivedStateOf {
            decodeText(decodeInput)
        }
    }
    var copyButtonText by remember { mutableStateOf("کپی") } // Copy
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current // Get UriHandler to open links

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = decodeInput,
            onValueChange = { decodeInput = it },
            label = { Text("متن برای کدگشایی") }, // Text to decode
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 200.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 5,
            trailingIcon = {
                IconButton(
                    onClick = {
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val pasteData = clipboardManager.primaryClip?.getItemAt(0)?.text
                        pasteData?.let {
                            decodeInput = it.toString()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_content_paste_24),
                        contentDescription = "Paste"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = decodeOutput,
            onValueChange = {}, // Read-only
            label = { Text("خروجی کدگشایی شده") }, // Decoded Output
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 200.dp),
            readOnly = true,
            singleLine = false,
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                copyToClipboard(context, decodeOutput)
                copyButtonText = "کپی شد!" // Copied!
                coroutineScope.launch {
                    delay(3000)
                    copyButtonText = "کپی" // Copy
                }
            },
            enabled = decodeOutput.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(copyButtonText)
        }
        // --- Source text added here ---
        Spacer(modifier = Modifier.height(24.dp)) // Some space before the source
        Text(
            text = "سورس کد:", // Text in Farsi
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // A more subtle text color
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "https://github.com/shahabsaalami/RaazCode", // The clickable URL
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary), // Make it look like a link
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable {
                    uriHandler.openUri("https://github.com/shahabsaalami/RaazCode")
                },
            textAlign = TextAlign.Center
        )
    }
}

// --- Encoding/Decoding Logic (These remain in English for clarity and standard practice) ---
private val capitalIndicator = 'ه'

private val toFaMapper = mapOf(
    'a' to 'ش', 'b' to 'ل', 'c' to 'ض', 'd' to 'ب', 'e' to 'ع',
    'f' to 'گ', 'g' to 'و', 'h' to 'ظ', 'i' to 'س', 'j' to 'ژ',
    'k' to 'ک', 'l' to 'م', 'm' to 'ن', 'n' to 'پ', 'o' to 'غ',
    'p' to 'ح', 'q' to 'ز', 'r' to 'ط', 's' to 'ر', 't' to 'ق',
    'u' to 'ث', 'v' to 'ف', 'w' to 'ی', 'x' to 'د', 'y' to 'ذ',
    'z' to 'خ',
    '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
    '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹',
    ' ' to ' ', ':' to ':', '/' to 'ت', '!' to '!', '@' to '@',
    '#' to '#', '$' to '$', '%' to '%', '^' to '^', '&' to 'ا',
    '*' to '*', '(' to '(', ')' to ')', '-' to '-', '_' to '_',
    '+' to '+', '?' to '?', '=' to 'چ', '.' to '.', ',' to ',',
    ';' to ';', '|' to '|', '~' to '~', '`' to '`'
)

private val toEnMapper = mapOf(
    'ش' to 'a', 'ل' to 'b', 'ض' to 'c', 'ب' to 'd', 'ع' to 'e',
    'گ' to 'f', 'و' to 'g', 'ظ' to 'h', 'س' to 'i', 'ژ' to 'j',
    'ک' to 'k', 'م' to 'l', 'ن' to 'm', 'پ' to 'n', 'غ' to 'o',
    'ح' to 'p', 'ز' to 'q', 'ط' to 'r', 'ر' to 's', 'ق' to 't',
    'ث' to 'u', 'ف' to 'v', 'ی' to 'w', 'د' to 'x', 'ذ' to 'y',
    'خ' to 'z',
    '۰' to '0', '۱' to '1', '۲' to '2', '۳' to '3', '۴' to '4',
    '۵' to '5', '۶' to '6', '۷' to '7', '۸' to '8', '۹' to '9',
    ' ' to ' ', ':' to ':', 'ت' to '/', '!' to '!', '@' to '@',
    '#' to '#', '$' to '$', '%' to '%', '^' to '^', 'ا' to '&',
    '*' to '*', '(' to '(', ')' to ')', '-' to '-', '_' to '_',
    '+' to '+', '?' to '?', 'چ' to '=', '.' to '.', ',' to ',',
    ';' to ';', '|' to '|', '~' to '~', '`' to '`'
)

fun encodeText(inputText: String): String {
    return try {
        inputText.map { letter ->
            val mappedChar = toFaMapper[letter.lowercaseChar()] ?: letter
            if (letter.isUpperCase()) "$capitalIndicator$mappedChar" else mappedChar.toString()
        }.joinToString("")
    } catch (e: Exception) {
        "خطا در کدگذاری ورودی: ${e.message}" // Error encoding input
    }
}


fun decodeText(inputText: String): String {
    return try {
        val result = StringBuilder()
        var i = 0
        while (i < inputText.length) {
            val char = inputText[i]
            if (char == capitalIndicator) {
                if (i + 1 < inputText.length) {
                    val nextChar = inputText[i + 1]
                    val decoded = toEnMapper[nextChar]?.uppercaseChar() ?: nextChar
                    result.append(decoded)
                    i += 2
                } else {
                    // Capital indicator at the end of string
                    result.append(char)
                    i++
                }
            } else {
                val decoded = toEnMapper[char] ?: char
                result.append(decoded)
                i++
            }
        }
        result.toString()
    } catch (e: Exception) {
        "خطا در کدگشایی ورودی: ${e.message}" // Error decoding input
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RaazCodeTheme {
        RaazCodeApp() // Use the new app composable for preview
    }
}
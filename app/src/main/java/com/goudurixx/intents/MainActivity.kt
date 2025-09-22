package com.goudurixx.intents

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Using MaterialTheme (M3) for styling.
            // For a new project, Android Studio typically generates Theme.kt in ui.theme package.
            // You can wrap IntentTesterApp() with your custom theme if you have one.
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IntentTesterApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentTesterApp() {
    val context = LocalContext.current

    var action by remember { mutableStateOf(TextFieldValue("")) }
    var packageNameText by remember { mutableStateOf(TextFieldValue("")) }
    var classNameText by remember { mutableStateOf(TextFieldValue("")) }
    var dataUri by remember { mutableStateOf(TextFieldValue("")) }
    var extraKey1 by remember { mutableStateOf(TextFieldValue("")) }
    var extraValue1 by remember { mutableStateOf(TextFieldValue("")) }
    var extraKey2 by remember { mutableStateOf(TextFieldValue("")) }
    var extraValue2 by remember { mutableStateOf(TextFieldValue("")) }
    var statusText by remember { mutableStateOf("Status: Ready") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .statusBarsPadding(), // Make the column scrollable,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Custom Intent Builder", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = action,
            onValueChange = { action = it },
            label = { Text("Intent Action (e.g., android.settings.SETTINGS)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = packageNameText,
            onValueChange = { packageNameText = it },
            label = { Text("Package Name (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = classNameText,
            onValueChange = { classNameText = it },
            label = { Text("Class Name (optional, full path)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = dataUri,
            onValueChange = { dataUri = it },
            label = { Text("Data URI (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )

        Text(
            "Extras (String Key-Value Pairs):",
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = extraKey1,
                onValueChange = { extraKey1 = it },
                label = { Text("Extra Key 1") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = extraValue1,
                onValueChange = { extraValue1 = it },
                label = { Text("Extra Value 1") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                singleLine = true
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = extraKey2,
                onValueChange = { extraKey2 = it },
                label = { Text("Extra Key 2") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = extraValue2,
                onValueChange = { extraValue2 = it },
                label = { Text("Extra Value 2") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                singleLine = true
            )
        }

        Button(
            onClick = {
                if (action.text.isBlank() && (packageNameText.text.isBlank() || classNameText.text.isBlank())) {
                    Toast.makeText(
                        context,
                        "\uD83D\uDEA8 Action or ComponentName (Package + Class) must be provided",
                        Toast.LENGTH_LONG
                    ).show()
                    statusText = "\uD83D\uDEA8 Status: Action or ComponentName missing"
                    return@Button
                }

                val intent = Intent()
                if (action.text.isNotBlank()) {
                    intent.action = action.text
                }
                if (packageNameText.text.isNotBlank() && classNameText.text.isNotBlank()) {
                    intent.component = ComponentName(packageNameText.text, classNameText.text)
                } else if (packageNameText.text.isNotBlank()) {
                    intent.setPackage(packageNameText.text)
                }

                if (dataUri.text.isNotBlank()) {
                    try {
                        intent.data = Uri.parse(dataUri.text)
                    } catch (e: Exception) {
                        Toast.makeText(context, "\uD83D\uDEA8 Invalid Data URI format", Toast.LENGTH_SHORT).show()
                        statusText = "\uD83D\uDEA8 Status: Invalid Data URI"
                        return@Button
                    }
                }

                if (extraKey1.text.isNotBlank() && extraValue1.text.isNotBlank()) {
                    intent.putExtra(extraKey1.text, extraValue1.text)
                }
                if (extraKey2.text.isNotBlank() && extraValue2.text.isNotBlank()) {
                    intent.putExtra(extraKey2.text, extraValue2.text)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                try {
                    context.startActivity(intent)
                    statusText = "✅ Status: Intent launched successfully!"
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Activity not found for this Intent. \uD83D\uDEA8", Toast.LENGTH_LONG)
                        .show()
                    statusText = "\uD83D\uDEA8 Status: ActivityNotFoundException"
                } catch (e: Exception) {
                    Toast.makeText(context, "Error launching Intent: ${e.message}", Toast.LENGTH_LONG).show()
                    statusText = "\uD83D\uDEA8 Status: Error - ${e.message}"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Launch Custom Intent. \uD83D\uDE80")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text("Preset Intents", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Button(
            onClick = {
                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", context.packageName, null)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                    statusText = "✅ Status: App Notification Settings launched!"
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Could not open App Notification Settings.", Toast.LENGTH_LONG).show()
                    statusText = "\uD83D\uDEA8 Status: Failed to open App Notification Settings"
                    try {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } catch (_: Exception) {
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Open My App's Notification Settings. \uD83D\uDCC2")
        }

        Button(
            onClick = {
                action = TextFieldValue("com.android.settings.SEARCH_RESULT_TRAMPOLINE")
                packageNameText = TextFieldValue("com.android.settings")
                classNameText = TextFieldValue("com.android.settings.search.SearchResultTrampoline")
                // Remind user to fill extras
                extraKey1 = TextFieldValue("query") // Example
                extraValue1 = TextFieldValue("show notification category") // Example
                statusText = "Status: Trampoline fields populated. Add/Verify extras and launch."
                Toast.makeText(
                    context,
                    "Fill/verify extras for Trampoline, then click 'Launch Custom Intent'",
                    Toast.LENGTH_LONG
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Load Samsung Search Trampoline. ✏\uFE0F")
        }

        Button(
            onClick = {
                action = TextFieldValue("android.settings.APP_SEARCH_SETTINGS")
                packageNameText = TextFieldValue("com.android.settings.intelligence")
                classNameText = TextFieldValue("com.android.settings.intelligence.search.SearchActivity")
                // Remind user to fill extras
                extraKey1 = TextFieldValue("query") // Example
                extraValue1 = TextFieldValue("show notification category") // Example
                statusText = "Status: Samsung Search fields populated. Add/Verify extras and launch."
                Toast.makeText(
                    context,
                    "Fill/verify extras for Samsung Search, then click 'Launch Custom Intent'",
                    Toast.LENGTH_LONG
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Load Samsung APP_SEARCH_SETTINGS. ✏\uFE0F")
        }

        Text(
            statusText,
            modifier = Modifier.padding(top = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text(
            "Configured Intent for Samsung's channel notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        val intent = Intent()
        intent.action = "android.settings.APP_SEARCH_SETTINGS"
        intent.component = ComponentName(
            "com.android.settings.intelligence",
            "com.android.settings.intelligence.search.SearchActivity"
        )
        intent.putExtra("query", "show notification category")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Text(
            intent.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(8.dp)
        )
        Button(
            onClick = {
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Activity not found for this Intent. \uD83D\uDEA8", Toast.LENGTH_LONG)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error launching Intent: ${e.message} \uD83D\uDEA8", Toast.LENGTH_LONG)
                        .show()
                }
            },
        ) {
            Text("Launch intent. \uD83D\uDE80")
        }
        Button(
            onClick = {
                openSamsungNotificationCategoriesSearch(
                    context
                )
            },
        ) {
            Text("Launch intent. \uD83D\uDE80")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text(
            "Do I have a Samsung device ?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        if (isSamsungDevice()) {
            Text("✅")
        } else {
            Text("❌")
        }

        Text(
            "Are Notifications Channels Activated ?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        when (notificationsChannelsStatus(context)) {
            NotificationsChannelsStatus.Disabled -> Text("❌")
            NotificationsChannelsStatus.Enabled -> Text("✅")
            NotificationsChannelsStatus.NotFound -> Text("⛓\uFE0F\u200D\uD83D\uDCA5")
        }

    }
}

internal fun openSamsungNotificationCategoriesSearch(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_SEARCH_SETTINGS // Or "android.settings.APP_SEARCH_SETTINGS"
        component = ComponentName(
            "com.android.settings.intelligence", // Samsung's Settings Intelligence package
            "com.android.settings.intelligence.search.SearchActivity" // Samsung's search activity
        )
        // This query specifically targets notification category settings
        putExtra("query", "show notification category")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required if starting from a non-Activity context
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    try {
        context.startActivity(intent)
        Toast.makeText(context, "Opening system search for notification settings...", Toast.LENGTH_SHORT).show()
    } catch (e: ActivityNotFoundException) {
        // Fallback for devices where this specific Intent or component might not exist
        // or for non-Samsung devices.
        // You can log this error for analytics or offer a more generic fallback.
        Toast.makeText(
            context,
            "Could not open specific settings search. Please navigate manually via Settings > Notifications > Advanced settings.",
            Toast.LENGTH_LONG
        ).show()
        e.printStackTrace()

        // Optional: Fallback to general app notification settings
        val generalAppNotificationsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        Intent()
        try {
            context.startActivity(generalAppNotificationsIntent)
            Toast.makeText(context, "Launching intent.", Toast.LENGTH_LONG).show()
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, "Could not open app notification settings.", Toast.LENGTH_LONG).show()
            e2.printStackTrace()
        }
    } catch (e: Exception) {
        // Generic error handling
        Toast.makeText(context, "Error opening settings: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}

internal fun notificationsChannelsStatus(context: Context): NotificationsChannelsStatus {
    val contentResolver = context.contentResolver
    try {
        val value = Settings.Secure.getInt(contentResolver, "show_notification_category_setting")
        return if (value == 1)
            NotificationsChannelsStatus.Enabled
        else
            NotificationsChannelsStatus.Disabled
    } catch (e: Settings.SettingNotFoundException) {
        Log.e("NotificationsChannelsStatus", "Setting not found: ${e.message}")
        return NotificationsChannelsStatus.NotFound
    } catch (e: Exception) {
        Log.e("NotificationsChannelsStatus", "Error checking notification channels status: ${e.message}")
        return NotificationsChannelsStatus.NotFound
    }
}

internal fun isSamsungDevice(): Boolean {
    return Build.MANUFACTURER.contains("samsung", ignoreCase = true)
}

internal sealed interface NotificationsChannelsStatus {
    data object Enabled : NotificationsChannelsStatus
    data object Disabled : NotificationsChannelsStatus
    data object NotFound : NotificationsChannelsStatus
}
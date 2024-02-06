package com.mathias8dev.mqttclient.ui.screens.configuration

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathias8dev.mqttclient.storage.room.model.Config
import com.mathias8dev.mqttclient.ui.composables.StandardDialog
import com.mathias8dev.mqttclient.ui.composables.TextInput
import io.github.mathias8dev.yup.Yup
import io.github.mathias8dev.yup.constraintsListOf


@Composable
fun ConfigurationForm(
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    onSubmitClicked: (config: Config) -> Unit
) {


    var showErrorsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var errorMessages by remember {
        mutableStateOf(emptyList<String>())
    }

    val modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)


    val formValidator = remember {
        Yup.statefulValidator(Yup.reactiveValidation, Yup.preserveDateType) {
            initialStateMap {
                mapOf(
                    "serverUrl" to "",
                    "serverPort" to "",
                    "currentTopic" to ""
                )
            }
            constraints {
                constraintsListOf(
                    "serverUrl" to urlValidationConstraints,
                    "serverPort" to portValidationConstraints,
                    "currentTopic" to topicValidationConstraints
                )
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = "Paramètres de connexion au server",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
        )
        TextInput(
            modifier = modifier,
            value = formValidator.state.getAsString("serverUrl") ?: "",
            placeholderText = "URL du serveur",
            hasErrors = formValidator.errors.get("serverUrl").isNotEmpty(),
            onValueChange = {
                formValidator.state.set("serverUrl", it)
            },
            onErrorInfoClicked = {
                showErrorsDialog = true
                errorMessages = formValidator.errors.get("serverUrl")
            }
        )

        TextInput(
            modifier = modifier,
            value = formValidator.state.getAsString("serverPort") ?: "",
            placeholderText = "Port du serveur",
            hasErrors = formValidator.errors.get("serverPort").isNotEmpty(),
            onValueChange = {
                formValidator.state.set("serverPort", it)
            },
            onErrorInfoClicked = {
                showErrorsDialog = true
                errorMessages = formValidator.errors.get("serverPort")
            }
        )

        TextInput(
            modifier = modifier,
            value = formValidator.state.getAsString("currentTopic") ?: "",
            placeholderText = "Topic auquel subscribe",
            hasErrors = formValidator.errors.get("currentTopic").isNotEmpty(),
            onValueChange = {
                formValidator.state.set("currentTopic", it)
            },
            onErrorInfoClicked = {
                showErrorsDialog = true
                errorMessages = formValidator.errors.get("currentTopic")
            }
        )


        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp),
            enabled = formValidator.isValid,
            shape = RoundedCornerShape(8.dp),
            onClick = {
                onSubmitClicked(
                    Config(
                        serverUrl = formValidator.state.getAsString("serverUrl") ?: "",
                        serverPort = formValidator.state.getAsString("serverPort") ?: "",
                        currentTopic = formValidator.state.getAsString("currentTopic") ?: ""
                    )
                )
            }) {
            Text(text = "Sauvegarder")
        }
    }




    if (showErrorsDialog && errorMessages.isNotEmpty()) {
        StandardDialog(
            onDismissRequest = {
                showErrorsDialog = false
            }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.Red.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )

            errorMessages.forEach {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = it,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
package com.mathias8dev.mqttclient.ui.screens.configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.mqttclient.R
import com.mathias8dev.mqttclient.domain.viewmodels.ConfigurationScreenViewModel
import com.mathias8dev.mqttclient.domain.utils.thenSync
import com.mathias8dev.mqttclient.ui.composables.ContentDetailsLayout
import com.mathias8dev.mqttclient.ui.composables.LottieAnimation
import com.mathias8dev.mqttclient.ui.composables.StandardDialog
import com.mathias8dev.mqttclient.ui.composables.SuccessDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.mathias8dev.yup.Yup
import io.github.mathias8dev.yup.constraintsListOf
import timber.log.Timber


@Composable
@Destination
@RootNavGraph
fun ConfigurationScreen(
    navigator: DestinationsNavigator,
    viewModel: ConfigurationScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var showAddConfigurationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val configurationsList = viewModel.configurationsFlow.collectAsStateWithLifecycle()
    val currentSelectedConfigId by viewModel.currentSelectedConfigId.collectAsStateWithLifecycle()
    val showConfigInsertedDialog by viewModel.showConfigInsertedDialog.collectAsStateWithLifecycle()
    val showConfigRemovedDialog by viewModel.showConfigRemovedDialog.collectAsStateWithLifecycle()

    ContentDetailsLayout(
        title = "Liste des configurations",
        trailingIcon = {
            IconButton(onClick = {
                showAddConfigurationDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        onBackClick = {
            navigator.popBackStack()
        }
    ) {
        if (configurationsList.value.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyListState()
            ) {
                itemsIndexed(
                    items = configurationsList.value,
                    key = { _, config -> config.id }) { _, config ->

                    ConfigComposable(
                        currentSelectedConfigId = currentSelectedConfigId,
                        config = config,
                        onConfigToggle = {
                            viewModel.onToggleConfig(it)
                        },
                        onRemoveConfig = {
                            viewModel.onRemoveConfig(it)
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier.size(92.dp),
                        animationRes = R.raw.lottie_empty
                    )
                    Text(
                        text = "Aucune configuration n'est présente dans la base de données",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

    }

    if (showAddConfigurationDialog) {
        StandardDialog(
            onDismissRequest = {
                showAddConfigurationDialog = false
            },
            contentPadding = PaddingValues(0.dp)
        ) {
            ConfigurationForm(
                onSubmitClicked = { config ->
                    Timber.d("Inserting config: $config in the database")
                    Yup.statelessValidator {
                        constraintsListOf(
                            "serverUrl" to urlValidationConstraints,
                            "serverPort" to portValidationConstraints,
                            "currentTopic" to topicValidationConstraints
                        )
                    }.validate(config).isEmpty().thenSync {
                        viewModel.insertConfig(
                            config
                        )
                    }

                }
            )
        }
    }

    if (showConfigRemovedDialog) {
        SuccessDialog(
            message = "La configuration a été supprimée avec succès",
            onDismissRequest = {
                viewModel.consumeShowConfigRemovedDialogEvent()
            }
        )
    }

    if (showConfigInsertedDialog) {
        SuccessDialog(
            message = "La configuration a été sauvegardée avec succès",
            onDismissRequest = {
                viewModel.consumeShowConfigInsertedDialogEvent()
                showAddConfigurationDialog = false
            }
        )
    }

}
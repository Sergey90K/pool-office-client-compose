package com.example.poolofficeclientcompose.ui.screens

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poolofficeclientcompose.R
import com.example.poolofficeclientcompose.data.AppearanceSensor
import com.example.poolofficeclientcompose.data.AppearanceSwitch
import com.example.poolofficeclientcompose.network.InitializationStateRelay
import com.example.poolofficeclientcompose.network.PoolInfoData
import com.example.poolofficeclientcompose.ui.theme.PoolOfficeClientComposeTheme
import kotlin.reflect.KFunction1

@Composable
fun HomeScreen(
    settingUiState: Boolean,
    poolInfoDataUiState: PoolOfficeUiState,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    reloadAllData: () -> Unit,
    appearanceSwitch: List<AppearanceSwitch>,
    appearanceSensor: List<AppearanceSensor>,
    switchAllRelay: (stateOnOff: Boolean) -> Unit,
    refreshState: Boolean,
    showNotification: (message: String, context: Context) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = poolInfoDataUiState,
        label = "Box state",
        animationSpec = tween(durationMillis = 800, easing = FastOutLinearInEasing)
    ) { uiStateIn ->
        when (uiStateIn) {
            is PoolOfficeUiState.Success -> {
                SuccessScreen(
                    settingUiState = settingUiState,
                    poolOfficeSensor = uiStateIn.combineData.sensorsData,
                    poolOfficeSwitch = uiStateIn.combineData.relayData,
                    switchRelay = switchRelay,
                    appearanceSwitch = appearanceSwitch,
                    appearanceSensor = appearanceSensor,
                    switchAllRelay = switchAllRelay,
                    reloadAllData = reloadAllData,
                    refreshState = refreshState,
                    modifier = modifier
                )
            }

            is PoolOfficeUiState.Error -> {
                showNotification(
                    stringResource(R.string.please_check_if_you_are_connected_to_your_wi_fi_network),
                    context
                )
                ErrorScreen(
                    descriptionError = uiStateIn.errorCode,
                    retryAction = reloadAllData,
                    modifier = modifier.fillMaxSize()
                )
            }

            is PoolOfficeUiState.Loading -> {
                LoadingScreen(modifier = modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun SuccessScreen(
    poolOfficeSensor: PoolInfoData,
    poolOfficeSwitch: InitializationStateRelay,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    appearanceSwitch: List<AppearanceSwitch>,
    appearanceSensor: List<AppearanceSensor>,
    switchAllRelay: (stateOnOff: Boolean) -> Unit,
    reloadAllData: () -> Unit,
    refreshState: Boolean,
    modifier: Modifier = Modifier,
    settingUiState: Boolean
) {
    AllPanelsInOne(
        settingUiState = settingUiState,
        poolOfficeSensor = poolOfficeSensor,
        poolOfficeSwitch = poolOfficeSwitch,
        switchRelay = switchRelay,
        appearanceSwitch = appearanceSwitch,
        appearanceSensor = appearanceSensor,
        switchAllRelay = switchAllRelay,
        reloadAllData = reloadAllData,
        refreshState = refreshState,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AllPanelsInOne(
    poolOfficeSensor: PoolInfoData,
    poolOfficeSwitch: InitializationStateRelay,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    appearanceSwitch: List<AppearanceSwitch>,
    appearanceSensor: List<AppearanceSensor>,
    switchAllRelay: (stateOnOff: Boolean) -> Unit,
    reloadAllData: () -> Unit,
    refreshState: Boolean,
    modifier: Modifier = Modifier,
    settingUiState: Boolean
) {
    val innerData =
        listOf(poolOfficeSensor.t1, poolOfficeSensor.t2, poolOfficeSensor.t3, poolOfficeSensor.p1)
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
        ),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val state = rememberPullRefreshState(refreshState, reloadAllData)
        Box(Modifier.pullRefresh(state)) {
            LazyColumn() {
                itemsIndexed(innerData) { index, sensor ->
                    TemperatureOrPumpItem(
                        sensorIndicator = sensor,
                        isTemperature = (index < 3),
                        appearanceSensor = appearanceSensor[index],
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                            .animateEnterExit(enter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ), initialOffsetY = { it }
                            )
                            )
                    )
                }
                itemsIndexed(poolOfficeSwitch.relayAnswer) { index, relay ->
                    SwitchItem(
                        index,
                        relay,
                        switchRelay,
                        appearanceSwitch[index],
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                            .animateEnterExit(enter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ), initialOffsetY = { it * (innerData.size) }
                            )
                            )
                    )
                }
                item() {
                    AnimatedVisibility(
                        settingUiState,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        ShowButtons(
                            switchAllRelay = switchAllRelay,
                            modifier = Modifier
                                .padding(dimensionResource(R.dimen.padding_small))
                                .animateEnterExit(enter = slideInVertically(
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessVeryLow,
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    ),
                                    initialOffsetY = { it * (innerData.size + poolOfficeSwitch.relayAnswer.size) }
                                )
                                ))
                    }
                }
            }
            PullRefreshIndicator(true, state, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun SwitchItem(
    relayNumber: Int,
    stateOnOff: Boolean,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    appearanceSwitch: AppearanceSwitch,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                SwitchIcon(switchIcon = appearanceSwitch.imageRes)
                SwitchLabel(appearanceSwitch.nameRes, appearanceSwitch.descriptionRes)
                Spacer(Modifier.weight(1f))
                RelaySwitch(relayNumber, stateOnOff, switchRelay)
            }
        }
    }
}

@Composable
fun ErrorScreen(
    @StringRes descriptionError: Int,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.connection_error)
        )
        Text(text = stringResource(descriptionError), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Loading",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(all = dimensionResource(R.dimen.padding_small))
            )
            CircularProgressIndicator(
                modifier = Modifier.width(150.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun RelaySwitch(
    relayId: Int,
    stateOnOff: Boolean,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = stateOnOff,
        onCheckedChange = {
            switchRelay(relayId, it)
        },
        thumbContent = if (stateOnOff) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.the_switch_is_on),
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.the_switch_is_off),
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        }, colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}

@Composable
fun SwitchLabel(
    @StringRes relayName: Int,
    @StringRes relayDescription: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(relayName),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
        Text(
            text = stringResource(relayDescription),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SwitchIcon(@DrawableRes switchIcon: Int) {
    Icon(
        painter = painterResource(id = switchIcon),
        contentDescription = stringResource(R.string.switch_icon),
        tint = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun TemperatureOrPumpItem(
    sensorIndicator: Float,
    isTemperature: Boolean = true,
    appearanceSensor: AppearanceSensor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                TemperatureOrPumpIcon(appearanceSensor.imageRes, isTemperature)
                TemperatureOrPumpLabel(appearanceSensor.nameRes, appearanceSensor.descriptionRes)
                Spacer(Modifier.weight(1f))
                TemperatureOrPumpInfo(sensorIndicator, isTemperature)
            }
        }
    }
}

@Composable
fun TemperatureOrPumpInfo(sensorIndicator: Float, isTemperature: Boolean) {
    val mark = if (isTemperature) {
        stringResource(R.string.c)
    } else {
        stringResource(R.string.Procent)
    }
    Row {
        Text(
            text = sensorIndicator.toString(),
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = mark,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TemperatureOrPumpLabel(
    @StringRes nameRes: Int,
    @StringRes descriptionRes: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(nameRes),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
        Text(
            text = stringResource(descriptionRes),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TemperatureOrPumpIcon(
    @DrawableRes imageRes: Int,
    isTemperature: Boolean,
    modifier: Modifier = Modifier
) {
    val description = if (isTemperature) {
        stringResource(R.string.temperature_icon)
    } else {
        stringResource(R.string.pump_icon)
    }
    Image(
        modifier = modifier
            .size(dimensionResource(R.dimen.image_size))
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
        painter = painterResource(imageRes),
        contentDescription = description,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolOfficeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    settingUiState: Boolean,
    changSettingUiState: KFunction1<Boolean, Unit>,
    changBiometricSettingUiState: KFunction1<Boolean, Unit>,
    biometricSettingUiState: Boolean
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_size))
                        .padding(dimensionResource(R.dimen.padding_small)),
                    painter = painterResource(R.drawable.roofing_black_24dp),
                    contentDescription = stringResource(R.string.company_icon)
                )
                Text(
                    text = stringResource(R.string.pool_office_client),
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_spacer)))
                ElevatedButton(onClick = { changSettingUiState(!settingUiState) }) {
                    AnimatedVisibility(
                        settingUiState,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        Image(
                            painterResource(id = R.drawable.indeterminate_check_box_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.icon_for_enabling_additional_control_buttons),
                            modifier = Modifier.size(dimensionResource(R.dimen.padding_medium))
                        )
                    }
                    AnimatedVisibility(
                        !settingUiState,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        Image(
                            painterResource(id = R.drawable.select_check_box_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.icon_for_disabling_additional_control_buttons),
                            modifier = Modifier.size(dimensionResource(R.dimen.padding_medium))
                        )
                    }
                }

                Spacer(Modifier.width(dimensionResource(R.dimen.padding_spacer)))
                ElevatedButton(onClick = { changBiometricSettingUiState(!biometricSettingUiState) }) {
                    AnimatedVisibility(
                        biometricSettingUiState,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        Image(
                            painterResource(id = R.drawable.cancel_presentation_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.disable_fingerprint_login),
                            modifier = Modifier.size(dimensionResource(R.dimen.padding_medium))
                        )
                    }
                    AnimatedVisibility(
                        !biometricSettingUiState,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                    ) {
                        Image(
                            painterResource(id = R.drawable.baseline_fingerprint_24),
                            contentDescription = stringResource(R.string.enable_fingerprint_login),
                            modifier = Modifier.size(dimensionResource(R.dimen.padding_medium))
                        )
                    }
                }

            }
        },
        modifier = modifier
    )
}

@Composable
fun ShowButtons(
    switchAllRelay: (stateOnOff: Boolean) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                ButtonOnOff(
                    valueButton = false,
                    switchAllRelay = switchAllRelay,
                    Icons.Filled.Clear,
                    R.string.turn_off_everything,
                    modifier
                )
                Spacer(Modifier.weight(0.5f))
                ButtonOnOff(
                    valueButton = true,
                    switchAllRelay = switchAllRelay,
                    Icons.Filled.Check,
                    R.string.enable_everything,
                    modifier
                )
            }
        }
    }
}

@Composable
fun ButtonOnOff(
    valueButton: Boolean,
    switchAllRelay: (stateOnOff: Boolean) -> Unit,
    iconRes: ImageVector,
    @StringRes nameRes: Int,
    modifier: Modifier
) {
    Button(
        onClick = { switchAllRelay(valueButton) },
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            iconRes,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(nameRes))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PoolOfficeClientComposeTheme(darkTheme = false) {
        ErrorScreen(descriptionError = R.string.connection_error, retryAction = { /*TODO*/ })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPrevies() {
    PoolOfficeClientComposeTheme(darkTheme = false) {
        LoadingScreen()
    }
}

package com.example.poolofficeclientcompose.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poolofficeclientcompose.R
import com.example.poolofficeclientcompose.network.InitializationStateRelay
import com.example.poolofficeclientcompose.network.PoolInfoData
import com.example.poolofficeclientcompose.ui.theme.PoolOfficeClientComposeTheme

@Composable
fun HomeScreen(
    poolInfoDataUiState: PoolOfficeUiState,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    reloadAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (poolInfoDataUiState) {
        is PoolOfficeUiState.Success -> {
            SuccessScreen(
                poolOfficeSensor = poolInfoDataUiState.combineData.sensorsData,
                poolOfficeSwitch = poolInfoDataUiState.combineData.relayData,
                switchRelay
            )
        }

        is PoolOfficeUiState.Error -> {
            ErrorScreen(retryAction = reloadAllData, modifier = modifier.fillMaxSize())
        }

        is PoolOfficeUiState.Loading -> {
            LoadingScreen(modifier = modifier.fillMaxSize())
        }

    }
}

@Composable
fun SuccessScreen(
    poolOfficeSensor: PoolInfoData,
    poolOfficeSwitch: InitializationStateRelay,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AllPanelsInOne(
        poolOfficeSensor = poolOfficeSensor,
        poolOfficeSwitch = poolOfficeSwitch,
        switchRelay = switchRelay,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun AllPanelsInOne(
    poolOfficeSensor: PoolInfoData, poolOfficeSwitch: InitializationStateRelay,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit, modifier: Modifier = Modifier
) {
    val innerData =
        listOf(poolOfficeSensor.t1, poolOfficeSensor.t2, poolOfficeSensor.t3, poolOfficeSensor.p1)

    LazyColumn() {
        items(innerData.size) {
            TemperatureOrPumpItem(
                sensorIndicator = innerData[it],
                isTemperature = (it < 3),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
        items(poolOfficeSwitch.relayAnswer.size) {
            SwitchItem(
                it,
                poolOfficeSwitch.relayAnswer[it],
                switchRelay,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }

//    Column() {
//        var counterSensor = 0
//        var counterRelay = 0
//        innerData.forEach { item ->
//            TemperatureOrPumpItem(
//                sensorIndicator = item,
//                isTemperature = (counterSensor < 3),
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
//            )
//            counterSensor++
//        }
//        poolOfficeSwitch.relayAnswer.forEach { itemSwitch ->
//            SwitchItem(
//                counterRelay,
//                itemSwitch,
//                switchRelay,
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
//            )
//            counterRelay++
//        }
//    }
}

@Composable
fun SwitchItem(
    relayNumber: Int,
    stateOnOff: Boolean,
    switchRelay: (relayId: Int, stateOnOff: Boolean) -> Unit,
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
                SwitchIcon(switchIcon = R.drawable.thermostat_black_48dp)
                SwitchLabel(relayNumber + 1)
                Spacer(Modifier.weight(1f))
                RelaySwitch(relayNumber, stateOnOff, switchRelay)

            }
        }
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.connection_error)
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
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
        onCheckedChange = { switchRelay(relayId, !stateOnOff) },
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
fun SwitchLabel(relayNumber: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.relay, relayNumber),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
        Text(
            text = stringResource(R.string.switch_for_management),
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
                TemperatureOrPumpIcon(isTemperature)
                TemperatureOrPumpLabel(isTemperature)
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
    isTemperature: Boolean,
    sensorNumber: Int = 1,
    modifier: Modifier = Modifier
) {
    val textRes = if (isTemperature) {
        R.string.temperature
    } else {
        R.string.pump
    }
    Column(modifier = modifier) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
        )
        Text(
            text = stringResource(R.string.sensor_number, sensorNumber),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TemperatureOrPumpIcon(
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
        painter = if (isTemperature) {
            painterResource(R.drawable.termometr)
        } else {
            painterResource(R.drawable.pump)
        },

        contentDescription = description,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolOfficeTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
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
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PoolOfficeClientComposeTheme(darkTheme = false) {
        // SwitchPanel(
        // poolOfficeSwitch = InitializationStateRelay(
        //  arrayOf(true, false, true, false, true, false), 0,), switchRelay = {poolOffice})
    }
}

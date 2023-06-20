package com.jaehl.spaceTraders.ui.pages.runTask

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.AgentRole
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunTaskPage(
    viewModel : RunTaskViewModel
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(
                title = "RunTask",
                returnButton = true,
                onBackClick = {
                    viewModel.onBackClick()
                }
            )
            Column(modifier = Modifier
                .padding(20.dp)
                .width(800.dp)
            ) {
                AgentRoles(
                    modifier = Modifier,
                    viewModel = viewModel,
                    agents = viewModel.agentRoles,
                    agentRoleDropDownAgentId = viewModel.agentRoleDropDownAgentId.value
                )

                Button(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    onClick = {
                        viewModel.startTaskClick()
                    },
                    enabled = !viewModel.taskRunning.value
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Start Task"
                    )
                }
                AgentDetailsList(
                    modifier = Modifier,
                    viewModel = viewModel,
                    agentDetailsList = viewModel.agentStates
                )
            }
        }
    }
}

@Composable
fun AgentRoles(
    modifier: Modifier,
    viewModel : RunTaskViewModel,
    agents : List<AgentRole>,
    agentRoleDropDownAgentId : String
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(agents) { index, agent ->
            Agent(
                index = index,
                viewModel = viewModel,
                agentRole = agent,
                agentRoleDropDownAgentId = agentRoleDropDownAgentId
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Agent(
    index : Int,
    viewModel : RunTaskViewModel,
    agentRole : AgentRole,
    agentRoleDropDownAgentId : String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { }
                .padding(10.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                agentRole.shipId,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)

            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .onClick {
                            viewModel.openAgentRoleDropDown(agentShipId = agentRole.shipId)
                        },
                    text = agentRole.role.name
                )
                DropdownMenu(
                    expanded = (agentRoleDropDownAgentId == agentRole.shipId),
                    onDismissRequest = {}
                ){
                    viewModel.agentRoleTypes.forEach { role ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.onAgentRoleSelected(agentShipId = agentRole.shipId, role)
                            }
                        ) {
                            Text(role.name)
                        }
                    }
                }
            }


        }
    }
}

@Composable
fun AgentDetailsList(
    modifier: Modifier,
    viewModel : RunTaskViewModel,
    agentDetailsList : List<AgentDetailsViewModel>
) {
    LaunchedEffect(viewModel.taskRunning.value) {
        while (viewModel.taskRunning.value) {
            delay(1.seconds)
            viewModel.updateAgentTimers()
        }
    }
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(agentDetailsList) { index, agentDetails ->
            AgentDetails(
                modifier = Modifier,
                agentDetails = agentDetails,
            )
        }
    }
}

@Composable
fun AgentDetails(
    modifier: Modifier,
    agentDetails : AgentDetailsViewModel
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    agentDetails.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
                Text(
                    agentDetails.state,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
                Text(
                    agentDetails.fuel,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )

                Text(
                    agentDetails.cargo,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    agentDetails.stateDescription,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
                Text(
                    agentDetails.coolDownString,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
        }
    }
}

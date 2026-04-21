package me.alekseinovikov.open_gtd.features.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.alekseinovikov.open_gtd.db.Areas
import me.alekseinovikov.open_gtd.db.ListType
import me.alekseinovikov.open_gtd.db.Project
import me.alekseinovikov.open_gtd.db.TaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: TaskEntity,
    projects: List<Project>,
    areas: List<Areas>,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
) {
    var title by remember(task.id) { mutableStateOf(task.title) }
    var notes by remember(task.id) { mutableStateOf(task.notes.orEmpty()) }
    var listType by remember(task.id) {
        mutableStateOf(runCatching { ListType.valueOf(task.listType) }.getOrDefault(ListType.INBOX))
    }
    var projectId by remember(task.id) { mutableStateOf(task.projectId) }
    val sheetState = rememberModalBottomSheetState()

    val areaTitles = areas.associate { it.id to it.title }
    val grouped = projects.groupBy { it.areaId }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Edit task", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                minLines = 2,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
            )

            Text("List", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ListType.entries.forEach { entry ->
                    FilterChip(
                        selected = listType == entry,
                        onClick = { listType = entry },
                        label = { Text(entry.prettyLabel()) },
                    )
                }
            }

            ProjectDropdown(
                projects = projects,
                grouped = grouped,
                areaTitles = areaTitles,
                selectedProjectId = projectId,
                onSelect = { projectId = it },
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Delete")
                }
                OutlinedButton(onClick = onComplete) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Complete")
                }
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            onSave(
                                task.copy(
                                    title = title.trim().ifEmpty { task.title },
                                    notes = notes.trim().ifEmpty { null },
                                    listType = listType.name,
                                    projectId = projectId,
                                )
                            )
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.padding(start = 4.dp),
                    ) { Text("Save") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDropdown(
    projects: List<Project>,
    grouped: Map<String?, List<Project>>,
    areaTitles: Map<String, String>,
    selectedProjectId: String?,
    onSelect: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = projects.firstOrNull { it.id == selectedProjectId }?.title ?: "No project"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedTitle,
            onValueChange = {},
            readOnly = true,
            label = { Text("Project") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("No project") },
                onClick = {
                    onSelect(null)
                    expanded = false
                },
            )

            grouped[null]?.takeIf { it.isNotEmpty() }?.let { orphans ->
                GroupHeader("Projects")
                orphans.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.title) },
                        onClick = {
                            onSelect(p.id)
                            expanded = false
                        },
                    )
                }
            }

            areaTitles.forEach { (areaId, areaTitle) ->
                val inArea = grouped[areaId].orEmpty()
                if (inArea.isNotEmpty()) {
                    GroupHeader(areaTitle)
                    inArea.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.title) },
                            onClick = {
                                onSelect(p.id)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

private fun ListType.prettyLabel(): String = when (this) {
    ListType.INBOX -> "Inbox"
    ListType.TODAY -> "Today"
    ListType.UPCOMING -> "Upcoming"
    ListType.ANYTIME -> "Anytime"
    ListType.SOMEDAY -> "Someday"
}

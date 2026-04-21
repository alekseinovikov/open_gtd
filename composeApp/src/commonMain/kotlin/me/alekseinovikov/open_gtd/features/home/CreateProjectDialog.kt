package me.alekseinovikov.open_gtd.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.alekseinovikov.open_gtd.db.Areas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectDialog(
    areas: List<Areas>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, areaId: String?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var selectedAreaId by remember { mutableStateOf<String?>(null) }
    var dropdownOpen by remember { mutableStateOf(false) }

    val selectedAreaLabel = selectedAreaId?.let { id -> areas.firstOrNull { it.id == id }?.title } ?: "No area"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New project") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                ExposedDropdownMenuBox(
                    expanded = dropdownOpen,
                    onExpandedChange = { dropdownOpen = it },
                ) {
                    OutlinedTextField(
                        value = selectedAreaLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownOpen) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownOpen,
                        onDismissRequest = { dropdownOpen = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("No area") },
                            onClick = {
                                selectedAreaId = null
                                dropdownOpen = false
                            },
                        )
                        areas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area.title) },
                                onClick = {
                                    selectedAreaId = area.id
                                    dropdownOpen = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, selectedAreaId) },
                enabled = title.isNotBlank(),
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

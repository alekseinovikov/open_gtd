package me.alekseinovikov.open_gtd.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.alekseinovikov.open_gtd.db.Areas
import me.alekseinovikov.open_gtd.db.ListType
import me.alekseinovikov.open_gtd.db.Project

private data class ListMeta(val label: String, val icon: ImageVector)

private val listOrder = listOf(
    ListType.INBOX to ListMeta("Inbox", Icons.Default.Inbox),
    ListType.TODAY to ListMeta("Today", Icons.Default.Today),
    ListType.UPCOMING to ListMeta("Upcoming", Icons.Default.CalendarMonth),
    ListType.ANYTIME to ListMeta("Anytime", Icons.AutoMirrored.Filled.FormatListBulleted),
    ListType.SOMEDAY to ListMeta("Someday", Icons.Default.Bookmark),
)

@Composable
fun Sidebar(
    selection: Selection,
    areas: List<Areas>,
    projects: List<Project>,
    onSelect: (Selection) -> Unit,
    onAddArea: () -> Unit,
    onAddProject: () -> Unit,
) {
    val expanded = remember { mutableStateMapOf<String, Boolean>() }
    val orphanProjects = projects.filter { it.areaId == null }
    val projectsByArea = projects.groupBy { it.areaId }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        item { SectionHeader("Lists") }
        items(listOrder, key = { it.first.name }) { (type, meta) ->
            val sel = Selection.ListSel(type)
            NavItem(
                label = meta.label,
                icon = meta.icon,
                selected = selection == sel,
                onClick = { onSelect(sel) },
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
        item {
            SectionHeaderWithAction(
                title = "Areas",
                actionLabel = "Add area",
                onAction = onAddArea,
            )
        }
        if (areas.isEmpty()) {
            item { EmptyHint("No areas yet") }
        } else {
            items(areas, key = { it.id }) { area ->
                val areaSel = Selection.AreaSel(area.id)
                val isExpanded = expanded[area.id] ?: true
                val areaProjects = projectsByArea[area.id].orEmpty()
                NavItem(
                    label = area.title,
                    icon = Icons.Default.FolderSpecial,
                    selected = selection == areaSel,
                    onClick = { onSelect(areaSel) },
                    trailing = {
                        if (areaProjects.isNotEmpty()) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { expanded[area.id] = !isExpanded },
                            )
                        }
                    },
                )
                if (isExpanded) {
                    areaProjects.forEach { project ->
                        val projectSel = Selection.ProjectSel(project.id)
                        NavItem(
                            label = project.title,
                            icon = Icons.Default.Folder,
                            selected = selection == projectSel,
                            onClick = { onSelect(projectSel) },
                            indent = true,
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
        item {
            SectionHeaderWithAction(
                title = "Projects",
                actionLabel = "Add project",
                onAction = onAddProject,
            )
        }
        if (orphanProjects.isEmpty()) {
            item { EmptyHint("No unassigned projects") }
        } else {
            items(orphanProjects, key = { it.id }) { project ->
                val sel = Selection.ProjectSel(project.id)
                NavItem(
                    label = project.title,
                    icon = Icons.Outlined.Inventory2,
                    selected = selection == sel,
                    onClick = { onSelect(sel) },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun SectionHeaderWithAction(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onAction) {
            Icon(Icons.Default.Add, contentDescription = actionLabel, modifier = Modifier.padding(end = 4.dp))
            Text(actionLabel)
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
    )
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    indent: Boolean = false,
) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, contentDescription = null) },
        selected = selected,
        onClick = onClick,
        badge = trailing,
        modifier = Modifier.padding(
            start = if (indent) 24.dp else 0.dp,
            end = 0.dp,
        ),
        colors = NavigationDrawerItemDefaults.colors(),
    )
}


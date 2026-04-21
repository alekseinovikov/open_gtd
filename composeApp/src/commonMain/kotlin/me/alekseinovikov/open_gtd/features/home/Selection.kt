package me.alekseinovikov.open_gtd.features.home

import me.alekseinovikov.open_gtd.db.ListType

sealed interface Selection {
    data class ListSel(val type: ListType) : Selection
    data class ProjectSel(val projectId: String) : Selection
    data class AreaSel(val areaId: String) : Selection

    companion object {
        val Default: Selection = ListSel(ListType.INBOX)
    }
}

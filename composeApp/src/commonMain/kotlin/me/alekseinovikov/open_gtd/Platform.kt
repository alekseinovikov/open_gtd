package me.alekseinovikov.open_gtd

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
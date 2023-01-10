package dev.mr3n.morugrokbukkit

import dev.mr3n.Morugrok
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread

class MorugrokBukkit: JavaPlugin() {
    private lateinit var morugrokThread: Thread

    override fun onEnable() {
        val server = Bukkit.getServer()
        val port = server.port
        val name = System.getenv("MORUGROK_NAME")?:"NO NAME"
        val token = System.getenv("MORUGROK_TOKEN")
        morugrokThread = thread {
            runBlocking { Morugrok.start("localhost", port, port, name, token) }
        }
    }

    override fun onDisable() {
        try { morugrokThread.interrupt() } catch(_: Exception) {}
    }
}
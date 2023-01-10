package dev.mr3n.morugrokbukkit

import dev.mr3n.Morugrok
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import kotlin.concurrent.thread

class MorugrokBukkit: JavaPlugin() {
    private lateinit var morugrokThread: Thread

    private var isRunning = true

    override fun onEnable() {
        logger.level = Level.FINER
        val server = Bukkit.getServer()
        val port = server.port
        val name = System.getenv("MORUGROK_NAME")?:"NO NAME"
        val token = System.getenv("MORUGROK_TOKEN")
        morugrokThread = thread {
            while(isRunning) {
                try { runBlocking { Morugrok.start("localhost", port, port, name, token, logger) } } catch(_: Exception) {}
                logger.info("MORUGROKとの接続が切断されたため10秒後に再接続を試みます。")
                Thread.sleep(1000 * 10)
            }
        }
    }

    override fun onDisable() {
        isRunning = false
        try { morugrokThread.interrupt() } catch(_: Exception) {}
    }
}
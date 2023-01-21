package dev.mr3n.morugrokbukkit

import dev.mr3n.Morugrok
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.thread

class MorugrokBukkit: JavaPlugin() {
    // morugrokを実行するスレッド。
    private lateinit var morugrokThread: Thread

    // プラグインが実行中かどうか。これがfalseになると接続が切れた際に再接続しない。
    private var isRunning = true

    override fun onEnable() {
        saveDefaultConfig()
        // サーバー情報
        val server = Bukkit.getServer()
        val port: Int
        val name: String
        val token: String
        val maxFailures = config.getInt("max_failures", 20)
        if(config.getBoolean("use_env")) {
            port = System.getenv("MORUGROK_SERVER_NAME")?.toIntOrNull()?:server.port
            name = System.getenv("MORUGROK_SERVER_NAME")?:"NO NAME"
            token = System.getenv("MORUGROK_TOKEN")
        } else {
            port = config.getInt("port", server.port)
            name = config.getString("name")?:"NO NAME"
            token = config.getString("token")?:throw IllegalArgumentException("TOKEN情報を正しく設定してください。")
        }
        var countFailed = 0
        // websocketはスレッドを止めるから別スレッドで。
        morugrokThread = thread {
            // プラグインの実行中は何度切断されても再接続する。
            while(isRunning) {
                try { runBlocking {
                    Morugrok.start("localhost", port, port, name, token, logger) {
                        logger.info("morugrokとのコネクションを確立しました: 試行回数は${countFailed}回(${countFailed * 5}秒)でした")
                        countFailed = 0
                    }
                } } catch(_: Exception) { }
                if(countFailed > maxFailures) {
                    logger.warning("morugrokへのの接続失敗回数が、最大試行回数(${maxFailures})に達したため接続を停止しました。最大試行回数はconfig.ymlの`max_failures`で変更可能です。")
                    break
                }
                countFailed++
                logger.info("MORUGROKとの接続が切断されたため5秒後に再接続を試みます: ${countFailed}回目の試行//平均1.6回(8秒)の試行で再接続します")
                Thread.sleep(1000 * 5)
            }
        }
    }

    override fun onDisable() {
        // 実行中をfalseにして勝手に再接続しないようにする
        isRunning = false
        // morugrokの実行スレッドを強制停止
        try { morugrokThread.interrupt() } catch(_: Exception) {}
    }
}
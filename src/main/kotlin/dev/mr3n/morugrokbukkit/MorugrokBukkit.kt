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
        // サーバー情報
        val server = Bukkit.getServer()
        // サーバーのポート。めんどくさいので公開ポートも同じにする。
        val port = server.port
        // morugrokのコネクションの識別名。別にnullでもいいけど仕様変更するかもしれないので一応ない場合は"NO NAME"にする。
        val name = System.getenv("MORUGROK_NAME")?:"NO NAME"
        // morugrokのtoken
        val token = System.getenv("MORUGROK_TOKEN")
        // websocketはスレッドを止めるから別スレッドで。
        morugrokThread = thread {
            // プラグインの実行中は何度切断されても再接続する。
            while(isRunning) {
                // morugrokに接続する
                try { runBlocking { Morugrok.start("localhost", port, port, name, token, logger) } } catch(_: Exception) {}
                // Q.なぜ30秒？ A.websocketのタイムアウトが15秒だから。(最大まで判定に29.999...秒かかる。)
                logger.info("MORUGROKとの接続が切断されたため30秒後に再接続を試みます。")
                // こっそり31秒後に再試行
                Thread.sleep(1000 * 31)
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
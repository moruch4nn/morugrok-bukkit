# morugrok-bukkit
サーバー起動時に自動的にトンネリングサーバー([morugrok](https://github.com/moruch4nn/morugrok-server))を使用して公開するプラグイン。
### 環境変数で設定する
環境変数にTOKENとサーバー名を入れておく必要があります。`MORUGROK_TOKEN` `MORUGROK_SERVER_NAME` `MORUGROK_PORT`
### config.ymlで設定する
環境変数ではなくconfig.ymlから取得する場合はconfig.yml内の`use_env`をfalseに変更してください。<br>
その後config.yml内の`token` `port` `name`を設定してください。

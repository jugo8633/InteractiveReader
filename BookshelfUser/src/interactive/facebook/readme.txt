1. 要使用facebook sdk 要先產生api key, 產生方式如下指令：

keytool -exportcert -alias androiddebugkey -keystore ".android\debug.keystore" | openssl sha1 -binary | openssl base64

注意：debug.keystore 是android sdk的

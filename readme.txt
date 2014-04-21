[Android ketstore]
keytool -genkey -v -keystore AppCross.keystore -alias AppCross -keyalg RSA -keysize 2048 -validity 10000

[Android compress]
zipalign -f -v 4 AppCross.apk Kyoto.apk

keystore password: appcross

[Ant build]
android update project --path .
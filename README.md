# Henshin Client

Cliente de Minecraft Bedrock para Android, escrito desde cero por ESC5P4/Kauri.

## Licencia

Apache License 2.0. Ver `LICENSE` y `NOTICE`.

## Compilar

Requiere JDK 17 y Android SDK.

    chmod +x gradlew
    ./gradlew assembleDebug

El APK queda en `app/build/outputs/apk/debug/`.

## Estructura

- `app/`: aplicación Android (interfaz y, más adelante, módulos).
- `relay/`: conexión con el servidor (pendiente).

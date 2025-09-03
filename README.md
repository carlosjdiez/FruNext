# FruNext 🍇

**FruNext** es un videojuego desarrollado en **Java** con el framework [libGDX](https://libgdx.com/).  
El jugador controla a un personaje que debe superar niveles, recoger ítems y evitar enemigos para alcanzar el final.

---

## Cómo jugar

- **Objetivo:** completa cada nivel alcanzando la fruta final.
- **Controles:**
    - ← / →  → mover
    - Barra espaciadora → saltar
    - ESC → abrir menú de pausa (reanudar, sonido ON/OFF, volver al menú principal, salir)

En pantalla verás tu **nivel actual, vidas y monedas**.

El juego dispone de enemigos con comportamientos básicos, plataformas móviles y sonidos/animaciones en los personajes.

---

## Niveles con Tiled


- `level1.tmx`
- `level2.tmx`
 

Para añadir nuevos niveles, simplemente crea `level3.tmx`, `level4.tmx`, etc.  
El juego los detectará automáticamente y avanzará al siguiente si existe.

---

##  Requisitos

- Java 11+ (recomendado 17)
- Gradle Wrapper incluido (`./gradlew` / `gradlew.bat`)

---

## Ejecutar en escritorio

Desde la raíz del proyecto:

```bash
# En Linux / macOS
./gradlew lwjgl3:run

# En Windows
gradlew lwjgl3:run

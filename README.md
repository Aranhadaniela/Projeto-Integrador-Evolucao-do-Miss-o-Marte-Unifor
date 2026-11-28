# Missao Marte UNIFOR

## Sprint 1 — Arquitetura, Gradle e Testes
- Camadas separadas: `com.marte.domain` (regras puras sem I/O ou OpenGL), `com.marte.application` (`Missao`) e `com.marte.ui.console` (`Main`).
- Build com Gradle e testes em JUnit 5 (`./gradlew test` e `./gradlew run`).

## Sprint 2 — libGDX Desktop
- Modulos `core` (logica, tela, recursos com `dispose()`) e `lwjgl3` (`DesktopLauncher`).
- Controles: `WASD`/setas para mover, `C` para embarcar, `R` para reiniciar.
- Executar: `./gradlew :lwjgl3:run`.

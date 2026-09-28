# Casos de prueba – Grupo 5

Los casos 1–10 se ejecutan automáticamente con la **opción 10** del menú principal (29 verificaciones). Además, cada caso puede demostrarse manualmente en la sustentación siguiendo los pasos indicados. Tomar captura de cada uno y guardarla en `docs/capturas/`.

| # | Caso | Estructura | Pasos en el menú | Resultado esperado |
|---|------|-----------|------------------|--------------------|
| 1 | Registrar equipo y rechazar duplicado | Lista secuencial | 1 → 2 → `RED006`, Router, 4. Repetir con el mismo código | Primero `[OK]`; segundo `[ERROR] Ya existe un equipo con el codigo RED006` |
| 2 | Préstamo de equipo disponible | Lista simple | 2 → 1 → `RED001`, `Carlos Mena` → 2 → 3 | RED001 pasa a Prestado y aparece en préstamos activos |
| 3 | **Regla Grupo 5**: equipo en mantenimiento | Inventario + Cola | 2 → 1 → `RED003`. Luego 3 → 1 → `RED003` | `[ERROR] REGLA GRUPO 5 ...` en ambos: no se presta ni entra a la cola |
| 4 | Equipo ocupado pasa a la cola (FIFO) | Cola | 2 → 1 → `RED002`, `Pedro Ruiz` → `s` → 3 → 4 | Pedro queda al final; Luis Paredes sigue al FRENTE |
| 5 | Devolución asigna al primero de la cola | Lista simple + Cola | 2 → 2 → `RED002` → `n` | `RED002 fue asignado automaticamente a Luis Paredes (cola FIFO)` |
| 6 | Devolución con falla | Inventario + Cola | 2 → 2 → `RED002` → `s` → 3 → 4 | RED002 en Mantenimiento y sus solicitudes salen de la cola |
| 7 | Deshacer configuración (LIFO) | Pila | 7 → 2 → `RED005`, `SW2-CORE`, `192.168.10.60`, 24, 30 → 4 → 8 → `s` | Se restaura `SW2-LAB 192.168.10.3/24 VLAN 10` |
| 8 | Validaciones de red | Pila / modelo | 7 → 2 con IP `192.168.10.0` /24, o IP `192.168.10.1` (de RED001) | `[ERROR]` IP de red / IP ya asignada |
| 9 | Turnos circulares | Lista circular | 5 → 5 → `4` pasos; luego 5 → 4 | Tras Grupo C vuelve a Grupo A; al eliminar sigue la ronda |
| 10 | Historial en ambos sentidos | Lista doble | 4 → 1 y 4 → 2 | Mismos movimientos en orden cronológico e inverso |
| 11 | Entrada inválida | Validación general | Escribir `abc` o `99` en el menú | `Ingrese un numero entero valido` / `Ingrese un valor entre 0 y 10` |

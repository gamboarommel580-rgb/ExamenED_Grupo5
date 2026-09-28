# Sistema de Equipos de Redes y Routers – Grupo 5

**Asignatura:** Estructura de Datos · **Lenguaje:** Java 17 · **Entorno:** Visual Studio Code
**Caso asignado:** Grupo 5 – Sistema de equipos de redes y routers para prácticas de networking.

## Integrantes y distribución del trabajo

| Integrante | Usuario GitHub | Responsabilidad | Estructura / módulo | Estado |
|-----------|----------------|-----------------|---------------------|--------|
| Rommel | @gamboarommel580-rgb | Coordinación, repositorio e integración | `Main`, `util`, `DatosPrueba`, `PruebasSistema`, README | Terminado |
| Kerly | @kerlyespinoza298-dev | Inventario y mantenimiento | **Lista secuencial** + modelo `EquipoRed`, `ConfiguracionLogica` | Terminado |
| Sebastián | @sr632252-crypto | Préstamos y devoluciones | **Lista simplemente enlazada** + `Nodo` | Terminado |
| Gabriel | @Gabriel143445 | Cola de espera y configuración lógica | **Cola** y **Pila** | Terminado |
| Esteban | @Esteban-EVIL | Historial y turnos del banco de pruebas | **Lista doblemente enlazada** y **Lista circular** | Terminado |

> Actualizar la columna *Estado* (Pendiente → En progreso → Terminado) a medida que se integran los módulos.

## Descripción del caso

El laboratorio de networking presta routers, switches y access points a estudiantes o grupos. Cuando un equipo está prestado, los interesados esperan en una cola por orden de llegada. El banco de pruebas compartido se usa por turnos rotativos. Cada equipo tiene una configuración lógica (hostname, IP/prefijo y VLAN) y el último cambio de configuración puede revertirse.

**Regla diferenciadora:** un equipo enviado a mantenimiento **no puede aparecer como disponible ni ingresar a la cola de asignación**. Se aplica en tres puntos: no se presta, no se puede encolar y, cuando un equipo entra a mantenimiento (manualmente o por devolución con falla), sus solicitudes pendientes se retiran de la cola.

## Estructuras de datos utilizadas

| Estructura | Uso en el sistema | Por qué se eligió | Complejidad principal |
|-----------|-------------------|-------------------|-----------------------|
| Lista secuencial (arreglo) | Inventario general | El inventario es estable y se recorre/lista con frecuencia; el arreglo da acceso por índice O(1) y crece al duplicar su capacidad | Insertar O(1) amortizado · Buscar/Eliminar O(n) |
| Lista simplemente enlazada | Préstamos activos | Los préstamos se crean y eliminan constantemente; eliminar un nodo solo reenlaza punteros, sin desplazar elementos | Insertar al final O(1) · Buscar/Eliminar O(n) |
| Lista doblemente enlazada | Historial de movimientos | Permite ver el historial cronológico (adelante) y lo más reciente primero (atrás) sin invertir nada | Insertar al final O(1) · Recorridos O(n) |
| Cola (FIFO) | Solicitudes en espera | Es justo atender primero a quien llegó primero | Encolar/Desencolar O(1) |
| Pila (LIFO) | Cambios de configuración lógica | Deshacer siempre revierte el cambio más reciente | Apilar/Desapilar O(1) |
| Lista circular | Turnos del banco de pruebas | Después del último grupo el turno vuelve al primero sin reiniciar la lista | Insertar/Avanzar/Eliminar actual O(1) |

Todas las estructuras dinámicas usan nodos propios (`Nodo`, `NodoDoble`, `NodoCircular`). No se usan `LinkedList`, `Stack`, `Queue`, `ArrayDeque` ni `ArrayList`.

## Arquitectura

```
src/
├── Main.java              Menú principal e integración
├── estructuras/           Nodo, ListaSecuencial, ListaSimple, NodoDoble, ListaDoble,
│                          Cola, Pila, NodoCircular, ListaCircular
├── modelo/                EquipoRed, EstadoEquipo, TipoEquipo, ConfiguracionLogica,
│                          Prestamo, Solicitud, CambioConfiguracion, Movimiento,
│                          TipoMovimiento, Turno
├── servicios/             Lógica de negocio: Inventario, Prestamo, ColaEspera,
│                          Configuracion, Historial, Turno
├── menus/                 Submenús de consola de cada módulo
├── util/                  Consola (lectura validada), Resultado, Fechas
└── datos/                 DatosPrueba y PruebasSistema (casos automáticos)
```

Capas: **menús** (entrada/salida) → **servicios** (reglas de negocio y validaciones) → **estructuras** (almacenamiento). Los servicios devuelven un `Resultado` (éxito/error + mensaje) y no leen del teclado.

Flujo de integración: préstamo de equipo ocupado → **cola** · devolución → se atiende la **cola** automáticamente · devolución con falla → **mantenimiento** + depuración de la cola · toda operación → **historial** · cambio de configuración → **pila**.

## Datos de prueba y plan de direccionamiento

| Código | Tipo | Capacidad | Estado inicial | Configuración lógica |
|-------|------|-----------|----------------|----------------------|
| RED001 | Router | 4 puertos | Disponible | R1-LAB · 192.168.10.1/24 · VLAN 10 |
| RED002 | Switch | 24 puertos | Prestado (Grupo Redes A) | SW1-LAB · 192.168.10.2/24 · VLAN 10 |
| RED003 | Access Point | 2 antenas | Mantenimiento | AP1-LAB · 192.168.20.1/24 · VLAN 20 |
| RED004 | Router | 2 puertos | Prestado (Ana Torres) | R2-LAB · 10.0.0.1/30 · VLAN 99 |
| RED005 | Switch | 48 puertos | Disponible | SW2-LAB · 192.168.10.3/24 · VLAN 10 |

Cola inicial: Luis Paredes y Grupo Redes B esperan RED002. Turnos: Grupo A, B y C.

## Compilación y ejecución

Requisitos: JDK 17 o superior y la extensión *Extension Pack for Java* en VS Code.

**Opción 1 – VS Code:** abrir la carpeta del proyecto, abrir `src/Main.java` y pulsar **Run**.

**Opción 2 – Terminal integrada (Windows):**
```
javac -d bin src\*.java src\estructuras\*.java src\modelo\*.java src\servicios\*.java src\menus\*.java src\util\*.java src\datos\*.java
java -cp bin Main
```
o simplemente `compilar_y_ejecutar.bat`.

**Linux / macOS:** `./compilar_y_ejecutar.sh`

## Casos de prueba

La opción **10** del menú ejecuta 10 casos automáticos (29 verificaciones). El detalle y los pasos manuales están en [`docs/CASOS_PRUEBA.md`](docs/CASOS_PRUEBA.md). Las capturas de ejecución en VS Code deben incorporarse a `docs/capturas/` antes de la entrega.

Para verificar directamente el módulo de Gabriel después de compilar todo el proyecto, ejecutar `java -cp bin datos.PruebasGabriel` (26 verificaciones de cola, pila, espera y deshacer).

## Evidencia de colaboración

Cada integrante debe trabajar desde su cuenta y aportar commits significativos en una rama propia. La integración de módulos y la versión final en `main` siguen en progreso. Ver *Insights → Contributors* y el historial de commits del repositorio para comprobar la participación individual.

Gabriel presentó su módulo desde `@Gabriel143445` en el [PR #3](https://github.com/gamboarommel580-rgb/ExamenED_Grupo5/pull/3), con commits de implementación, pruebas y documentación. La integración al proyecto del grupo está pendiente de revisión.

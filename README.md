# LOGISTPULSE · Smart Inventory — Patrones Command, Chain of Responsibility y Strategy

Implementación en Java (17+) del flujo `POST /reservations`:

```
Command (Reserve) -> Chain (SKU -> stock -> lote -> capacidad) -> Receptor (Inventory)
```

## Estructura

```
src/main/java/com/logistpulse/
├── Main.java                  # Cliente (Command + Chain)
├── StrategyDemo.java          # Cliente (Strategy + Command + Chain)
├── model/                     # Lot, Location, LotState
├── inventory/Inventory.java   # Receptor (lógica de negocio)
├── chain/                     # Chain of Responsibility
│   ├── ValidationHandler.java #   manejador base
│   ├── SkuHandler.java
│   ├── StockHandler.java
│   ├── LotHandler.java
│   ├── CapacityHandler.java
│   ├── ReservationRequest.java
│   └── ValidationResult.java
├── command/                   # Command
│   ├── Command.java           #   interfaz comando
│   ├── ReserveCommand.java
│   ├── ReleaseCommand.java
│   ├── AdjustCommand.java
│   └── TransferCommand.java
├── invoker/
│   ├── CommandInvoker.java    # Emisor (historial, idempotencia, undo)
│   └── Ledger.java            # Auditoría
└── strategy/                  # Strategy
    ├── PickingPolicy.java     #   interfaz estrategia
    ├── FefoPolicy.java        #   estrategia concreta: vence primero, sale primero
    ├── FifoPolicy.java        #   estrategia concreta: ingresa primero, sale primero
    ├── LifoPolicy.java        #   estrategia concreta: ingresa último, sale primero
    ├── Picking.java           #   contexto
    └── Allocation.java        #   resultado del plan (lote + cantidad)
```

## Roles del patrón Command

| Rol | Clase |
|---|---|
| Emisor (Invoker) | `CommandInvoker` |
| Interfaz comando | `Command` |
| Comandos concretos | `ReserveCommand`, `ReleaseCommand`, `AdjustCommand`, `TransferCommand` |
| Receptor | `Inventory` |
| Cliente | `Main` |

## Roles del patrón Strategy

| Rol | Clase |
|---|---|
| Contexto | `Picking` |
| Interfaz estrategia | `PickingPolicy` |
| Estrategias concretas | `FefoPolicy`, `FifoPolicy`, `LifoPolicy` |
| Cliente | `StrategyDemo` |

`Picking.plan(sku, qty)` no modifica el inventario: devuelve una lista de `Allocation` que el cliente
convierte en `ReserveCommand`, de modo que cada reserva pasa por la cadena de validación.

## Compilar y ejecutar

```bash
javac -d out $(find src -name "*.java")
java -cp out com.logistpulse.Main          # Command + Chain
java -cp out com.logistpulse.StrategyDemo  # Strategy
```

En Windows (PowerShell):

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out com.logistpulse.Main
```

Fuente conceptual: Refactoring.Guru, patrones de comportamiento.

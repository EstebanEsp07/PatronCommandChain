# LOGISTPULSE · Smart Inventory — Patrones Command y Chain of Responsibility

Implementación en Java (17+) del flujo `POST /reservations`:

```
Command (Reserve) -> Chain (SKU -> stock -> lote -> capacidad) -> Receptor (Inventory)
```

## Estructura

```
src/main/java/com/logistpulse/
├── Main.java                  # Cliente
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
└── invoker/
    ├── CommandInvoker.java    # Emisor (historial, idempotencia, undo)
    └── Ledger.java            # Auditoría
```

## Roles del patrón Command

| Rol | Clase |
|---|---|
| Emisor (Invoker) | `CommandInvoker` |
| Interfaz comando | `Command` |
| Comandos concretos | `ReserveCommand`, `ReleaseCommand`, `AdjustCommand`, `TransferCommand` |
| Receptor | `Inventory` |
| Cliente | `Main` |

## Compilar y ejecutar

```bash
javac -d out $(find src -name "*.java")
java -cp out com.logistpulse.Main
```

En Windows (PowerShell):

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out com.logistpulse.Main
```

Fuente conceptual: Refactoring.Guru, patrones de comportamiento.

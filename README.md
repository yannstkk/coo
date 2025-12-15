# Bike-Sharing Simulation System

## Overview

This system simulates a bike-sharing service with multiple stations, different types of vehicles (classic bikes and electric bikes with accessories), users, and redistribution strategies. The architecture relies on several design patterns that ensure flexibility, extensibility, and maintainability of the code.

## General Architecture

The system is organized around a central `Simulation` class that orchestrates all components: stations, vehicles, users, control center, technician, and random generator. This modular architecture provides a clear separation of responsibilities and makes the system easier to evolve.

## Implemented Design Patterns

### 1. Observer Pattern

**Problem addressed:**  
How can stations automatically notify the control center when they require bike redistribution, without creating tight coupling between these components?

**Implementation:**

- **Stations** act as observable subjects (`Subject`)
- The **ControlCenter** acts as an observer (`Observer`)
- Stations notify the control center via `notifyObservers()` when users rent or return bikes
- The control center registers itself with stations using `attach(Observer)`

**Advantages:**

- Loose coupling between stations and the control center
- Easy addition of new observers without modifying station code
- Real-time automatic notifications of state changes

**Usage scenario:**  
When a station becomes empty or overloaded for two consecutive time intervals, it automatically notifies the control center, which can then trigger a redistribution.

---

### 2. State Pattern

**Problem addressed:**  
How can the different states of a vehicle (available, in use, under repair, stolen) and their transitions be managed efficiently without an explosion of `if/else` conditions?

**Implementation:**

- State hierarchy: `VehicleState` (interface) with four concrete implementations:
  - `ParkedState`: vehicle parked and available
  - `InUseState`: vehicle currently rented
  - `UnderRepairState`: vehicle under maintenance
  - `StolenState`: vehicle reported stolen
- Each state encapsulates behavior specific to that context
- State transitions are handled by the state objects themselves

**Advantages:**

- More readable and maintainable code (no nested conditionals)
- Easy addition of new states without modifying existing code
- Clear encapsulation of state-specific behavior
- Explicit and controlled state transitions

**Usage scenario:**  
When a user rents a bike, the vehicle automatically transitions from `ParkedState` to `InUseState`.  
When the bike is returned to a station, if the number of rentals reaches a threshold (set to 5), the bike transitions to `UnderRepairState`; otherwise, it returns to `ParkedState`.  
Invalid transitions throw an `IllegalStateException`.

---

### 3. Strategy Pattern

**Problem addressed:**  
How can different bike redistribution algorithms be used interchangeably without modifying the control center code?

**Implementation:**

- `Distribution` interface defining the contract `distribute(Station[])`
- Concrete strategies:
  - `RoundRobin`: sequential and fair distribution
  - `RandomDistribution`: random distribution based on a generator
- The `ControlCenter` delegates redistribution to the configured strategy

**Advantages:**

- Algorithm switching at runtime without recompilation
- Easy addition of new strategies without changing existing code
- Simplified unit testing of each strategy independently
- Compliance with the Open/Closed Principle

**Usage scenario:**  
The system can switch to a fair distribution strategy during peak hours and to a random strategy during off-peak hours.

---

### 4. Decorator Pattern

**Problem addressed:**  
How can features (accessories) be added dynamically to vehicles without creating a combinatorial explosion of subclasses?

**Implementation:**

- Abstract class `VehicleDecorator` that extends and wraps `Vehicle`
- Concrete decorators:
  - `Basket`: adds a front basket
  - `LuggageRack`: adds a rear rack
- Decorators can be stacked to combine multiple accessories
- Each decorator adds its cost to the base price

**Advantages:**

- Flexible runtime composition of accessories
- Avoids creating classes such as `BicycleWithBasketAndRack`
- Easy addition of new accessories without modifying existing vehicles
- Respects the Single Responsibility Principle

**Usage scenario:**  
A classic bike can be equipped with a basket, a luggage rack, or both, with automatic calculation of the total price.

---

### 5. Visitor Pattern

**Problem addressed:**  
How can different operations (price calculation, maintenance) be performed on vehicles without modifying their classes while respecting the Open/Closed Principle?

**Implementation:**

- `Visitor` interface with the method `visit(Vehicle)`
- Concrete visitors:
  - `Technician` for maintenance operations
- Vehicles accept visitors via `accept(Visitor)`

**Advantages:**

- New operations can be added without modifying vehicle classes
- Business logic centralized in visitor classes
- Double dispatch to select the correct operation
- Facilitates cross-cutting functionality

**Usage scenario:**  
The technician visits each vehicle to perform maintenance.

---

## Pattern Interactions

1. **Observer + Strategy**: The control center observes stations and uses a strategy to redistribute bikes
2. **State + Visitor**: Visitors perform different operations depending on the vehicle state
3. **Decorator + Visitor**: Visitors traverse the decorator chain to compute total prices

---

## System Extensibility

- **New vehicle types**: extend the `Vehicle` class
- **New states**: implement the `VehicleState` interface
- **New accessories**: create new decorators
- **New redistribution strategies**: implement the `Distribution` interface
- **New operations**: create new visitors
- **New observers**: implement the `Observer` interface

---

## SOLID Principles Respected

- **Single Responsibility Principle**
- **Open/Closed Principle**
- **Liskov Substitution Principle**
- **Interface Segregation Principle**
- **Dependency Inversion Principle**

--

## Simulation

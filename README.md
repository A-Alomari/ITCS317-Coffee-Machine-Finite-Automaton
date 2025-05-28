# ☕ Coffee Machine Finite Automaton

This project is an implementation of a finite automaton for a coffee vending machine. The machine simulates a real-world interaction where users can select a drink, insert coins, handle invalid input, refill ingredients, and receive change.

## 📝 Overview

The task was to design and implement a finite automaton for a coffee machine with the following specifications:

* **Startup**: The machine begins in the OFF state. Pressing the power button starts the machine.
* **States**:

  * `OFF`
  * `IDLE`
  * `WORKING`
  * `INSERTING_MONEY`
  * `INVALID_COIN`
  * `OUT_OF_INGREDIENTS`
  * `DISPENSING`
  * `WAITING_FOR_CHANGE`
* **Drink Options**:

  * Espresso — 0.5 BD
  * Coffee Latte — 0.7 BD
* **Accepted Coins**:

  * 1 BD
  * 0.5 BD
  * 0.1 BD
* **Invalid Coins**: Any other denominations are rejected.
* **User Interaction**:

  * The user first selects a drink.
  * The machine prompts for coin input until the price is met or exceeded.
  * If ingredients are insufficient, the machine asks for a refill.
  * After the drink is dispensed, change (if any) is returned.
  * A timeout mechanism resets the machine to the IDLE state after inactivity.

## 🛠️ Technologies Used

* Java
* JavaFX (GUI framework)

## 🎮 How to Use

1. **Launch the application**:
   Run the `CoffeeMachineApp` class. Make sure you have JavaFX configured in your environment.

2. **Power On**:
   Press the **Power** button to begin.

3. **Select a Drink**:
   Choose between **Espresso** or **Latte**.

4. **Insert Coins**:
   Use the buttons to insert valid coins until the price is met. Invalid coins will be rejected and returned.

5. **Refill (if required)**:
   If ingredients are insufficient, refill them using the respective buttons and press **Continue**.

6. **Receive Drink & Change**:
   After dispensing, take your drink and collect any change.

7. **Timeout Handling**:
   If no action is taken for 10 seconds at any state, the machine returns to IDLE.

## 📂 Project Structure
com/
└── example/
└── project317/
└── CoffeeMachineApp.java

## ✅ Features
* Fully interactive GUI with JavaFX
* State-driven logic mimicking a finite automaton
* Validation of input and change handling
* Ingredient stock management and refill prompt
* Timeout mechanism to reset the system

 ## ⚠️ Known Issues
* **Timeout Money Handling**:

  * When timeout occurs during the INSERTING_MONEY state:
  * Inserted coins are not saved
  * Money resets to zero without returning coins
  * **Expected behavior**: 
should return inserted coins before timeout

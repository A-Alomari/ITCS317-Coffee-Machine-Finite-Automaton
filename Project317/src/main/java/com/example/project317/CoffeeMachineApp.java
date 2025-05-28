package com.example.project317;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CoffeeMachineApp extends Application {
    private enum State {
        OFF, IDLE, WORKING, INSERTING_MONEY, INVALID_COIN, OUT_OF_INGREDIENTS, DISPENSING, WAITING_FOR_CHANGE,
    }

    private State currentState = State.OFF;
    private double insertedMoney = 0.0;
    private double drinkPrice = 0.0;
    private String selectedDrink = "";
    private double change = 0.0;
    private int water = 0, coffee = 0, milk = 0;

    private Label stateLabel = new Label("State: OFF");
    private Label messageLabel = new Label("Press Power to Start");
    private Label stockLabel = new Label();
    private Label missingLabel = new Label();

    private Button powerBtn = new Button("Power");
    private Button refillWaterBtn = new Button("Refill Water");
    private Button refillCoffeeBtn = new Button("Refill Coffee");
    private Button refillMilkBtn = new Button("Refill Milk");
    private Button continueAfterRefillBtn = new Button("Continue");
    private Button espressoBtn = new Button("Espresso (0.5 BD)");
    private Button latteBtn = new Button("Latte (0.7 BD)");
    private Button[] validCoinBtns;
    private Button[] invalidCoinBtns;

    private Button takeInvalidBtn = new Button("Take Invalid Coin");
    private Button takeDrinkBtn = new Button("Take Drink");
    private Button takeChangeBtn = new Button("Take Change");

    private VBox root;
    private VBox statusSection;
    private VBox powerSection;
    private VBox drinkSection;
    private VBox coinSection;
    private VBox refillSection;
    private VBox takeDrinkSection;
    private VBox takeChangeSection;
    private VBox invalidCoinSection;

    private PauseTransition timeout = new PauseTransition(Duration.seconds(10));

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(30);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center; -fx-background-color: white;");

        stateLabel.setStyle("-fx-font-size: 16px;");
        messageLabel.setStyle("-fx-font-size: 14px;");
        stockLabel.setStyle("-fx-font-size: 14px;");
        missingLabel.setStyle("-fx-text-fill: red;");

        statusSection = new VBox(5, stateLabel, messageLabel);
        statusSection.setStyle("-fx-alignment: center;");

        powerSection = new VBox(10, powerBtn);
        powerSection.setStyle("-fx-alignment: center;");

        drinkSection = new VBox(10, new Label("Select Drink:"), new HBox(10, espressoBtn, latteBtn));
        drinkSection.setStyle("-fx-alignment: center;");
        ((HBox) drinkSection.getChildren().get(1)).setStyle("-fx-alignment: center;");

        Button coin100 = new Button("100F");
        Button coin500 = new Button("500F");
        Button coin1bd = new Button("1BD");
        validCoinBtns = new Button[]{coin100, coin500, coin1bd};

        Button coin10f = new Button("10F");
        Button coin25f = new Button("25F");
        Button coin50f = new Button("50F");
        Button coin5bd = new Button("5BD");
        Button coin10bd = new Button("10BD");
        Button coin20bd = new Button("20BD");
        invalidCoinBtns = new Button[]{coin10f, coin25f, coin50f, coin5bd, coin10bd, coin20bd};

        HBox validCoinRow = new HBox(10, coin100, coin500, coin1bd);
        HBox invalidCoinRow = new HBox(10, coin10f, coin25f, coin50f, coin5bd, coin10bd, coin20bd);
        validCoinRow.setStyle("-fx-alignment: center;");
        invalidCoinRow.setStyle("-fx-alignment: center;");

        coinSection = new VBox(10, validCoinRow, invalidCoinRow);
        coinSection.setStyle("-fx-alignment: center;");

        VBox refillButtons = new VBox(10, refillWaterBtn, refillCoffeeBtn, refillMilkBtn);
        refillButtons.setStyle("-fx-alignment: center;");
        refillSection = new VBox(10,
                new Label("Not enough ingredients! Please refill below."),
                missingLabel,
                refillButtons,
                new Label("Click Continue after refilling."),
                continueAfterRefillBtn);
        refillSection.setStyle("-fx-alignment: center;");

        takeDrinkSection = new VBox(10, new Label("Please take your drink."), takeDrinkBtn);
        takeDrinkSection.setStyle("-fx-alignment: center;");

        takeChangeSection = new VBox(10, new Label("Please take your change."), takeChangeBtn);
        takeChangeSection.setStyle("-fx-alignment: center;");

        invalidCoinSection = new VBox(10, new Label("Invalid coin inserted! Please take it."), takeInvalidBtn);
        invalidCoinSection.setStyle("-fx-alignment: center;");

        stockLabel.setStyle("-fx-padding: 20px;");

        root.getChildren().addAll(statusSection, powerSection, stockLabel);
        setupEventHandlers();
        showOnly(powerSection);

        primaryStage.setTitle("Coffee Machine");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    private void setupEventHandlers() {
        powerBtn.setOnAction(e -> {
            if (currentState == State.OFF || currentState == State.IDLE) {
                switchToWorkingState();
            }
            timeout.playFromStart();
        });
        espressoBtn.setOnAction(e -> {
            selectDrink("Espresso", 0.5);
            timeout.playFromStart();
        });
        latteBtn.setOnAction(e -> {
            selectDrink("Latte", 0.7);
            timeout.playFromStart();
        });

        for (Button btn : validCoinBtns) {
            btn.setOnAction(e -> {
                handleCoinInput(btn.getText());
                timeout.playFromStart();
            });
        }

        for (Button btn : invalidCoinBtns) {
            btn.setOnAction(e -> {
                currentState = State.INVALID_COIN;
                stateLabel.setText("State: INVALID_COIN");
                messageLabel.setText("Invalid coin inserted: " + btn.getText());
                showOnly(invalidCoinSection);
                timeout.playFromStart();
            });
        }

        takeInvalidBtn.setOnAction(e -> {
            messageLabel.setText("Please insert valid coins.");
            currentState = State.INSERTING_MONEY;
            stateLabel.setText("State: INSERTING_MONEY");
            showOnly(coinSection);
            timeout.playFromStart();
        });

        refillWaterBtn.setOnAction(e -> { water = 3;
            updateStockLabel();
            updateMissingLabel();
            timeout.playFromStart(); });
        refillCoffeeBtn.setOnAction(e -> { coffee = 3; updateStockLabel(); updateMissingLabel(); timeout.playFromStart(); });
        refillMilkBtn.setOnAction(e -> { milk = 3; updateStockLabel(); updateMissingLabel(); timeout.playFromStart(); });

        continueAfterRefillBtn.setOnAction(e -> {
            if (hasEnoughIngredients()) {
                dispenseDrink();
            } else {
                updateMissingLabel();
                messageLabel.setText("Still missing ingredients. Please refill.");
            }
            timeout.playFromStart();
        });

        takeDrinkBtn.setOnAction(e -> {
            takeDrinkOrChange();
            timeout.playFromStart();
        });
        takeChangeBtn.setOnAction(e -> {
            switchToIdleState();
            timeout.playFromStart();
        });

        timeout.setOnFinished(e -> {
            messageLabel.setText("Timed out. Going to Idle state.");
            switchToIdleState();
        });
    }

    private void switchToIdleState() {
        currentState = State.IDLE;
        stateLabel.setText("State: IDLE");
        messageLabel.setText("Machine is idle. Press Power to start.");
        resetTransaction();
        showOnly(powerSection);
        timeout.stop();
    }

    private void switchToWorkingState() {
        currentState = State.WORKING;
        stateLabel.setText("State: WORKING");
        messageLabel.setText("Select a drink.");
        updateStockLabel();
        resetTransaction();
        showOnly(drinkSection);
        timeout.playFromStart();
    }

    private void selectDrink(String drink, double price) {
        if (currentState != State.WORKING) return;
        selectedDrink = drink;
        drinkPrice = price;
        currentState = State.INSERTING_MONEY;
        stateLabel.setText("State: INSERTING_MONEY");
        messageLabel.setText("Selected " + drink + ". Please insert " + price + " BD.");
        showOnly(coinSection);
        timeout.playFromStart();
    }

    private void handleCoinInput(String label) {
        if (currentState != State.INSERTING_MONEY) return;
        double value;
        switch (label) {
            case "100F": value = 0.1; break;
            case "500F": value = 0.5; break;
            case "1BD": value = 1.0; break;
            default: return;
        }

        insertedMoney += value;
        if (insertedMoney >= drinkPrice) {
            if (!hasEnoughIngredients()) {
                currentState = State.OUT_OF_INGREDIENTS;
                messageLabel.setText("Not enough ingredients!");
                stateLabel.setText("State: OUT_OF_INGREDIENTS");
                updateMissingLabel();
                showOnly(refillSection);
            } else {
                dispenseDrink();
            }
        } else {
            messageLabel.setText("Inserted: " + String.format("%.2f", insertedMoney) + " BD. " +
                    "Need more: " + String.format("%.2f", (drinkPrice - insertedMoney)) + " BD.");
        }
        timeout.playFromStart();
    }

    private void dispenseDrink() {
        currentState = State.DISPENSING;
        stateLabel.setText("State: DISPENSING");
        useIngredients();
        change = insertedMoney - drinkPrice;
        messageLabel.setText("Dispensing " + selectedDrink + "...");
        updateStockLabel();
        showOnly(takeDrinkSection);
        timeout.playFromStart();
    }

    private void takeDrinkOrChange() {
        if (change > 0) {
            currentState = State.WAITING_FOR_CHANGE;
            stateLabel.setText("State: WAITING_FOR_CHANGE");
            messageLabel.setText("Remaining change: " + String.format("%.2f", change) + " BD");
            showOnly(takeChangeSection);
            timeout.playFromStart();
        } else {
            switchToIdleState();
        }
    }

    private void resetTransaction() {
        insertedMoney = 0;
        drinkPrice = 0;
        selectedDrink = "";
        change = 0;
    }

    private boolean hasEnoughIngredients() {
        return selectedDrink.equals("Espresso")
                ? water > 0 && coffee > 0
                : water > 0 && coffee > 0 && milk > 0;
    }

    private void useIngredients() {
        if (selectedDrink.equals("Espresso")) {
            water--; coffee--;
        } else {
            water--; coffee--; milk--;
        }
    }

    private void updateStockLabel() {
        stockLabel.setText("Water: " + water + " | Coffee: " + coffee + " | Milk: " + milk);
    }

    private void updateMissingLabel() {
        StringBuilder missing = new StringBuilder("Missing: ");
        if (selectedDrink.equals("Espresso")) {
            if (water == 0) missing.append("Water ");
            if (coffee == 0) missing.append("Coffee ");
        } else {
            if (water == 0) missing.append("Water ");
            if (coffee == 0) missing.append("Coffee ");
            if (milk == 0) missing.append("Milk ");
        }
        missingLabel.setText(missing.toString());
    }

    private void showOnly(Pane visibleSection) {
        root.getChildren().setAll(statusSection, visibleSection, stockLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
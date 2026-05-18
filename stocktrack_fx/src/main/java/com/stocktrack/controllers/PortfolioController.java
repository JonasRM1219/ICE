package com.stocktrack.controllers;

import com.stocktrack.models.*;
import com.stocktrack.services.StorageService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.UUID;

public class PortfolioController {

    private Portfolio portfolio;
    private MainController main;
    private TableView<Holding> table;

    public PortfolioController(Portfolio portfolio, MainController main) {
        this.portfolio = portfolio;
        this.main = main;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(28));
        view.setStyle("-fx-background-color: #0A0F0A;");

        // Header
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Portefølje");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#E8F5E9"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Tilføj aktie");
        addBtn.setStyle("""
            -fx-background-color: #4ADE80;
            -fx-text-fill: #0A0F0A;
            -fx-font-weight: bold;
            -fx-font-size: 13px;
            -fx-background-radius: 10;
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
            """);
        addBtn.setOnAction(e -> showAddDialog());

        header.getChildren().addAll(title, spacer, addBtn);

        // Summary cards
        HBox summary = buildSummaryCards();

        // Table
        table = buildTable();

        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(header, summary, table);
        return view;
    }

    private HBox buildSummaryCards() {
        HBox row = new HBox(12);

        double totalValue = portfolio.getTotalValue();
        double totalGain  = portfolio.getTotalGain();
        double totalTax   = portfolio.getTotalTax();
        double totalCost  = portfolio.getTotalCost();
        double gainPct    = totalCost > 0 ? (totalGain / totalCost) * 100 : 0;

        row.getChildren().addAll(
            summaryCard("Samlet værdi",   String.format("%.2f kr", totalValue), "#E8F5E9"),
            summaryCard("Gevinst/Tab",    String.format("%+.2f kr (%.1f%%)", totalGain, gainPct),
                        totalGain >= 0 ? "#4ADE80" : "#FF6B6B"),
            summaryCard("Est. skat",      String.format("%.2f kr", totalTax), "#FB923C"),
            summaryCard("Investeret",     String.format("%.2f kr", totalCost), "#86EFAC")
        );
        return row;
    }

    private VBox summaryCard(String label, String value, String valueColor) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 12;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 12;
            -fx-border-width: 1;
            """);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web("#86EFAC"));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        val.setTextFill(Color.web(valueColor));

        card.getChildren().addAll(lbl, val);
        return card;
    }

    @SuppressWarnings("unchecked")
    private TableView<Holding> buildTable() {
        TableView<Holding> tv = new TableView<>();
        tv.setStyle("""
            -fx-background-color: #111811;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Holding, String> colTicker = new TableColumn<>("Ticker");
        colTicker.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTicker()));

        TableColumn<Holding, String> colName = new TableColumn<>("Navn");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Holding, String> colShares = new TableColumn<>("Stk");
        colShares.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.0f", c.getValue().getShares())));

        TableColumn<Holding, String> colBuy = new TableColumn<>("Købt til");
        colBuy.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.2f kr", c.getValue().getAvgBuyPrice())));

        TableColumn<Holding, String> colValue = new TableColumn<>("Værdi");
        colValue.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.2f kr", c.getValue().getCurrentValue())));

        TableColumn<Holding, String> colGain = new TableColumn<>("Gevinst");
        colGain.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%+.2f kr (%.1f%%)",
                c.getValue().getUnrealizedGain(), c.getValue().getUnrealizedGainPct())));
        colGain.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(item.startsWith("+") ? Color.web("#4ADE80") : Color.web("#FF6B6B"));
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            }
        });

        TableColumn<Holding, String> colTax = new TableColumn<>("Est. skat");
        colTax.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.2f kr", c.getValue().estimatedTax())));
        colTax.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(Color.web("#FB923C"));
            }
        });

        TableColumn<Holding, String> colAccount = new TableColumn<>("Konto");
        colAccount.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getAccountType().getLabel()));

        TableColumn<Holding, Void> colActions = new TableColumn<>("Handlinger");
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button sellBtn = new Button("Sælg");
            private final Button divBtn  = new Button("Udbytte");
            private final Button delBtn  = new Button("Slet");
            private final HBox box = new HBox(4, sellBtn, divBtn, delBtn);

            {
                String btnStyle = """
                    -fx-font-size: 11px;
                    -fx-background-radius: 6;
                    -fx-padding: 4 8 4 8;
                    -fx-cursor: hand;
                    """;
                sellBtn.setStyle("-fx-background-color: #166534; -fx-text-fill: #4ADE80;" + btnStyle);
                divBtn.setStyle("-fx-background-color: #1E3A26; -fx-text-fill: #86EFAC;" + btnStyle);
                delBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5;" + btnStyle);
                box.setAlignment(Pos.CENTER);

                sellBtn.setOnAction(e -> showSellDialog(getTableView().getItems().get(getIndex())));
                divBtn.setOnAction(e -> showDividendDialog(getTableView().getItems().get(getIndex())));
                delBtn.setOnAction(e -> deleteHolding(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(colTicker, colName, colShares, colBuy, colValue, colGain, colTax, colAccount, colActions);
        tv.setItems(FXCollections.observableArrayList(portfolio.getHoldings()));
        tv.setPlaceholder(new Label("Ingen aktier endnu — tryk '+ Tilføj aktie'"));
        return tv;
    }

    private void showAddDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tilføj aktie");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tickerField = new TextField();
        tickerField.setPromptText("fx AAPL, NOVO-B.CO");

        TextField nameField = new TextField();
        nameField.setPromptText("fx Apple Inc");

        TextField sharesField = new TextField();
        sharesField.setPromptText("fx 10");

        TextField priceField = new TextField();
        priceField.setPromptText("fx 1500.00");

        ComboBox<AccountType> accountBox = new ComboBox<>();
        accountBox.getItems().addAll(AccountType.values());
        accountBox.setValue(AccountType.FRIE_MIDLER);

        grid.add(new Label("Ticker:"), 0, 0);       grid.add(tickerField, 1, 0);
        grid.add(new Label("Navn:"), 0, 1);         grid.add(nameField, 1, 1);
        grid.add(new Label("Antal aktier:"), 0, 2); grid.add(sharesField, 1, 2);
        grid.add(new Label("Købspris (kr):"), 0, 3); grid.add(priceField, 1, 3);
        grid.add(new Label("Kontotype:"), 0, 4);    grid.add(accountBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String ticker = tickerField.getText().trim().toUpperCase();
                    String name   = nameField.getText().trim().isEmpty()
                        ? ticker : nameField.getText().trim();
                    double shares = Double.parseDouble(sharesField.getText().replace(",", ".").trim());
                    double price  = Double.parseDouble(priceField.getText().replace(",", ".").trim());

                    Holding h = new Holding(UUID.randomUUID().toString(),
                        ticker, name, shares, price, accountBox.getValue(), LocalDate.now());
                    portfolio.addHolding(h);
                    StorageService.savePortfolio(portfolio);
                    main.refresh();
                } catch (NumberFormatException e) {
                    MainController.showAlert("Fejl", "Tjek at antal og pris er tal.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void showSellDialog(Holding h) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Registrér salg – " + h.getTicker());
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));

        TextField sharesField = new TextField();
        sharesField.setPromptText("fx 5");
        TextField priceField = new TextField();
        priceField.setPromptText("fx 1800.00");

        grid.add(new Label("Antal solgte aktier:"), 0, 0); grid.add(sharesField, 1, 0);
        grid.add(new Label("Salgspris pr. stk (kr):"), 0, 1); grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    double shares = Double.parseDouble(sharesField.getText().replace(",", ".").trim());
                    double price  = Double.parseDouble(priceField.getText().replace(",", ".").trim());
                    Sale sale = new Sale(UUID.randomUUID().toString(),
                        shares, price, h.getAvgBuyPrice(), LocalDate.now(), h.getAccountType());
                    h.addSale(sale);
                    StorageService.savePortfolio(portfolio);
                    MainController.showAlert("Salg registreret",
                        String.format("Gevinst: %.2f kr%nSkat: %.2f kr%nNetto: %.2f kr",
                            sale.getGain(), sale.getTax(), sale.getNetGain()),
                        Alert.AlertType.INFORMATION);
                    main.refresh();
                } catch (NumberFormatException e) {
                    MainController.showAlert("Fejl", "Tjek at antal og pris er tal.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void showDividendDialog(Holding h) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registrér udbytte – " + h.getTicker());
        dialog.setHeaderText(null);
        dialog.setContentText("Udbyttebeløb (kr):");
        dialog.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input.replace(",", ".").trim());
                h.addDividend(new Dividend(UUID.randomUUID().toString(), amount, LocalDate.now()));
                StorageService.savePortfolio(portfolio);
                MainController.showAlert("Udbytte registreret",
                    String.format("%.2f kr tilføjet til %s", amount, h.getTicker()),
                    Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                MainController.showAlert("Fejl", "Indtast et gyldigt beløb.", Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteHolding(Holding h) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Slet aktie");
        confirm.setHeaderText(null);
        confirm.setContentText("Er du sikker på du vil slette " + h.getTicker() + "?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                portfolio.removeHolding(h.getId());
                StorageService.savePortfolio(portfolio);
                main.refresh();
            }
        });
    }
}

package com.stocktrack.controllers;

import com.stocktrack.models.Holding;
import com.stocktrack.models.Portfolio;
import com.stocktrack.models.Sale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class HistoryController {

    private Portfolio portfolio;

    public HistoryController(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(28));
        view.setStyle("-fx-background-color: #0A0F0A;");

        Label title = new Label("Salgshistorik");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#E8F5E9"));

        // Saml alle salg
        record SaleRow(String ticker, Sale sale) {}
        ArrayList<SaleRow> rows = new ArrayList<>();
        for (Holding h : portfolio.getHoldings()) {
            for (Sale s : h.getSales()) {
                rows.add(new SaleRow(h.getTicker(), s));
            }
        }

        if (rows.isEmpty()) {
            Label empty = new Label("Ingen salg registreret endnu.\nRegistrér et salg fra portefølje-skærmen.");
            empty.setTextFill(Color.web("#86EFAC"));
            empty.setFont(Font.font("Segoe UI", 14));
            view.getChildren().addAll(title, empty);
            return view;
        }

        TableView<SaleRow> table = new TableView<>();
        table.setStyle("""
            -fx-background-color: #111811;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<SaleRow, String> colTicker = new TableColumn<>("Ticker");
        colTicker.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ticker()));

        TableColumn<SaleRow, String> colDate = new TableColumn<>("Dato");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().sale().getDate().toString()));

        TableColumn<SaleRow, String> colShares = new TableColumn<>("Stk solgt");
        colShares.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.0f", c.getValue().sale().getShares())));

        TableColumn<SaleRow, String> colSalePrice = new TableColumn<>("Salgspris");
        colSalePrice.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.2f kr", c.getValue().sale().getSalePrice())));

        TableColumn<SaleRow, String> colGain = new TableColumn<>("Gevinst");
        colGain.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%+.2f kr", c.getValue().sale().getGain())));
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

        TableColumn<SaleRow, String> colTax = new TableColumn<>("Skat");
        colTax.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.2f kr", c.getValue().sale().getTax())));
        colTax.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item); setTextFill(Color.web("#FB923C"));
            }
        });

        TableColumn<SaleRow, String> colNet = new TableColumn<>("Netto");
        colNet.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%+.2f kr", c.getValue().sale().getNetGain())));
        colNet.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(item.startsWith("+") ? Color.web("#4ADE80") : Color.web("#FF6B6B"));
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            }
        });

        table.getColumns().addAll(colTicker, colDate, colShares, colSalePrice, colGain, colTax, colNet);
        table.setItems(FXCollections.observableArrayList(rows));
        VBox.setVgrow(table, Priority.ALWAYS);

        view.getChildren().addAll(title, table);
        return view;
    }
}

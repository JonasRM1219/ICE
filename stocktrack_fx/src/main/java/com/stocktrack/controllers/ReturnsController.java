package com.stocktrack.controllers;

import com.stocktrack.models.Holding;
import com.stocktrack.models.Portfolio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReturnsController {

    private Portfolio portfolio;

    public ReturnsController(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(28));
        view.setStyle("-fx-background-color: #0A0F0A;");

        Label title = new Label("Samlet afkast");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#E8F5E9"));

        // Samlet totalkort
        VBox totalCard = bigCard(
            "Samlet afkast",
            String.format("%+.2f kr", portfolio.getTotalReturn()),
            portfolio.getTotalReturn() >= 0 ? "#4ADE80" : "#FF6B6B",
            "Inkl. urealiseret gevinst, realiseret salg og udbytte"
        );

        // Breakdown
        HBox breakdown = new HBox(12);
        breakdown.getChildren().addAll(
            miniCard("📈 Urealiseret", String.format("%+.2f kr", portfolio.getTotalGain()),
                portfolio.getTotalGain() >= 0 ? "#4ADE80" : "#FF6B6B"),
            miniCard("💰 Realiseret", String.format("%+.2f kr", portfolio.getTotalSaleGain()),
                portfolio.getTotalSaleGain() >= 0 ? "#4ADE80" : "#FF6B6B"),
            miniCard("🏦 Udbytte", String.format("%.2f kr", portfolio.getTotalDividends()), "#86EFAC"),
            miniCard("🧾 Est. skat", String.format("%.2f kr", portfolio.getTotalTax()), "#FB923C")
        );

        Separator sep = new Separator();

        Label perTitle = new Label("Pr. aktie");
        perTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        perTitle.setTextFill(Color.web("#E8F5E9"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox perHolding = new VBox(10);
        perHolding.setPadding(new Insets(4, 0, 4, 0));

        if (portfolio.isEmpty()) {
            perHolding.getChildren().add(new Label("Ingen aktier endnu."));
        } else {
            for (Holding h : portfolio.getHoldings()) {
                perHolding.getChildren().add(holdingReturnRow(h));
            }
        }
        scroll.setContent(perHolding);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.getChildren().addAll(title, totalCard, breakdown, sep, perTitle, scroll);
        return view;
    }

    private VBox bigCard(String label, String value, String valueColor, String sub) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20));
        card.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 14;
            -fx-border-color: #4ADE80;
            -fx-border-radius: 14;
            -fx-border-width: 1.5;
            """);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 13));
        lbl.setTextFill(Color.web("#86EFAC"));
        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        val.setTextFill(Color.web(valueColor));
        Label subLbl = new Label(sub);
        subLbl.setFont(Font.font("Segoe UI", 11));
        subLbl.setTextFill(Color.web("#86EFAC"));
        card.getChildren().addAll(lbl, val, subLbl);
        return card;
    }

    private VBox miniCard(String label, String value, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14, 16, 14, 16));
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
        val.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        val.setTextFill(Color.web(color));
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private HBox holdingReturnRow(Holding h) {
        HBox row = new HBox(16);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 10;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 10;
            -fx-border-width: 1;
            """);

        Label ticker = new Label(h.getTicker());
        ticker.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        ticker.setTextFill(Color.web("#E8F5E9"));
        ticker.setMinWidth(70);

        Label unreal = miniStat("Urealiseret", String.format("%+.2f kr", h.getUnrealizedGain()),
            h.getUnrealizedGain() >= 0 ? "#4ADE80" : "#FF6B6B");
        Label real   = miniStat("Realiseret", String.format("%+.2f kr", h.getTotalSaleGain()),
            h.getTotalSaleGain() >= 0 ? "#4ADE80" : "#FF6B6B");
        Label div    = miniStat("Udbytte", String.format("%.2f kr", h.getTotalDividends()), "#86EFAC");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        double total = h.getTotalReturn();
        Label totalLbl = new Label(String.format("%+.2f kr", total));
        totalLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        totalLbl.setTextFill(Color.web(total >= 0 ? "#4ADE80" : "#FF6B6B"));

        HBox.setHgrow(unreal, Priority.ALWAYS);
        HBox.setHgrow(real, Priority.ALWAYS);
        HBox.setHgrow(div, Priority.ALWAYS);

        row.getChildren().addAll(ticker, unreal, real, div, spacer, totalLbl);
        return row;
    }

    private Label miniStat(String label, String value, String color) {
        Label lbl = new Label(label + ": " + value);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web(color));
        return lbl;
    }
}

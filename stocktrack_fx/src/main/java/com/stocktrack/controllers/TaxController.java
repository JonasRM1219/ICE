package com.stocktrack.controllers;

import com.stocktrack.models.AccountType;
import com.stocktrack.models.Holding;
import com.stocktrack.models.Portfolio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TaxController {

    private Portfolio portfolio;

    public TaxController(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public VBox getView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(28));
        view.setStyle("-fx-background-color: #0A0F0A;");

        Label title = new Label("Skatteberegning");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#E8F5E9"));

        // Samlet skattekort
        VBox totalCard = new VBox(6);
        totalCard.setPadding(new Insets(20));
        totalCard.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 14;
            -fx-border-color: #FB923C;
            -fx-border-radius: 14;
            -fx-border-width: 1.5;
            """);
        Label totalLbl = new Label("Estimeret skat ved salg af alle aktier");
        totalLbl.setTextFill(Color.web("#86EFAC"));
        totalLbl.setFont(Font.font("Segoe UI", 13));
        Label totalVal = new Label(String.format("%.2f kr", portfolio.getTotalTax()));
        totalVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        totalVal.setTextFill(Color.web("#FB923C"));
        Label totalSub = new Label("Baseret på urealiserede gevinster og din kontotype");
        totalSub.setTextFill(Color.web("#86EFAC"));
        totalSub.setFont(Font.font("Segoe UI", 11));
        totalCard.getChildren().addAll(totalLbl, totalVal, totalSub);

        // Skattesatser info
        HBox rates = new HBox(12);
        rates.getChildren().addAll(
            rateCard("Frie midler", "27% op til 61.000 kr\n42% over grænsen", "#E8F5E9"),
            rateCard("Aktiesparekonto", "17% lagerbeskatning\nMaks. 135.900 kr indskud", "#86EFAC"),
            rateCard("Pension", "15,3% PAL-skat\nÅrlig lagerbeskatning", "#86EFAC")
        );

        Separator sep = new Separator();

        Label perTitle = new Label("Skat pr. aktie");
        perTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        perTitle.setTextFill(Color.web("#E8F5E9"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox perHolding = new VBox(8);

        if (portfolio.isEmpty()) {
            perHolding.getChildren().add(new Label("Ingen aktier endnu."));
        } else {
            for (Holding h : portfolio.getHoldings()) {
                perHolding.getChildren().add(holdingTaxRow(h));
            }
        }
        scroll.setContent(perHolding);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Disclaimer
        Label disclaimer = new Label(
            "⚠️  Beregningerne er vejledende. Progressionsgrænse 2024: 61.000 kr (ugift) / 122.000 kr (gift). " +
            "Rådfør dig med en revisor for præcis skatterådgivning.");
        disclaimer.setWrapText(true);
        disclaimer.setFont(Font.font("Segoe UI", 11));
        disclaimer.setTextFill(Color.web("#86EFAC"));

        view.getChildren().addAll(title, totalCard, rates, sep, perTitle, scroll, disclaimer);
        return view;
    }

    private VBox rateCard(String name, String info, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 12;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 12;
            -fx-border-width: 1;
            """);
        HBox.setHgrow(card, Priority.ALWAYS);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(color));
        Label infoLbl = new Label(info);
        infoLbl.setFont(Font.font("Segoe UI", 11));
        infoLbl.setTextFill(Color.web("#86EFAC"));
        card.getChildren().addAll(nameLbl, infoLbl);
        return card;
    }

    private HBox holdingTaxRow(Holding h) {
        HBox row = new HBox(16);
        row.setPadding(new Insets(12, 16, 12, 16));
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
        ticker.setMinWidth(80);

        Label account = new Label(h.getAccountType().getLabel());
        account.setFont(Font.font("Segoe UI", 12));
        account.setTextFill(Color.web("#86EFAC"));
        account.setMinWidth(140);

        Label gain = new Label(String.format("Gevinst: %+.2f kr", h.getUnrealizedGain()));
        gain.setFont(Font.font("Segoe UI", 12));
        gain.setTextFill(h.getUnrealizedGain() >= 0 ? Color.web("#4ADE80") : Color.web("#FF6B6B"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tax = new Label(String.format("Skat: %.2f kr", h.estimatedTax()));
        tax.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        tax.setTextFill(Color.web("#FB923C"));

        row.getChildren().addAll(ticker, account, gain, spacer, tax);
        return row;
    }
}

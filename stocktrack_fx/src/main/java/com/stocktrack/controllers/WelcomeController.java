package com.stocktrack.controllers;

import com.stocktrack.models.Portfolio;
import com.stocktrack.services.StorageService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class WelcomeController {

    public void show(Stage stage) {
        // ── Layout ────────────────────────────────────────────────────────────
        VBox root = new VBox(32);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #0A0F0A;");

        // Logo / titel
        Label logo = new Label("📈");
        logo.setFont(Font.font(64));

        Label title = new Label("StockTrack");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        title.setTextFill(Color.web("#E8F5E9"));

        Label subtitle = new Label("Dansk aktieportefølje & skatteberegner");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#86EFAC"));

        // Card
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(32));
        card.setMaxWidth(380);
        card.setStyle("""
            -fx-background-color: #111811;
            -fx-background-radius: 16;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 16;
            -fx-border-width: 1;
            """);

        Label cardTitle = new Label("Velkommen tilbage");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        cardTitle.setTextFill(Color.web("#E8F5E9"));

        Label cardSub = new Label("Dine data er gemt lokalt på denne computer");
        cardSub.setFont(Font.font("Segoe UI", 12));
        cardSub.setTextFill(Color.web("#86EFAC"));
        cardSub.setWrapText(true);
        cardSub.setAlignment(Pos.CENTER);

        Button startBtn = new Button("Åbn portefølje  →");
        startBtn.setPrefWidth(280);
        startBtn.setPrefHeight(44);
        startBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        startBtn.setStyle("""
            -fx-background-color: #4ADE80;
            -fx-text-fill: #0A0F0A;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """);
        startBtn.setOnMouseEntered(e -> startBtn.setStyle("""
            -fx-background-color: #22C55E;
            -fx-text-fill: #0A0F0A;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """));
        startBtn.setOnMouseExited(e -> startBtn.setStyle("""
            -fx-background-color: #4ADE80;
            -fx-text-fill: #0A0F0A;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """));

        // Ny bruger knap
        Button newBtn = new Button("Start med tom portefølje");
        newBtn.setPrefWidth(280);
        newBtn.setFont(Font.font("Segoe UI", 13));
        newBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #86EFAC;
            -fx-border-color: #1E3A26;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 8 20 8 20;
            """);

        card.getChildren().addAll(cardTitle, cardSub, new Separator(), startBtn, newBtn);

        // Footer
        Label footer = new Label("Data gemmes automatisk · Ingen internetforbindelse nødvendig");
        footer.setFont(Font.font("Segoe UI", 11));
        footer.setTextFill(Color.web("#4ADE80"));

        root.getChildren().addAll(logo, title, subtitle, card, footer);

        // ── Actions ───────────────────────────────────────────────────────────
        startBtn.setOnAction(e -> {
            Portfolio portfolio = StorageService.loadPortfolio();
            new MainController().show(stage, portfolio);
        });

        newBtn.setOnAction(e -> {
            StorageService.deleteAll();
            Portfolio portfolio = new Portfolio();
            new MainController().show(stage, portfolio);
        });

        // ── Scene ─────────────────────────────────────────────────────────────
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("StockTrack");
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.show();
    }
}

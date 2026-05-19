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

public class MainController {

    private BorderPane root;
    private Portfolio portfolio;
    private Button activeNavBtn;

    public void show(Stage stage, Portfolio portfolio) {
        this.portfolio = portfolio;

        root = new BorderPane();
        root.setStyle("-fx-background-color: #0A0F0A;");

        // ── Sidebar ───────────────────────────────────────────────────────────
        VBox sidebar = buildSidebar(stage);
        root.setLeft(sidebar);

        // ── Start på portefølje-skærm ─────────────────────────────────────────
        showPortfolio();

        // ── Scene ─────────────────────────────────────────────────────────────
        Scene scene = new Scene(root, 1000, 680);
        try {
            scene.getStylesheets().add(
                getClass().getResource("/com/stocktrack/styles/dark.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        stage.setScene(scene);
        stage.setTitle("StockTrack");
        stage.setMinWidth(800);
        stage.setMinHeight(560);
        stage.show();
    }

    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("""
            -fx-background-color: #0D160D;
            -fx-border-color: transparent #1E3A26 transparent transparent;
            -fx-border-width: 1;
            """);

        // App navn
        Label appName = new Label("StockTrack");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        appName.setTextFill(Color.web("#4ADE80"));
        appName.setPadding(new Insets(0, 0, 20, 8));

        Button btnPortfolio = navBtn("📊  Portefølje");
        Button btnReturns   = navBtn("📈  Afkast");
        Button btnHistory   = navBtn("🕓  Historik");
        Button btnTax       = navBtn("🧾  Skat");

        btnPortfolio.setOnAction(e -> { setActive(btnPortfolio); showPortfolio(); });
        btnReturns.setOnAction(e ->   { setActive(btnReturns);   showReturns(); });
        btnHistory.setOnAction(e ->   { setActive(btnHistory);   showHistory(); });
        btnTax.setOnAction(e ->       { setActive(btnTax);       showTax(); });

        setActive(btnPortfolio);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Gem & tilbage
        Button saveBtn = new Button("💾  Gem data");
        saveBtn.setPrefWidth(176);
        saveBtn.setStyle("""
            -fx-background-color: #166534;
            -fx-text-fill: #4ADE80;
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-padding: 10 16 10 16;
            -fx-cursor: hand;
            """);
        saveBtn.setOnAction(e -> {
            StorageService.savePortfolio(portfolio);
            showAlert("Data gemt", "Dine data er gemt.", Alert.AlertType.INFORMATION);
        });

        Button backBtn = new Button("← Forside");
        backBtn.setPrefWidth(176);
        backBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #86EFAC;
            -fx-font-size: 12px;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            """);
        backBtn.setOnAction(e -> {
            StorageService.savePortfolio(portfolio);
            new WelcomeController().show(stage);
        });

        sidebar.getChildren().addAll(
            appName, btnPortfolio, btnReturns, btnHistory, btnTax,
            spacer, saveBtn, backBtn
        );
        return sidebar;
    }

    private Button navBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(176);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #86EFAC;
            -fx-font-size: 14px;
            -fx-background-radius: 8;
            -fx-padding: 10 16 10 16;
            -fx-cursor: hand;
            """);
        btn.setOnMouseEntered(e -> {
            if (btn != activeNavBtn)
                btn.setStyle("""
                    -fx-background-color: #1E3A26;
                    -fx-text-fill: #86EFAC;
                    -fx-font-size: 14px;
                    -fx-background-radius: 8;
                    -fx-padding: 10 16 10 16;
                    -fx-cursor: hand;
                    """);
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeNavBtn)
                btn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #86EFAC;
                    -fx-font-size: 14px;
                    -fx-background-radius: 8;
                    -fx-padding: 10 16 10 16;
                    -fx-cursor: hand;
                    """);
        });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #86EFAC;
                -fx-font-size: 14px;
                -fx-background-radius: 8;
                -fx-padding: 10 16 10 16;
                -fx-cursor: hand;
                """);
        }
        activeNavBtn = btn;
        btn.setStyle("""
            -fx-background-color: #166534;
            -fx-text-fill: #4ADE80;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-background-radius: 8;
            -fx-padding: 10 16 10 16;
            -fx-cursor: hand;
            """);
    }

    private void showPortfolio() {
        root.setCenter(new PortfolioController(portfolio, this).getView());
    }

    private void showReturns() {
        root.setCenter(new ReturnsController(portfolio).getView());
    }

    private void showHistory() {
        root.setCenter(new HistoryController(portfolio).getView());
    }

    private void showTax() {
        root.setCenter(new TaxController(portfolio).getView());
    }

    public void refresh() {
        showPortfolio();
    }

    public Portfolio getPortfolio() { return portfolio; }

    public static void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

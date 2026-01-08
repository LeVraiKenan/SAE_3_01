package tribollojfx.controller;

import tribollojfx.model.*;
import tribollojfx.view.components.*;
import tribollojfx.observer.observers.MainObserver;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MainController {
    private Stage primaryStage;
    private TaskModel model;
    private BorderPane root;
    private Map<Statut, ColumnView> columns;
    private MainObserver mainObserver;
    private TaskTableView listView;
    private ColumnContainerView containerView;
    private NotificationView notificationView;
    private Timeline notificationChecker;
    private ColonneController colonneController;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.model = new TaskModel();
        this.columns = new HashMap<>();
        this.colonneController = new ColonneController(model);
    }

    public void start() {
        try {
            initializeUI();
            setupObservers();
            setupNotificationChecker();
            setupStage();
        } catch (Exception e) {
            System.err.println("Erreur dans MainController.start(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        root = new BorderPane();

        for (Statut statut : Statut.values()) {
            if (statut != Statut.BLOQUEE) {
                ColumnView column = new ColumnView(statut, model);
                columns.put(statut, column);
            }
        }

        listView = new TaskTableView();
        listView.updateTasks(model.getTaches());

        containerView = new ColumnContainerView(columns, model, colonneController);
        root.setCenter(containerView.getView());

        notificationView = new NotificationView(model);

        HBox topBar = createTopBar();
        root.setTop(topBar);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e5e7eb; " +
                "-fx-border-width: 0 0 1 0;");

        Button btnTableau = createStyledButton("📋 Tableau", "#3b82f6");
        Button btnListe = createStyledButton("📝 Liste", "#8b5cf6");
        Button btnGantt = createStyledButton("📊 Gantt", "#10b981");

        Button btnAjoutColonne = new Button("+ Colonne");
        btnAjoutColonne.setStyle("-fx-background-color: #8b5cf6; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: 600; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 20; " +
                "-fx-border-radius: 8;");
        btnAjoutColonne.setOnAction(e -> ajouterColonne());

        Button btnNotifications = notificationView.getNotificationButton();
        btnNotifications.setTooltip(new javafx.scene.control.Tooltip("Notifications"));
        btnNotifications.setOnAction(e -> showNotificationPanel());

        btnGantt.setOnAction(e -> {
            GanttController gc = new GanttController(model);
            gc.showGanttView();
        });

        btnTableau.setOnAction(e -> showTableView());
        btnListe.setOnAction(e -> showListView());

        topBar.getChildren().addAll(btnTableau, btnListe, btnGantt,
                btnAjoutColonne, new javafx.scene.layout.Region(),
                btnNotifications);
        HBox.setHgrow(topBar.getChildren().get(topBar.getChildren().size() - 2), Priority.ALWAYS);

        return topBar;
    }

    private void ajouterColonne() {
        if (!colonneController.peutAjouterColonne()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Limite atteinte");
            alert.setHeaderText("Nombre maximum de colonnes atteint");
            alert.setContentText("Vous ne pouvez pas créer plus de 2 colonnes personnalisées.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle Colonne");
        dialog.setHeaderText("Ajouter une colonne personnalisée");
        dialog.setContentText("Nom :");

        dialog.showAndWait().ifPresent(nom -> {
            if (!nom.trim().isEmpty()) {
                colonneController.creerColonne(nom.trim());
                refreshInterface();
            }
        });
    }

    private void refreshInterface() {
        try {
            containerView = new ColumnContainerView(columns, model, colonneController);

            root.setCenter(containerView.getView());

            if (mainObserver != null) {
                model.removeObserver(mainObserver);
            }
            mainObserver = new MainObserver(model, columns, containerView);

            model.notifyObservers();

        } catch (Exception e) {
            System.err.println("Erreur refreshFullInterface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: 600; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10 20; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + darkenColor(color) + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10 20; " +
                    "-fx-border-radius: 8; " +
                    "-fx-cursor: hand;");
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: " + color + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10 20; " +
                    "-fx-border-radius: 8; " +
                    "-fx-cursor: hand;");
        });

        return btn;
    }

    private String darkenColor(String hexColor) {
        return hexColor.replaceFirst("#", "#55");
    }

    private void showNotificationPanel() {
        if (notificationView == null) return;

        Stage notificationStage = new Stage();
        notificationStage.setTitle("Notifications");
        notificationStage.initOwner(primaryStage);

        ScrollPane scrollPane = new ScrollPane(notificationView.getView());
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 500, 600);
        notificationStage.setScene(scene);
        notificationStage.show();

        notificationView.refresh();
    }

    public void showTableView() {
        try {
            root.setCenter(containerView.getView());
        } catch (Exception e) {
            System.err.println("Erreur showTableView: " + e.getMessage());
        }
    }

    public void showListView() {
        try {
            listView.updateTasks(model.getTaches());
            ScrollPane scrollPane = new ScrollPane(listView.getView());
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            root.setCenter(scrollPane);
        } catch (Exception e) {
            System.err.println("Erreur showListView: " + e.getMessage());
        }
    }

    private void setupObservers() {
        try {
            mainObserver = new MainObserver(model, columns, containerView);
        } catch (Exception e) {
            System.err.println("Erreur setupObservers: " + e.getMessage());
        }
    }

    private void setupNotificationChecker() {
        notificationChecker = new Timeline(
                new KeyFrame(Duration.minutes(1), e -> {
                    model.getNotificationManager().verifierNotifications();
                    notificationView.refresh();
                })
        );
        notificationChecker.setCycleCount(Animation.INDEFINITE);
        notificationChecker.play();
    }

    private void setupStage() {
        try {
            Scene scene = new Scene(root, 1400, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Tribollo - Gestion de Tâches");
            primaryStage.show();

            notificationView.refresh();
        } catch (Exception e) {
            System.err.println("Erreur setupStage: " + e.getMessage());
        }
    }

    public void stop() {
        if (notificationChecker != null) {
            notificationChecker.stop();
        }
    }
}
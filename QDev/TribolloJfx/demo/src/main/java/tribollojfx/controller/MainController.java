package tribollojfx.controller;

import tribollojfx.model.*;
import tribollojfx.view.components.*;
import tribollojfx.observer.observers.MainObserver;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.model = new TaskModel();
        this.columns = new HashMap<>();
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

        containerView = new ColumnContainerView(columns);
        root.setCenter(containerView.getView());

        try {
            notificationView = new NotificationView(model);
        } catch (Exception e) {
            System.err.println("Erreur création NotificationView: " + e.getMessage());
            notificationView = null;
        }

        ButtonBarView buttonBar = new ButtonBarView(model, root, columns, this, notificationView);
        root.setTop(buttonBar.getView());

        if (notificationView != null && notificationView.getNotificationButton() != null) {
            notificationView.getNotificationButton().setOnAction(e -> {
                showNotificationPanel();
            });
        }
    }


    private void setupNotificationChecker() {
        notificationChecker = new Timeline(
                new KeyFrame(Duration.minutes(1), e -> {
                    model.getNotificationManager().verifierNotifications();
                    notificationView.refresh();

                    checkUrgentNotifications();
                })
        );
        notificationChecker.setCycleCount(Animation.INDEFINITE);
        notificationChecker.play();
    }

    private void checkUrgentNotifications() {
        long urgentesNonLues = model.getNotificationManager().getNotificationsNonLues().stream()
                .filter(notif -> notif.getType() == Notification.Type.RETARD_DEBUT ||
                        notif.getType() == Notification.Type.RETARD_FIN)
                .count();

        if (urgentesNonLues > 0 && !primaryStage.isFocused()) {
            System.out.println(urgentesNonLues + " notification(s) urgente(s) !");
        }
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
            mainObserver = new MainObserver(model, columns);
        } catch (Exception e) {
            System.err.println("Erreur setupObservers: " + e.getMessage());
        }
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
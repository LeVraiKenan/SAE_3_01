package tribollojfx.view.components;

import tribollojfx.model.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import java.util.List;

public class NotificationView {
    private VBox view;
    private TaskModel model;
    private Label notificationBadge;
    private Button notificationButton;
    private VBox notificationListContainer;

    public NotificationView(TaskModel model) {
        this.model = model;
        createView();
    }

    private void createView() {
        view = new VBox(10);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Notifications");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Button markAllRead = new Button("Tout marquer comme lu");
        markAllRead.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 12px;");
        markAllRead.setOnAction(e -> {
            model.getNotificationManager().marquerToutesCommeLues();
            updateNotificationList();
            updateBadge();
        });

        Button clearAll = new Button("Tout effacer");
        clearAll.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280; -fx-font-size: 12px;");
        clearAll.setOnAction(e -> {
            model.getNotificationManager().supprimerToutesNotifications();
            updateNotificationList();
            updateBadge();
        });

        header.getChildren().addAll(title, new Region(), markAllRead, clearAll);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        notificationListContainer = new VBox(8);
        notificationListContainer.setPadding(new Insets(10));

        updateNotificationList();

        scrollPane.setContent(notificationListContainer);

        view.getChildren().addAll(header, scrollPane);

        notificationBadge = new Label();
        notificationBadge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                "-fx-font-size: 10px; -fx-font-weight: bold; " +
                "-fx-padding: 2 5; -fx-background-radius: 10;");
        notificationBadge.setVisible(false);

        notificationButton = new Button("🔔");
        notificationButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px;");
        notificationButton.setGraphic(notificationBadge);
        updateBadge();
    }

    private void updateNotificationList() {
        notificationListContainer.getChildren().clear();

        List<Notification> notifications = model.getNotificationManager().getToutesNotifications();

        if (notifications == null || notifications.isEmpty()) {
            Label noNotifications = new Label("Aucune notification");
            noNotifications.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-font-style: italic;");
            notificationListContainer.getChildren().add(noNotifications);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);

            for (Notification notification : notifications) {
                if (notification != null) {
                    VBox notificationCard = createNotificationCard(notification, formatter);
                    notificationListContainer.getChildren().add(notificationCard);
                }
            }
        }
    }

    private VBox createNotificationCard(Notification notification, DateTimeFormatter formatter) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));

        String color = "#f3f4f6";
        String textColor = "#374151";

        if (notification.getType() != null) {
            color = getNotificationColor(notification);
            textColor = getNotificationTextColor(notification);
        }

        card.setStyle("-fx-background-color: " + color + "; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 8;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        String typeText = notification.getType() != null ?
                notification.getType().getLibelle() : "Notification";

        Label typeLabel = new Label(typeText);
        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: " + textColor + ";");

        Label dateLabel = new Label(notification.getDateCreation() != null ?
                notification.getDateCreation().format(formatter) : "");
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b7280;");

        header.getChildren().addAll(typeLabel, new Region(), dateLabel);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        Label messageLabel = new Label(notification.getMessage() != null ?
                notification.getMessage() : "");
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");
        messageLabel.setWrapText(true);

        HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        if (!notification.isLue()) {
            Button markRead = new Button("Marquer comme lu");
            markRead.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 11px;");
            markRead.setOnAction(e -> {
                notification.marquerCommeLue();
                updateNotificationList();
                updateBadge();
            });
            buttons.getChildren().add(markRead);
        }

        Button delete = new Button("×");
        delete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 12px;");
        delete.setOnAction(e -> {
            model.getNotificationManager().supprimerNotification(notification);
            updateNotificationList();
            updateBadge();
        });

        buttons.getChildren().addAll(delete);

        card.getChildren().addAll(header, messageLabel, buttons);
        return card;
    }

    private String getNotificationColor(Notification notification) {
        if (notification.getType() == null) return "#f3f4f6";

        return switch (notification.getType()) {
            case RETARD_DEBUT, RETARD_FIN -> "#fee2e2";
            case DEBUT_TACHE, FIN_APPROCHE -> "#dbeafe";
            case DEPENDANCE_BLOQUANTE -> "#fef3c7";
        };
    }

    private String getNotificationTextColor(Notification notification) {
        if (notification.getType() == null) return "#374151";

        return switch (notification.getType()) {
            case RETARD_DEBUT, RETARD_FIN -> "#dc2626";
            case DEBUT_TACHE, FIN_APPROCHE -> "#1d4ed8";
            case DEPENDANCE_BLOQUANTE -> "#92400e";
        };
    }

    private void updateBadge() {
        if (model.getNotificationManager() == null) return;

        int nonLues = model.getNotificationManager().getNombreNotificationsNonLues();
        if (nonLues > 0) {
            notificationBadge.setText(String.valueOf(nonLues));
            notificationBadge.setVisible(true);
            notificationButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: #3b82f6;");
        } else {
            notificationBadge.setVisible(false);
            notificationButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: #6b7280;");
        }
    }

    public VBox getView() {
        return view;
    }

    public Button getNotificationButton() {
        return notificationButton;
    }

    public void refresh() {
        if (model.getNotificationManager() != null) {
            model.getNotificationManager().verifierNotifications();
            updateNotificationList();
            updateBadge();
        }
    }
}
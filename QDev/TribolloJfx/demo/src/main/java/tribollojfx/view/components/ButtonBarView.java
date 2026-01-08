package tribollojfx.view.components;

import tribollojfx.model.TaskModel;
import tribollojfx.model.Statut;
import tribollojfx.controller.GanttController;
import tribollojfx.controller.MainController;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Map;

public class ButtonBarView {
    private HBox view;
    private TaskModel model;
    private MainController mainController;
    private NotificationView notificationView;

    public ButtonBarView(TaskModel model, BorderPane root,
                         Map<Statut, ColumnView> columns, MainController mainController,
                         NotificationView notificationView) {
        this.model = model;
        this.mainController = mainController;
        this.notificationView = notificationView;
        createView();
    }

    private void createView() {
        view = new HBox(15);
        view.setPadding(new Insets(15, 25, 15, 25));
        view.setAlignment(Pos.CENTER_LEFT);
        view.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e5e7eb; " +
                "-fx-border-width: 0 0 1 0;");

        Button btnTableau = createStyledButton("📋 Tableau", "#3b82f6");
        Button btnListe = createStyledButton("📝 Liste", "#8b5cf6");
        Button btnGantt = createStyledButton("📊 Gantt", "#10b981");

        Button btnNotifications = notificationView.getNotificationButton();
        btnNotifications.setTooltip(new javafx.scene.control.Tooltip("Notifications"));

        btnGantt.setOnAction(e -> {
            GanttController gc = new GanttController(model);
            gc.showGanttView();
        });

        btnTableau.setOnAction(e -> mainController.showTableView());
        btnListe.setOnAction(e -> mainController.showListView());

        view.getChildren().addAll(btnTableau, btnListe, btnGantt, new javafx.scene.layout.Region(), btnNotifications);
        HBox.setHgrow(view.getChildren().get(view.getChildren().size() - 2), javafx.scene.layout.Priority.ALWAYS);
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

    public HBox getView() {
        return view;
    }
}
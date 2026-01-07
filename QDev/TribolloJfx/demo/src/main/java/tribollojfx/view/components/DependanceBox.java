package tribollojfx.view.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import tribollojfx.model.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.Comparator;

public class DependanceBox {
    private VBox container;
    private Task parentTask;
    private TaskModel model;

    public DependanceBox(Task parentTask, TaskModel model) {
        this.parentTask = parentTask;
        this.model = model;
        createBox();
    }

    private void createBox() {
        container = new VBox(5);
        container.setPadding(new Insets(5, 0, 0, 20));
        updateDependances();
    }

    public void updateDependances() {
        container.getChildren().clear();

        if (parentTask.getDependances().isEmpty()) {
            Label noDepsLabel = new Label("Aucune dépendance");
            noDepsLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
            container.getChildren().add(noDepsLabel);
            return;
        }

        ArrayList<Task> dependancesTriees = new ArrayList<>(parentTask.getDependances());
        dependancesTriees.sort(Comparator.comparing((Task t) -> {
            if (t.getStatut() == Statut.TERMINEE) return 2;
            if (t.getStatut() == Statut.EN_COURS) return 1;
            return 0;
        }).thenComparing(Task::getTitre));

        for (Task dep : dependancesTriees) {
            HBox depBox = new HBox(8);
            depBox.setAlignment(Pos.CENTER_LEFT);
            depBox.setPadding(new Insets(3));
            depBox.setStyle("-fx-background-color: #f9fafb; -fx-border-radius: 4; -fx-padding: 4;");

            CheckBox check = new CheckBox();
            check.setSelected(dep.getStatut() == Statut.TERMINEE);
            check.setOnAction(e -> {
                Statut nouveauStatut = check.isSelected() ? Statut.TERMINEE : Statut.A_FAIRE;
                model.updateDependanceStatut(dep, nouveauStatut);
            });

            Circle statutDot = new Circle(4);
            switch (dep.getStatut()) {
                case A_FAIRE -> statutDot.setFill(Color.web("#9ca3af"));
                case EN_COURS -> statutDot.setFill(Color.web("#3b82f6"));
                case TERMINEE -> statutDot.setFill(Color.web("#10b981"));
                case ARCHIVEE -> statutDot.setFill(Color.web("#6b7280"));
                case BLOQUEE -> statutDot.setFill(Color.web("#ef4444"));
            }

            Label depLabel = new Label(dep.getTitre());
            depLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");

            Button removeBtn = new Button("×");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 10px;");
            removeBtn.setTooltip(new Tooltip("Retirer la dépendance"));
            removeBtn.setOnAction(e -> model.retirerDependance(parentTask, dep));

            depBox.getChildren().addAll(check, statutDot, depLabel, removeBtn);
            container.getChildren().add(depBox);
        }
    }

    public VBox getView() {
        return container;
    }
}
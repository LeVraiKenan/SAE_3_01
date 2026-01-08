package tribollojfx.view.components;

import tribollojfx.model.*;
import tribollojfx.observer.observers.MainObserver;
import tribollojfx.view.dialogs.TaskDialog;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class ColumnView {
    private VBox column;
    private VBox content;
    private Statut statut;
    private TaskModel model;
    private Label titleLabel;

    public ColumnView(Statut statut, TaskModel model) {
        this.statut = statut;
        this.model = model;
        createColumn();
        setupDropZone();
    }

    private void createColumn() {
        column = new VBox(10);
        column.setPadding(new Insets(15));
        column.setPrefWidth(280);
        column.setMinHeight(600);

        String columnColor = switch (statut) {
            case A_FAIRE -> "#f3f4f6";
            case EN_COURS -> "#dbeafe";
            case TERMINEE -> "#d1fae5";
            case ARCHIVEE -> "#f5f5f5";
            case BLOQUEE -> "#fee2e2";
        };

        column.setStyle("-fx-background-color: " + columnColor + "; " +
                "-fx-border-color: #e5e7eb; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1;");

        titleLabel = new Label(getColumnName(statut));
        titleLabel.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: 700; " +
                "-fx-text-fill: #374151; " +
                "-fx-padding: 0 0 10 0;");

        content = new VBox(8);
        content.setId("contenu-" + statut.name());
        content.setPadding(new Insets(10, 0, 0, 0));

        Button addButton = new Button("+ Ajouter une tâche");
        addButton.setStyle("-fx-background-color: #3b82f6; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 8 16; " +
                "-fx-border-radius: 8;");
        addButton.setOnAction(e -> {
            TaskDialog dialog = new TaskDialog(model, statut);
            dialog.showAndWait();
        });

        addButton.setOnMouseEntered(e -> {
            addButton.setStyle("-fx-background-color: #2563eb; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 8 16; " +
                    "-fx-border-radius: 8;");
        });

        addButton.setOnMouseExited(e -> {
            addButton.setStyle("-fx-background-color: #3b82f6; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 8 16; " +
                    "-fx-border-radius: 8;");
        });

        column.getChildren().addAll(titleLabel, content, addButton);
    }

    private void setupDropZone() {
        content.setOnDragOver(event -> {
            if (event.getGestureSource() != content &&
                    event.getDragboard().hasString() &&
                    event.getDragboard().getString().equals("task")) {

                Task draggedTask = MainObserver.getDraggedTask();

                if (statut == Statut.TERMINEE && draggedTask != null) {
                    boolean toutesDependancesTerminees = true;
                    for (Task dep : draggedTask.getDependances()) {
                        if (dep.getStatut() != Statut.TERMINEE) {
                            toutesDependancesTerminees = false;
                            break;
                        }
                    }

                    if (!toutesDependancesTerminees) {
                        content.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); " +
                                "-fx-border-color: #ef4444; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 8;");
                        event.consume();
                        return;
                    }
                }

                event.acceptTransferModes(TransferMode.MOVE);
                content.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); " +
                        "-fx-border-color: #3b82f6; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8;");
            }
            event.consume();
        });

        content.setOnDragExited(event -> {
            content.setStyle("");
            event.consume();
        });

        content.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && db.getString().equals("task")) {
                Task draggedTask = MainObserver.getDraggedTask();

                if (draggedTask != null && draggedTask.getStatut() != statut) {
                    if (statut == Statut.TERMINEE) {
                        // Vérifier les dépendances...
                    }

                    // MODIFICATION IMPORTANTE : Mettre colonnePersoId à 0
                    draggedTask.setColonnePersoId(0);  // ← Ajoutez cette ligne

                    model.updateTaskStatut(draggedTask, statut);
                    success = true;
                    MainObserver.setDraggedTask(null);
                }
            }

            content.setStyle("");
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private String getColumnName(Statut statut) {
        return switch (statut) {
            case A_FAIRE -> "À FAIRE";
            case EN_COURS -> "EN COURS";
            case TERMINEE -> "TERMINÉE";
            case ARCHIVEE -> "ARCHIVÉE";
            case BLOQUEE -> "BLOQUÉE";
            default -> statut.name();
        };
    }

    public VBox getView() {
        return column;
    }

    public VBox getContent() {
        return content;
    }

    public Statut getStatut() {
        return statut;
    }

    public void updateTitle(int count) {
        titleLabel.setText(getColumnName(statut) + " (" + count + ")");

        if (count > 5) {
            titleLabel.setTextFill(Color.web("#dc2626"));
        } else if (count > 2) {
            titleLabel.setTextFill(Color.web("#f59e0b"));
        } else {
            titleLabel.setTextFill(Color.web("#374151"));
        }
    }
}
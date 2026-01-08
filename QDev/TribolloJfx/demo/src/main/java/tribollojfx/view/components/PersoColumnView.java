package tribollojfx.view.components;

import javafx.scene.layout.HBox;
import tribollojfx.model.*;
import tribollojfx.controller.ColonneController;
import tribollojfx.observer.observers.MainObserver;
import tribollojfx.view.dialogs.TaskDialog;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class PersoColumnView {
    private VBox column;
    private VBox content;
    private int colonneId;
    private String nomColonne;
    private TaskModel model;
    private ColonneController colonneController;
    private Label titleLabel;
    private Button deleteButton;

    public PersoColumnView(int colonneId, String nomColonne, TaskModel model, ColonneController colonneController) {
        this.colonneId = colonneId;
        this.nomColonne = nomColonne;
        this.model = model;
        this.colonneController = colonneController;
        createColumn();
        setupDropZone();
    }

    private void createColumn() {
        column = new VBox(10);
        column.setPadding(new Insets(15));
        column.setPrefWidth(280);
        column.setMinHeight(600);

        column.setStyle("-fx-background-color: #e3f2fd; " +
                "-fx-border-color: #90caf9; " +
                "-fx-border-radius: 12; " +
                "-fx-border-width: 1;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label(nomColonne);
        titleLabel.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: 700; " +
                "-fx-text-fill: #1565c0;");

        deleteButton = new Button("×");
        deleteButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #d32f2f; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px;");
        deleteButton.setTooltip(new javafx.scene.control.Tooltip("Supprimer cette colonne"));
        deleteButton.setOnAction(e -> supprimerColonne());

        header.getChildren().addAll(titleLabel, deleteButton);
        javafx.scene.layout.HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);

        content = new VBox(8);
        content.setId("contenu-perso-" + colonneId);
        content.setPadding(new Insets(10, 0, 0, 0));

        Button addButton = new Button("+ Ajouter une tâche");
        addButton.setStyle("-fx-background-color: #2196f3; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 8 16; " +
                "-fx-border-radius: 8;");
        addButton.setOnAction(e -> {
            TaskDialog dialog = new TaskDialog(model, Statut.A_FAIRE, colonneId);
            dialog.showAndWait();
        });

        addButton.setOnMouseEntered(e -> {
            addButton.setStyle("-fx-background-color: #1976d2; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 8 16; " +
                    "-fx-border-radius: 8;");
        });

        addButton.setOnMouseExited(e -> {
            addButton.setStyle("-fx-background-color: #2196f3; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 8 16; " +
                    "-fx-border-radius: 8;");
        });

        column.getChildren().addAll(header, content, addButton);
    }

    private void supprimerColonne() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer la colonne");
        alert.setHeaderText("Supprimer la colonne \"" + nomColonne + "\"");
        alert.setContentText("Voulez-vous vraiment supprimer cette colonne ? " +
                "Les tâches seront déplacées vers 'À FAIRE'.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                model.supprimerColonnePersonnalisee(colonneId);
            }
        });
    }

    private void setupDropZone() {
        content.setOnDragOver(event -> {
            if (event.getGestureSource() != content &&
                    event.getDragboard().hasString() &&
                    event.getDragboard().getString().equals("task")) {

                event.acceptTransferModes(TransferMode.MOVE);
                content.setStyle("-fx-background-color: rgba(33, 150, 243, 0.1); " +
                        "-fx-border-color: #2196f3; " +
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

                if (draggedTask != null) {
                    model.deplacerVersColonnePerso(draggedTask, colonneId);
                    success = true;
                    MainObserver.setDraggedTask(null);
                }
            }

            content.setStyle("");
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void updateTitle(int count) {
        titleLabel.setText(nomColonne + " (" + count + ")");

        if (count > 5) {
            titleLabel.setTextFill(Color.web("#d32f2f"));
        } else if (count > 2) {
            titleLabel.setTextFill(Color.web("#f57c00"));
        } else {
            titleLabel.setTextFill(Color.web("#1565c0"));
        }
    }

    public VBox getView() {
        return column;
    }

    public VBox getContent() {
        return content;
    }

    public int getColonneId() {
        return colonneId;
    }
}
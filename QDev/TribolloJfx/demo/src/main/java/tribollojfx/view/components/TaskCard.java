package tribollojfx.view.components;

import tribollojfx.model.*;
import tribollojfx.controller.ArchiveController;
import tribollojfx.observer.observers.MainObserver;
import tribollojfx.view.dialogs.EditDialog;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCard {
    private HBox card;
    private Task task;
    private TaskModel model;
    private ArchiveController archiveController;

    public TaskCard(Task task, TaskModel model) {
        this.task = task;
        this.model = model;
        this.archiveController = new ArchiveController(model);
        createCard();
    }

    private void createCard() {
        card = new HBox(10);
        card.setPadding(new Insets(8));
        card.setAlignment(Pos.CENTER_LEFT);

        String baseStyle = "-fx-background-color: #ffffff; " +
                "-fx-border-color: #d1d5db; " +
                "-fx-border-radius: 8; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);";

        if (estEnRetard()) {
            card.setStyle(baseStyle + " -fx-background-color: #fef2f2; -fx-border-color: #f87171;");
        } else {
            card.setStyle(baseStyle);
        }

        Rectangle priorityIndicator = createPriorityIndicator();
        VBox infoBox = createInfoBox();
        HBox actionBox = createActionBox();

        card.getChildren().addAll(priorityIndicator, infoBox, actionBox);

        setupEventHandlers();
        setupDragAndDrop();
    }

    private Rectangle createPriorityIndicator() {
        Rectangle rect = new Rectangle(6, 60);
        rect.setArcHeight(3);
        rect.setArcWidth(3);

        Color color = switch (task.getPriorite()) {
            case BASSE -> Color.web("#10b981");
            case NORMALE -> Color.web("#3b82f6");
            case HAUTE -> Color.web("#f59e0b");
            case URGENTE -> Color.web("#ef4444");
            default -> Color.GRAY;
        };
        rect.setFill(color);
        rect.setEffect(new javafx.scene.effect.DropShadow(2, Color.color(0, 0, 0, 0.1)));
        return rect;
    }

    private VBox createInfoBox() {
        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(180);

        HBox titleBox = new HBox(5);
        Label titleLabel = new Label(task.getTitre());
        titleLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-text-fill: #1f2937;");
        titleLabel.setWrapText(true);

        if (estEnRetard()) {
            Label retardIcon = new Label("⚠");
            retardIcon.setTextFill(Color.web("#dc2626"));
            retardIcon.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            retardIcon.setTooltip(new Tooltip("Tâche en retard"));
            titleBox.getChildren().addAll(retardIcon, titleLabel);
        } else {
            titleBox.getChildren().add(titleLabel);
        }

        if (!task.getDependances().isEmpty()) {
            int terminees = (int) task.getDependances().stream()
                    .filter(d -> d.getStatut() == Statut.TERMINEE)
                    .count();

            HBox progressBox = new HBox(5);
            progressBox.setAlignment(Pos.CENTER_LEFT);

            double progress = (double) terminees / task.getDependances().size();
            Rectangle bgBar = new Rectangle(100, 6);
            bgBar.setFill(Color.web("#e5e7eb"));
            bgBar.setArcHeight(3);
            bgBar.setArcWidth(3);

            Rectangle progressBar = new Rectangle(100 * progress, 6);
            progressBar.setFill(progress == 1.0 ? Color.web("#10b981") : Color.web("#3b82f6"));
            progressBar.setArcHeight(3);
            progressBar.setArcWidth(3);

            StackPane progressStack = new StackPane(bgBar, progressBar);

            Label progressLabel = new Label(terminees + "/" + task.getDependances().size());
            progressLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");

            progressBox.getChildren().addAll(progressStack, progressLabel);
            infoBox.getChildren().add(progressBox);
        }

        if (task.getDateDebut() != null && task.getDateFin() != null) {
            Label dateLabel = new Label(
                    task.getDateDebut().toLocalDate() + " → " +
                            task.getDateFin().toLocalDate()
            );
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
            infoBox.getChildren().add(dateLabel);
        }

        infoBox.getChildren().add(0, titleBox);
        return infoBox;
    }

    private HBox createActionBox() {
        HBox actionBox = new HBox(5);

        Button archiveBtn = new Button();
        archiveBtn.getStyleClass().add("icon-button");

        if (task.getStatut() == Statut.ARCHIVEE) {
            archiveBtn.setText("↻");
            archiveBtn.setTooltip(new Tooltip("Restaurer"));
            archiveBtn.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280;");
        } else {
            archiveBtn.setText("📁");
            archiveBtn.setTooltip(new Tooltip("Archiver"));
            archiveBtn.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280;");
        }

        archiveBtn.setOnAction(e -> toggleArchive());

        Button dependanceBtn = new Button("🔗");
        dependanceBtn.setTooltip(new Tooltip("Ajouter dépendance"));
        dependanceBtn.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white;");
        dependanceBtn.setOnAction(e -> ajouterDependance());

        Button deleteBtn = new Button("×");
        deleteBtn.setTooltip(new Tooltip("Supprimer"));
        deleteBtn.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> deleteTask());

        actionBox.getChildren().addAll(archiveBtn, dependanceBtn, deleteBtn);
        return actionBox;
    }

    private void setupEventHandlers() {
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                EditDialog dialog = new EditDialog(task, model);
                dialog.showAndWait();
            }
            else if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 1) {
                ouvrirDetailTask();
            }
        });

        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + " -fx-background-color: #f9fafb;");
        });

        card.setOnMouseExited(e -> {
            if (estEnRetard()) {
                card.setStyle("-fx-background-color: #fef2f2; -fx-border-color: #f87171; -fx-border-radius: 8;");
            } else {
                card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d1d5db; -fx-border-radius: 8;");
            }
        });
    }

    private void setupDragAndDrop() {
        card.setOnDragDetected(event -> {
            if (task.getStatut() != Statut.ARCHIVEE) {
                MainObserver.setDraggedTask(task);

                Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("task");
                db.setContent(content);

                card.setOpacity(0.7);
                event.consume();
            }
        });

        card.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                card.setOpacity(1.0);
            }
            event.consume();
        });
    }

    private void ajouterDependance() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une dépendance");
        dialog.setHeaderText("Choisir une tâche à ajouter comme dépendance à: " + task.getTitre());

        javafx.scene.control.ListView<Task> taskListView = new javafx.scene.control.ListView<>();
        taskListView.setItems(FXCollections.observableArrayList(
                model.getTaches().stream()
                        .filter(t -> t != task && !task.getDependances().contains(t))
                        .toList()
        ));

        taskListView.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitre() + " (" + item.getStatut() + ")");
                }
            }
        });

        dialog.getDialogPane().setContent(taskListView);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                return taskListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedTask -> {
            if (selectedTask != null) {
                model.ajouterDependance(task, selectedTask);
            }
        });
    }

    private void updateCardAppearance() {
        if (task.getStatut() != Statut.TERMINEE && !task.getDependances().isEmpty()) {
            long dependancesNonTerminees = task.getDependances().stream()
                    .filter(d -> d.getStatut() != Statut.TERMINEE)
                    .count();

            if (dependancesNonTerminees > 0) {
                Tooltip tooltip = new Tooltip(
                        dependancesNonTerminees + " dépendance(s) non terminée(s)\n" +
                                "Terminez-les avant de pouvoir marquer cette tâche comme terminée."
                );
                Tooltip.install(card, tooltip);
            }
        }
    }

    private void ouvrirDetailTask() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de la tâche : " + task.getTitre());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(450);

        Label titre = new Label(task.getTitre());
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox statutBox = new HBox(10);
        Label labelStatut = new Label("Statut:");
        labelStatut.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label statutValue = new Label(task.getStatut().toString());
        statutValue.setStyle(getStatutStyle(task.getStatut()));

        statutBox.getChildren().addAll(labelStatut, statutValue);

        HBox prioriteBox = new HBox(10);
        Label labelPriorite = new Label("Priorité:");
        labelPriorite.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label prioriteValue = new Label(task.getPriorite().toString());
        prioriteValue.setStyle(getPrioriteStyle(task.getPriorite()));

        prioriteBox.getChildren().addAll(labelPriorite, prioriteValue);

        VBox datesBox = new VBox(5);
        Label labelDates = new Label("Dates:");
        labelDates.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        if (task.getDateDebut() != null && task.getDateFin() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String datesText = task.getDateDebut().toLocalDate().format(formatter) +
                    " → " + task.getDateFin().toLocalDate().format(formatter);

            Label datesValue = new Label(datesText);
            datesValue.setStyle("-fx-font-size: 13px;");

            if (estEnRetard()) {
                Label retardLabel = new Label("⚠ EN RETARD");
                retardLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                datesBox.getChildren().addAll(labelDates, datesValue, retardLabel);
            } else {
                datesBox.getChildren().addAll(labelDates, datesValue);
            }
        } else {
            Label noDates = new Label("Aucune date définie");
            noDates.setStyle("-fx-font-style: italic; -fx-text-fill: #6b7280;");
            datesBox.getChildren().addAll(labelDates, noDates);
        }

        VBox descBox = new VBox(5);
        Label labelDesc = new Label("Description:");
        labelDesc.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextArea descText = new TextArea(task.getDescription() == null || task.getDescription().isEmpty()
                ? "Aucune description fournie." : task.getDescription());
        descText.setEditable(false);
        descText.setWrapText(true);
        descText.setPrefRowCount(4);
        descText.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb;");

        descBox.getChildren().addAll(labelDesc, descText);

        VBox dependancesBox = new VBox(5);
        Label labelDep = new Label("Dépendances (" + task.getDependances().size() + "):");
        labelDep.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        if (task.getDependances().isEmpty()) {
            Label aucuneLabel = new Label("Aucune dépendance.");
            aucuneLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6b7280;");
            dependancesBox.getChildren().addAll(labelDep, aucuneLabel);
        } else {
            VBox listeDeps = new VBox(3);
            for (Task dep : task.getDependances()) {
                HBox depItem = new HBox(8);
                depItem.setAlignment(Pos.CENTER_LEFT);

                CheckBox check = new CheckBox();
                check.setSelected(dep.getStatut() == Statut.TERMINEE);
                check.setDisable(true);

                Label titreDep = new Label(dep.getTitre());
                titreDep.setStyle("-fx-font-size: 13px;");

                Label statutDep = new Label("[" + dep.getStatut() + "]");
                statutDep.setStyle(getStatutStyle(dep.getStatut()) + " -fx-font-size: 11px;");

                depItem.getChildren().addAll(check, titreDep, statutDep);
                listeDeps.getChildren().add(depItem);
            }
            dependancesBox.getChildren().addAll(labelDep, listeDeps);
        }

        layout.getChildren().addAll(titre, statutBox, prioriteBox, datesBox, descBox, dependancesBox);

        Button editButton = new Button("✏️ Modifier");
        editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setOnAction(e -> {
            dialog.close();
            EditDialog editDialog = new EditDialog(task, model);
            editDialog.showAndWait();
        });

        HBox buttonBox = new HBox(editButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        layout.getChildren().add(buttonBox);

        dialog.getDialogPane().setContent(layout);
        dialog.showAndWait();
    }

    private String getStatutStyle(Statut statut) {
        return switch (statut) {
            case A_FAIRE -> "-fx-text-fill: #6b7280;";
            case EN_COURS -> "-fx-text-fill: #3b82f6; -fx-font-weight: bold;";
            case TERMINEE -> "-fx-text-fill: #10b981;";
            case ARCHIVEE -> "-fx-text-fill: #9ca3af;";
            case BLOQUEE -> "-fx-text-fill: #ef4444;";
        };
    }

    private String getPrioriteStyle(Priorite priorite) {
        return switch (priorite) {
            case BASSE -> "-fx-text-fill: #10b981;";
            case NORMALE -> "-fx-text-fill: #3b82f6;";
            case HAUTE -> "-fx-text-fill: #f59e0b;";
            case URGENTE -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
        };
    }

    private boolean estEnRetard() {
        if (task.getDateFin() == null) return false;
        LocalDate aujourdHui = LocalDate.now();
        LocalDate fin = task.getDateFin().toLocalDate();
        return fin.isBefore(aujourdHui) && task.getStatut() != Statut.TERMINEE;
    }

    private void toggleArchive() {
        if (task.getStatut() == Statut.ARCHIVEE) {
            archiveController.restaurer(task);
        } else {
            archiveController.archiver(task);
        }
    }

    private void deleteTask() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la tâche");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff;");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            model.supprimerTask(task);
        }
    }

    public HBox getView() {
        return card;
    }
}
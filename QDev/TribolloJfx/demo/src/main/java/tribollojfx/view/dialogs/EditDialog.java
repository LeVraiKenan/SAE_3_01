package tribollojfx.view.dialogs;

import javafx.scene.layout.HBox;
import tribollojfx.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class EditDialog extends Dialog<Task> {
    private Task task;
    private TaskModel model;

    public EditDialog(Task task, TaskModel model) {
        this.task = task;
        this.model = model;

        setTitle("Modifier tâche: " + task.getTitre());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titre = new TextField(task.getTitre());
        TextArea desc = new TextArea(task.getDescription());
        desc.setPrefRowCount(3);

        ComboBox<Priorite> priorite = new ComboBox<>();
        priorite.getItems().addAll(Priorite.values());
        priorite.setValue(task.getPriorite());

        VBox dependancesBox = new VBox(5);
        Label dependancesLabel = new Label("Dépendances:");
        dependancesLabel.setStyle("-fx-font-weight: bold;");
        dependancesBox.getChildren().add(dependancesLabel);

        if (task.getDependances().isEmpty()) {
            Label aucuneLabel = new Label("Aucune dépendance");
            aucuneLabel.setStyle("-fx-text-fill: gray;");
            dependancesBox.getChildren().add(aucuneLabel);
        } else {
            for (Task dep : task.getDependances()) {
                HBox depBox = new HBox(10);
                depBox.setAlignment(Pos.CENTER_LEFT);

                CheckBox check = new CheckBox();
                check.setSelected(dep.getStatut() == Statut.TERMINEE);
                check.setOnAction(e -> {
                    Statut nouveauStatut = check.isSelected() ? Statut.TERMINEE : Statut.A_FAIRE;
                    model.updateDependanceStatut(dep, nouveauStatut);
                });

                Label titreLabel = new Label(dep.getTitre());
                Label statutLabel = new Label("[" + dep.getStatut() + "]");
                statutLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                Button supprimer = new Button("Retirer");
                supprimer.setStyle("-fx-font-size: 10px;");
                supprimer.setOnAction(e -> {
                    model.retirerDependance(task, dep);
                    close();
                    new EditDialog(task, model).showAndWait();
                });

                depBox.getChildren().addAll(check, titreLabel, statutLabel, supprimer);
                dependancesBox.getChildren().add(depBox);
            }
        }

        Button btnAjouterDependance = new Button("+ Ajouter une dépendance");
        btnAjouterDependance.setOnAction(e -> {
            ajouterDependanceDialog();
            close();
        });

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titre, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(desc, 1, 1);
        grid.add(new Label("Priorité:"), 0, 2);
        grid.add(priorite, 1, 2);
        grid.add(new Label("Dépendances:"), 0, 3);
        grid.add(dependancesBox, 1, 3);
        grid.add(btnAjouterDependance, 1, 4);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                task.setTitre(titre.getText().trim());
                task.setDescription(desc.getText().trim());
                task.setPriorite(priorite.getValue());
                return task;
            }
            return null;
        });
    }

    private void ajouterDependanceDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une dépendance");
        dialog.setHeaderText("Choisir une tâche à ajouter comme dépendance");

        ListView<Task> taskListView = new ListView<>();
        taskListView.getItems().addAll(model.getTaches().stream()
                .filter(t -> t != task && !task.getDependances().contains(t))
                .toList());

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
}
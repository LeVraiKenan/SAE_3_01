package tribollojfx.view.dialogs;

import tribollojfx.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDate;

public class TaskDialog extends Dialog<Task> {
    private TaskModel model;
    private Statut statut;

    public TaskDialog(TaskModel model, Statut statut) {
        this.model = model;
        this.statut = statut;

        setTitle("Nouvelle tâche");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titre = new TextField();
        titre.setPromptText("Titre de la tâche");

        TextArea desc = new TextArea();
        desc.setPromptText("Description");
        desc.setPrefRowCount(3);

        DatePicker debut = new DatePicker();
        debut.setPromptText("Date de début");
        debut.setValue(LocalDate.now());

        DatePicker fin = new DatePicker();
        fin.setPromptText("Date de fin");

        ComboBox<Priorite> priorite = new ComboBox<>();
        priorite.getItems().addAll(Priorite.values());
        priorite.setValue(Priorite.NORMALE);

        ComboBox<Task> dependance = new ComboBox<>();
        dependance.getItems().addAll(model.getTaches());
        dependance.setPromptText("Ajouter une dépendance");

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titre, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(desc, 1, 1);
        grid.add(new Label("Début:"), 0, 2);
        grid.add(debut, 1, 2);
        grid.add(new Label("Fin:"), 0, 3);
        grid.add(fin, 1, 3);
        grid.add(new Label("Priorité:"), 0, 4);
        grid.add(priorite, 1, 4);
        grid.add(new Label("Dépendance:"), 0, 5);
        grid.add(dependance, 1, 5);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        Runnable validate = () -> {
            boolean titreOk = !titre.getText().trim().isEmpty();
            boolean debutOk = debut.getValue() != null;
            boolean finOk = fin.getValue() != null &&
                    (debut.getValue() == null || !fin.getValue().isBefore(debut.getValue()));
            okButton.setDisable(!(titreOk && debutOk && finOk));
        };

        titre.textProperty().addListener((obs, o, n) -> validate.run());
        debut.valueProperty().addListener((obs, o, n) -> validate.run());
        fin.valueProperty().addListener((obs, o, n) -> validate.run());

        setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                Task t = new Task(
                        titre.getText().trim(),
                        desc.getText().trim(),
                        priorite.getValue(),
                        debut.getValue().atStartOfDay(),
                        fin.getValue().atStartOfDay()
                );

                if (dependance.getValue() != null) {
                    t.addDependance(dependance.getValue());
                }

                t.changerStatut(statut);
                return t;
            }
            return null;
        });

        setOnCloseRequest(e -> {
            Task result = getResult();
            if (result != null) {
                model.ajouterTask(result);
            }
        });
    }
}
package tribollojfx.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class Main extends Application implements TaskModelObservateur {
    private TaskModel model;
    private VBox colonneAFaire;
    private VBox colonneArchivee;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        model = new TaskModel();
        model.addObserver(this);

        colonneAFaire = new VBox(10);
        colonneArchivee = new VBox(10);

        VBox colonneGauche = new VBox(new Label("À faire"), colonneAFaire, creerBoutonAjout(Statut.A_FAIRE));
        VBox colonneDroite = new VBox(new Label("Archivée"), colonneArchivee);

        HBox root = new HBox(20, colonneGauche, colonneDroite);
        root.setPadding(new Insets(20));

        notifier(model.getTaches());

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Tribollo");
        stage.show();
    }

    private Button creerBoutonAjout(Statut statut) {
        Button btn = new Button("+ Ajouter");
        btn.setOnAction(e -> {
            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("Créer une tâche");

            TextField titre = new TextField();
            TextArea desc = new TextArea();
            DatePicker debut = new DatePicker();
            DatePicker fin = new DatePicker();
            ComboBox<Priorite> priorite = new ComboBox<>();

            priorite.getItems().setAll(Priorite.values());

            GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(10);
            grid.setPadding(new Insets(10));
            grid.addRow(0, new Label("Titre:"), titre);
            grid.addRow(1, new Label("Description:"), desc);
            grid.addRow(2, new Label("Début:"), debut);
            grid.addRow(3, new Label("Fin:"), fin);
            grid.addRow(4, new Label("Priorité:"), priorite);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btnType -> {
                if (btnType == ButtonType.OK) {
                    Task t = new Task(
                            titre.getText(),
                            desc.getText(),
                            priorite.getValue(),
                            debut.getValue().atStartOfDay(),
                            fin.getValue().atStartOfDay()
                    );
                    t.changerStatut(statut);
                    return t;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(model::ajouterTask);
        });
        return btn;
    }

    private HBox creerCarte(Task t) {
        Label titre = new Label(t.getTitre());
        CheckBox archive = new CheckBox("Archiver");
        archive.setSelected(t.getStatut() == Statut.ARCHIVEE);
        archive.setOnAction(e -> {
            t.changerStatut(archive.isSelected() ? Statut.ARCHIVEE : Statut.A_FAIRE);
            model.notifier();
        });

        Button supprimer = new Button("Supprimer");
        supprimer.setOnAction(e -> model.supprimerTask(t));

        Button ajouterSousTache = new Button("+ Sous-tâche");
        ajouterSousTache.setOnAction(e -> {
            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("Créer une sous-tâche");

            TextField stitre = new TextField();
            TextArea desc = new TextArea();

            GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(10);
            grid.setPadding(new Insets(10));
            grid.addRow(0, new Label("Titre:"), stitre);
            grid.addRow(1, new Label("Description:"), desc);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btnType -> {
                if (btnType == ButtonType.OK) {
                    Task st = new Task(stitre.getText(), desc.getText(), Priorite.NORMALE,
                            LocalDateTime.now(), LocalDateTime.now().plusDays(1));
                    t.addSousTask(st);
                    return st;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(st -> model.notifier());
        });
        HBox box = new HBox(10, titre, archive, ajouterSousTache, supprimer);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-border-color: gray; -fx-background-color: #f0f0f0;");
        VBox sousTachesBox = new VBox(5);
        for (Task st : t.getSousTaches()) {
            Label lbl = new Label("↳ " + st.getTitre());
            sousTachesBox.getChildren().add(lbl);
        }
        VBox carte = new VBox(box, sousTachesBox);
        return new HBox(carte);
    }

    @Override
    public void notifier(List<Task> tasks) {
        colonneAFaire.getChildren().clear();
        colonneArchivee.getChildren().clear();
        for (Task t : tasks) {
            if (t.getStatut() == Statut.A_FAIRE) {
                colonneAFaire.getChildren().add(creerCarte(t));
            } else if (t.getStatut() == Statut.ARCHIVEE) {
                colonneArchivee.getChildren().add(creerCarte(t));
            }
        }
    }
}
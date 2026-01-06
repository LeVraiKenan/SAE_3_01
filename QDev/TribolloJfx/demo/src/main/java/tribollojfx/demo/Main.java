package tribollojfx.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class Main extends Application implements TaskModelObservateur {
    private TaskModel model;
    private Map<Statut, VBox> colonnes;
    private Task draggedTask;

    private BorderPane root;
    private HBox vueTableauRoot;
    private TableView<Task> vueListe;

    @Override
    public void start(Stage stage) {
        model = new TaskModel();
        model.addObserver(this);

        colonnes = new HashMap<>();

        VBox colonneAFaire = creerColonne("À FAIRE", Statut.A_FAIRE);
        VBox colonneEnCours = creerColonne("EN COURS", Statut.EN_COURS);
        VBox colonneTerminee = creerColonne("TERMINÉE", Statut.TERMINEE);
        VBox colonneArchivee = creerColonne("ARCHIVÉE", Statut.ARCHIVEE);

        colonnes.put(Statut.A_FAIRE, colonneAFaire);
        colonnes.put(Statut.EN_COURS, colonneEnCours);
        colonnes.put(Statut.TERMINEE, colonneTerminee);
        colonnes.put(Statut.ARCHIVEE, colonneArchivee);

        vueTableauRoot = new HBox(20, colonneAFaire, colonneEnCours, colonneTerminee, colonneArchivee);
        vueTableauRoot.setPadding(new Insets(20));

        vueListe = creerVueListe();

        Button btnVueTableau = new Button("Vue tableau");
        Button btnVueListe = new Button("Vue liste");

        btnVueTableau.setOnAction(e -> root.setCenter(vueTableauRoot));
        btnVueListe.setOnAction(e -> {
            mettreAJourVueListe();
            root.setCenter(vueListe);
        });

        HBox barreBoutons = new HBox(10, btnVueTableau, btnVueListe);
        barreBoutons.setPadding(new Insets(10));
        barreBoutons.setAlignment(Pos.CENTER_LEFT);

        root = new BorderPane();
        root.setTop(barreBoutons);
        root.setCenter(vueTableauRoot);

        notifier(model.getTaches());

        stage.setScene(new Scene(root, 1000, 600));
        stage.setTitle("Tribollo - Tableau Simple");
        stage.show();
    }

    private VBox creerColonne(String titre, Statut statut) {
        VBox colonne = new VBox(10);
        colonne.setPadding(new Insets(10));
        colonne.setPrefWidth(230);

        Label titreLabel = new Label(titre);

        VBox contenu = new VBox(5);
        contenu.setId("contenu-" + statut.name());

        Button btnAjouter = new Button("+ Ajouter");
        btnAjouter.setOnAction(e -> ouvrirDialogueAjout(statut));

        colonne.getChildren().addAll(titreLabel, contenu, btnAjouter);

        configurerDragDrop(contenu, statut);

        return colonne;
    }

    private void configurerDragDrop(VBox contenu, Statut statutCible) {
        contenu.setOnDragOver(event -> {
            if (event.getGestureSource() != contenu && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        contenu.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && draggedTask != null) {
                model.updateTaskStatut(draggedTask, statutCible);
                success = true;
                draggedTask = null;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private boolean estEnRetard(Task t) {
        if (t.getDateFin() == null) return false;
        LocalDate aujourdHui = LocalDate.now();
        LocalDate fin = t.getDateFin().toLocalDate();
        return fin.isBefore(aujourdHui) && t.getStatut() != Statut.TERMINEE;
    }

    private HBox creerCarte(Task t) {
        HBox carte = new HBox(10);
        carte.setPadding(new Insets(8));
        carte.setAlignment(Pos.CENTER_LEFT);

        Rectangle prioriteIndicator = new Rectangle(5, 15);
        switch (t.getPriorite()) {
            case BASSE: prioriteIndicator.setFill(Color.LIGHTGREEN); break;
            case NORMALE: prioriteIndicator.setFill(Color.LIGHTBLUE); break;
            case HAUTE: prioriteIndicator.setFill(Color.ORANGE); break;
            case URGENTE: prioriteIndicator.setFill(Color.RED); break;
        }

        VBox infos = new VBox(3);
        Label titre = new Label(t.getTitre());

        if (estEnRetard(t)) {
            carte.setStyle("-fx-background-color: #ffe6e6; -fx-border-color: red;");
            Label retardIcon = new Label("⚠");
            retardIcon.setTextFill(Color.RED);
            retardIcon.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            retardIcon.setTooltip(new Tooltip("Tâche en retard"));
            HBox titreBox = new HBox(5, retardIcon, titre);
            infos.getChildren().add(titreBox);
        } else {
            carte.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
            infos.getChildren().add(titre);
        }

        String sousTachesInfo = "";
        if (!t.getSousTaches().isEmpty()) {
            int terminees = 0;
            int enCours = 0;
            for (Task st : t.getSousTaches()) {
                if (st.getStatut() == Statut.TERMINEE) terminees++;
                if (st.getStatut() == Statut.EN_COURS) enCours++;
            }
            sousTachesInfo = " [" + terminees + "/" + t.getSousTaches().size() + "]";
            if (enCours > 0) {
                sousTachesInfo += " (en cours)";
            }
        }

        Label details = new Label(t.getSousTaches().size() + " sous-tâches" + sousTachesInfo);
        infos.getChildren().add(details);

        HBox actions = new HBox(5);

        Button btnArchive = new Button(t.getStatut() == Statut.ARCHIVEE ? "R" : "A");
        btnArchive.setTooltip(new Tooltip(t.getStatut() == Statut.ARCHIVEE ? "Restaurer" : "Archiver"));
        btnArchive.setOnAction(e -> {
            if (t.getStatut() == Statut.ARCHIVEE) {
                model.updateTaskStatut(t, Statut.A_FAIRE);
            } else {
                model.updateTaskStatut(t, Statut.ARCHIVEE);
            }
        });

        Button btnSousTache = new Button("+");
        btnSousTache.setTooltip(new Tooltip("Ajouter sous-tâche"));
        btnSousTache.setOnAction(e -> ouvrirDialogueSousTache(t));

        Button btnSupprimer = new Button("X");
        btnSupprimer.setTooltip(new Tooltip("Supprimer"));
        btnSupprimer.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer la tâche");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                model.supprimerTask(t);
            }
        });

        actions.getChildren().addAll(btnArchive, btnSousTache, btnSupprimer);

        carte.getChildren().addAll(prioriteIndicator, infos, actions);

        carte.setOnDragDetected(event -> {
            if (t.getStatut() != Statut.ARCHIVEE) {
                draggedTask = t;
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("task");
                db.setContent(content);
                event.consume();
            }
        });

        carte.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ouvrirDialogueEdition(t);
            } else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                ouvrirDetailTask(t);
            }
        });

        return carte;
    }

    private void ouvrirDialogueSousTache(Task parent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouvelle sous-tâche");
        dialog.setHeaderText("Ajouter une sous-tâche à: " + parent.getTitre());
        dialog.setContentText("Titre:");

        dialog.showAndWait().ifPresent(titre -> {
            if (!titre.trim().isEmpty()) {
                Task sousTask = new Task(titre.trim());
                sousTask.setPriorite(parent.getPriorite());
                model.ajouterSousTask(parent, sousTask);
            }
        });
    }

    private void ouvrirDetailTask(Task t) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de la tâche : " + t.getTitre());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(400);

        Label titre = new Label(t.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label labelStatut = new Label("Status:");
        labelStatut.setStyle("-fx-font-size: 18px;");
        Text statusText = new Text("[" + t.getStatut() + "]");

        Label labelDesc = new Label("Description :");
        labelDesc.setStyle("-fx-font-weight: bold;");
        Text descText = new Text(t.getDescription() == null || t.getDescription().isEmpty()
                ? "Aucune description fournie." : t.getDescription());
        descText.setWrappingWidth(350);

        VBox dependancesBox = new VBox(5);
        Label labelDep = new Label("Dépend de (" + t.getDependance().size() + ") :");
        labelDep.setStyle("-fx-font-weight: bold;");
        dependancesBox.getChildren().add(labelDep);

        if (t.getDependance().isEmpty()) {
            dependancesBox.getChildren().add(new Label("Aucune dépendance."));
        } else {
            for (Task dep : t.getDependance()) {
                Label lDep = new Label("- " + dep.getTitre() + " (" + dep.getStatut() + ")");
                if (dep.getStatut() != Statut.TERMINEE) {
                    lDep.setTextFill(Color.RED);
                }
                dependancesBox.getChildren().add(lDep);
            }
        }

        VBox sousTachesBox = new VBox(5);
        Label labelST = new Label("Sous-tâches :");
        labelST.setStyle("-fx-font-weight: bold;");
        sousTachesBox.getChildren().add(labelST);

        if (t.getSousTaches().isEmpty()) {
            sousTachesBox.getChildren().add(new Label("Aucune sous-tâche."));
        } else {
            for (Task st : t.getSousTaches()) {
                CheckBox cb = new CheckBox(st.getTitre() + " [" + st.getStatut() + "]");
                cb.setSelected((st.getStatut() == Statut.TERMINEE));
                cb.setDisable(true);
                sousTachesBox.getChildren().add(cb);
            }
        }

        layout.getChildren().addAll(titre, labelStatut, statusText, labelDesc, descText, labelDep, dependancesBox, labelST, sousTachesBox);
        dialog.getDialogPane().setContent(layout);
        dialog.showAndWait();
    }

    private void ouvrirDialogueEdition(Task t) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Modifier tâche: " + t.getTitre());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titre = new TextField(t.getTitre());
        TextArea desc = new TextArea(t.getDescription());
        desc.setPrefRowCount(3);

        ComboBox<Priorite> priorite = new ComboBox<>();
        priorite.getItems().addAll(Priorite.values());
        priorite.setValue(t.getPriorite());

        VBox sousTachesBox = new VBox(5);
        if (!t.getSousTaches().isEmpty()) {
            Label sousTachesLabel = new Label("Sous-tâches:");
            sousTachesBox.getChildren().add(sousTachesLabel);

            for (Task st : t.getSousTaches()) {
                HBox stBox = new HBox(10);
                stBox.setAlignment(Pos.CENTER_LEFT);

                CheckBox check = new CheckBox();
                check.setSelected(st.getStatut() == Statut.TERMINEE);
                check.setOnAction(e -> {
                    Statut nouveauStatut = check.isSelected() ? Statut.TERMINEE : Statut.A_FAIRE;
                    model.updateSousTaskStatut(st, nouveauStatut);
                });

                Label titreLabel = new Label(st.getTitre());

                Label statutLabel = new Label();
                statutLabel.setPrefWidth(70);
                updateStatutLabel(statutLabel, st.getStatut());

                Button supprimer = new Button("Supp");
                supprimer.setOnAction(e -> {
                    t.getSousTaches().remove(st);
                    model.notifier();
                });

                stBox.getChildren().addAll(check, titreLabel, statutLabel, supprimer);
                sousTachesBox.getChildren().add(stBox);
            }
        } else {
            Label aucuneLabel = new Label("Aucune sous-tâche");
            sousTachesBox.getChildren().add(aucuneLabel);
        }

        Button btnAjouterSousTache = new Button("+ Ajouter une sous-tâche");
        btnAjouterSousTache.setOnAction(e -> {
            ouvrirDialogueSousTache(t);
            dialog.close();
        });

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titre, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(desc, 1, 1);
        grid.add(new Label("Priorité:"), 0, 2);
        grid.add(priorite, 1, 2);
        grid.add(new Label("Sous-tâches:"), 0, 3);
        grid.add(sousTachesBox, 1, 3);
        grid.add(btnAjouterSousTache, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                t.setTitre(titre.getText().trim());
                t.setDescription(desc.getText().trim());
                t.setPriorite(priorite.getValue());
                return t;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> model.notifier());
    }

    private void updateStatutLabel(Label label, Statut statut) {
        switch (statut) {
            case A_FAIRE:
                label.setText("À faire");
                label.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
                break;
            case EN_COURS:
                label.setText("En cours");
                label.setStyle("-fx-text-fill: blue; -fx-font-size: 10px; -fx-font-weight: bold;");
                break;
            case TERMINEE:
                label.setText("Terminée");
                label.setStyle("-fx-text-fill: green; -fx-font-size: 10px;");
                break;
            case ARCHIVEE:
                label.setText("Archivée");
                label.setStyle("-fx-text-fill: orange; -fx-font-size: 10px;");
                break;
        }
    }

    private void ouvrirDialogueAjout(Statut statut) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle tâche");

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
        priorite.setPromptText("Sélectionnez une priorité");

        ComboBox<Task> dependance = new ComboBox<>();
        dependance.getItems().addAll(model.getTaches());
        dependance.setPromptText("Ajout de dépendance");

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

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        Runnable validate = () -> {
            boolean titreOk = !titre.getText().trim().isEmpty();
            boolean debutOk = debut.getValue() != null && !debut.getValue().isBefore(LocalDate.now());
            boolean finOk = fin.getValue() != null && !fin.getValue().isBefore(debut.getValue());
            okButton.setDisable(!(titreOk && debutOk && finOk));
        };

        titre.textProperty().addListener((obs, o, n) -> validate.run());
        debut.valueProperty().addListener((obs, o, n) -> validate.run());
        fin.valueProperty().addListener((obs, o, n) -> validate.run());

        dialog.setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                if (debut.getValue().isBefore(LocalDate.now())) {
                    new Alert(Alert.AlertType.ERROR,
                            "La date de début doit être aujourd'hui ou plus tard.").showAndWait();
                    return null;
                }
                if (fin.getValue().isBefore(debut.getValue())) {
                    new Alert(Alert.AlertType.ERROR,
                            "La date de fin doit être après ou égale à la date de début.").showAndWait();
                    return null;
                }

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

        dialog.showAndWait().ifPresent(model::ajouterTask);
    }

    private TableView<Task> creerVueListe() {
        TableView<Task> table = new TableView<>();

        TableColumn<Task, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getTitre()
        ));

        TableColumn<Task, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDescription() == null ? "" : data.getValue().getDescription()
        ));

        TableColumn<Task, String> colPriorite = new TableColumn<>("Priorité");
        colPriorite.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getPriorite() == null ? "" : data.getValue().getPriorite().name()
        ));

        TableColumn<Task, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatut().name()
        ));

        table.getColumns().addAll(colTitre, colDescription, colPriorite, colStatut);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        return table;
    }

    private void mettreAJourVueListe() {
        vueListe.getItems().setAll(model.getTaches());
    }

    @Override
    public void notifier(List<Task> tasks) {
        for (VBox colonne : colonnes.values()) {
            VBox contenu = (VBox) colonne.getChildren().get(1);
            contenu.getChildren().clear();
        }

        Map<Statut, Integer> compteurs = new HashMap<>();
        for (Statut statut : Statut.values()) {
            compteurs.put(statut, 0);
        }

        for (Task t : tasks) {
            VBox colonne = colonnes.get(t.getStatut());
            if (colonne != null) {
                VBox contenu = (VBox) colonne.getChildren().get(1);
                HBox carte = creerCarte(t);
                contenu.getChildren().add(carte);

                compteurs.put(t.getStatut(), compteurs.get(t.getStatut()) + 1);

                if (!t.getSousTaches().isEmpty()) {
                    VBox sousTachesBox = new VBox(2);
                    sousTachesBox.setPadding(new Insets(5, 0, 0, 20));

                    List<Task> sousTachesTriees = new ArrayList<>(t.getSousTaches());
                    sousTachesTriees.sort((st1, st2) -> {
                        if (st1.getStatut() == Statut.EN_COURS && st2.getStatut() != Statut.EN_COURS) return -1;
                        if (st1.getStatut() != Statut.EN_COURS && st2.getStatut() == Statut.EN_COURS) return 1;
                        if (st1.getStatut() == Statut.A_FAIRE && st2.getStatut() == Statut.TERMINEE) return -1;
                        if (st1.getStatut() == Statut.TERMINEE && st2.getStatut() == Statut.A_FAIRE) return 1;
                        return st1.getTitre().compareTo(st2.getTitre());
                    });

                    for (Task st : sousTachesTriees) {
                        HBox stBox = new HBox(5);
                        stBox.setAlignment(Pos.CENTER_LEFT);
                        stBox.setPadding(new Insets(2));

                        Circle statutDot = new Circle(3);
                        switch (st.getStatut()) {
                            case A_FAIRE:
                                statutDot.setFill(Color.LIGHTGRAY);
                                break;
                            case EN_COURS:
                                statutDot.setFill(Color.BLUE);
                                break;
                            case TERMINEE:
                                statutDot.setFill(Color.GREEN);
                                break;
                        }

                        Label stLabel = new Label(st.getTitre());

                        CheckBox check = new CheckBox();
                        check.setSelected(st.getStatut() == Statut.TERMINEE);
                        check.setOnAction(e -> {
                            Statut nouveauStatut = check.isSelected() ? Statut.TERMINEE : Statut.A_FAIRE;
                            model.updateSousTaskStatut(st, nouveauStatut);
                        });

                        stBox.getChildren().addAll(statutDot, stLabel, check);
                        sousTachesBox.getChildren().add(stBox);
                    }
                    contenu.getChildren().add(sousTachesBox);
                }
            }
        }

        for (Map.Entry<Statut, VBox> entry : colonnes.entrySet()) {
            Statut statut = entry.getKey();
            VBox colonne = entry.getValue();
            Label titreLabel = (Label) colonne.getChildren().get(0);
            titreLabel.setText(getNomColonne(statut) + " (" + compteurs.get(statut) + ")");
        }

        if (vueListe != null && root.getCenter() == vueListe) {
            mettreAJourVueListe();
        }
    }

    private String getNomColonne(Statut statut) {
        switch (statut) {
            case A_FAIRE: return "À FAIRE";
            case EN_COURS: return "EN COURS";
            case TERMINEE: return "TERMINÉE";
            case ARCHIVEE: return "ARCHIVÉE";
            default: return statut.name();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

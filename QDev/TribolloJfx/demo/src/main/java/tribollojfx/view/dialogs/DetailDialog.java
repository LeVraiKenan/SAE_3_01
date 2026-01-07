package tribollojfx.view.dialogs;

import tribollojfx.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

public class DetailDialog extends Dialog<Void> {
    public DetailDialog(Task task) {
        setTitle("Détails de la tâche : " + task.getTitre());
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(400);

        Label titre = new Label(task.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label labelStatut = new Label("Statut:");
        labelStatut.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Text statusText = new Text("[" + task.getStatut() + "]");

        Label labelPriorite = new Label("Priorité:");
        labelPriorite.setStyle("-fx-font-weight: bold;");
        Text prioriteText = new Text(task.getPriorite().name());

        Label labelDesc = new Label("Description :");
        labelDesc.setStyle("-fx-font-weight: bold;");
        Text descText = new Text(task.getDescription() == null || task.getDescription().isEmpty()
                ? "Aucune description fournie." : task.getDescription());
        descText.setWrappingWidth(350);

        VBox dependancesBox = new VBox(5);
        Label labelDep = new Label("Dépend de (" + task.getDependances().size() + ") :");
        labelDep.setStyle("-fx-font-weight: bold;");
        dependancesBox.getChildren().add(labelDep);

        if (task.getDependances().isEmpty()) {
            dependancesBox.getChildren().add(new Label("Aucune dépendance."));
        } else {
            for (Task dep : task.getDependances()) {
                Label lDep = new Label("- " + dep.getTitre() + " (" + dep.getStatut() + ")");
                if (dep.getStatut() != Statut.TERMINEE) {
                    lDep.setTextFill(Color.RED);
                }
                dependancesBox.getChildren().add(lDep);
            }
        }

        layout.getChildren().addAll(titre, labelStatut, statusText, labelPriorite,
                prioriteText, labelDesc, descText, dependancesBox);
        getDialogPane().setContent(layout);
    }
}
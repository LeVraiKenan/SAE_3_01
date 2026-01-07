package tribollojfx.view.components;

import tribollojfx.model.Statut;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.util.Map;

public class ColumnContainerView {
    private HBox container;

    public ColumnContainerView(Map<Statut, ColumnView> columns) {
        createContainer(columns);
    }

    private void createContainer(Map<Statut, ColumnView> columns) {
        container = new HBox(25);
        container.setPadding(new Insets(25));
        container.setStyle("-fx-background-color: #f8fafc;");

        if (columns.get(Statut.A_FAIRE) != null) {
            container.getChildren().add(columns.get(Statut.A_FAIRE).getView());
        }
        if (columns.get(Statut.EN_COURS) != null) {
            container.getChildren().add(columns.get(Statut.EN_COURS).getView());
        }
        if (columns.get(Statut.TERMINEE) != null) {
            container.getChildren().add(columns.get(Statut.TERMINEE).getView());
        }
        if (columns.get(Statut.ARCHIVEE) != null) {
            container.getChildren().add(columns.get(Statut.ARCHIVEE).getView());
        }
    }

    public HBox getView() {
        return container;
    }
}
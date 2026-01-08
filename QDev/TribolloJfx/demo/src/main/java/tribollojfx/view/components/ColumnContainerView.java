package tribollojfx.view.components;

import tribollojfx.model.Statut;
import tribollojfx.model.TaskModel;
import tribollojfx.controller.ColonneController;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.util.Map;

public class ColumnContainerView {
    private HBox container;
    private TaskModel model;
    private Map<Statut, ColumnView> columns;
    private Map<Integer, PersoColumnView> persoColumns;
    private ColonneController colonneController;

    public ColumnContainerView(Map<Statut, ColumnView> columns, TaskModel model, ColonneController colonneController) {
        this.columns = columns;
        this.model = model;
        this.colonneController = colonneController;
        this.persoColumns = new java.util.HashMap<>();
        createContainer();
    }

    private void createContainer() {
        container = new HBox(25);
        container.setPadding(new Insets(25));
        container.setStyle("-fx-background-color: #f8fafc;");
        rebuildContainer();
    }

    private void rebuildContainer() {
        container.getChildren().clear();
        persoColumns.clear();

        if (columns.get(Statut.A_FAIRE) != null) {
            container.getChildren().add(columns.get(Statut.A_FAIRE).getView());
        }
        if (columns.get(Statut.EN_COURS) != null) {
            container.getChildren().add(columns.get(Statut.EN_COURS).getView());
        }
        if (columns.get(Statut.TERMINEE) != null) {
            container.getChildren().add(columns.get(Statut.TERMINEE).getView());
        }

        model.getColonnesPersonnalisees().forEach((id, nom) -> {
            PersoColumnView persoColumn = new PersoColumnView(id, nom, model, colonneController);
            persoColumns.put(id, persoColumn);
            container.getChildren().add(persoColumn.getView());
        });

        if (columns.get(Statut.ARCHIVEE) != null) {
            container.getChildren().add(columns.get(Statut.ARCHIVEE).getView());
        }
    }

    public void refresh() {
        rebuildContainer();
    }

    public Map<Integer, PersoColumnView> getPersoColumns() {
        return persoColumns;
    }

    public HBox getView() {
        return container;
    }
}
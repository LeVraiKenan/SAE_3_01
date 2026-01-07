package tribollojfx.view.components;

import tribollojfx.model.Task;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;

public class TaskTableView {
    private TableView<Task> table;

    public TaskTableView() {
        table = new TableView<>();

        TableColumn<Task, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitre()));

        TableColumn<Task, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(cellData -> {
            String desc = cellData.getValue().getDescription();
            return new SimpleStringProperty(desc != null ? desc : "");
        });

        TableColumn<Task, String> colPriorite = new TableColumn<>("Priorité");
        colPriorite.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPriorite().toString()));

        TableColumn<Task, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatut().toString()));

        table.getColumns().add(colTitre);
        table.getColumns().add(colDescription);
        table.getColumns().add(colPriorite);
        table.getColumns().add(colStatut);
    }

    public void updateTasks(java.util.List<Task> tasks) {
        table.setItems(FXCollections.observableArrayList(tasks));
    }

    public TableView<Task> getView() {
        return table;
    }
}
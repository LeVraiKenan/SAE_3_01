package tribollojfx.controller;

import tribollojfx.model.TaskModel;
import tribollojfx.view.components.GanttView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;

public class GanttController {
    private TaskModel model;
    private GanttView ganttView;

    public GanttController(TaskModel model) {
        this.model = model;
        this.ganttView = new GanttView(model.getTaches());
    }

    public void showGanttView() {
        Stage stage = new Stage();
        stage.setTitle("Diagramme de Gantt");

        ScrollPane scrollPane = new ScrollPane(ganttView.getView());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 1000, 600);
        stage.setScene(scene);
        stage.show();
    }
}
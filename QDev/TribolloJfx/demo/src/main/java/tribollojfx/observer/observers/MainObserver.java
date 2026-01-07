package tribollojfx.observer.observers;

import tribollojfx.observer.TaskObserver;
import tribollojfx.model.*;
import tribollojfx.view.components.*;
import javafx.scene.input.*;
import java.util.List;
import java.util.Map;

public class MainObserver implements TaskObserver {
    private Map<Statut, ColumnView> columns;
    private TaskModel model;
    private static Task draggedTask;

    public MainObserver(TaskModel model, Map<Statut, ColumnView> columns) {
        this.model = model;
        this.columns = columns;
        model.addObserver(this);
        setupGlobalDragDrop();
    }

    private void setupGlobalDragDrop() {
        for (ColumnView sourceColumn : columns.values()) {
            if (sourceColumn == null) continue;
            for (ColumnView targetColumn : columns.values()) {
                if (targetColumn == null || sourceColumn == targetColumn) continue;
                setupColumnDragDrop(sourceColumn, targetColumn);
            }
        }
    }

    private void setupColumnDragDrop(ColumnView source, ColumnView target) {
        target.getContent().setOnDragOver(event -> {
            if (event.getGestureSource() != target.getContent() &&
                    event.getDragboard().hasString() &&
                    event.getDragboard().getString().equals("task")) {

                event.acceptTransferModes(TransferMode.MOVE);
                target.getContent().setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); " +
                        "-fx-border-color: #3b82f6; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8;");
            }
            event.consume();
        });

        target.getContent().setOnDragExited(event -> {
            target.getContent().setStyle("");
            event.consume();
        });

        target.getContent().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString() && db.getString().equals("task") && draggedTask != null) {
                model.updateTaskStatut(draggedTask, target.getStatut());
                success = true;
                draggedTask = null;
            }

            target.getContent().setStyle("");
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public static void setDraggedTask(Task task) {
        draggedTask = task;
    }

    public static Task getDraggedTask() {
        return draggedTask;
    }

    @Override
    public void onTasksChanged(List<Task> tasks) {
        int[] counts = new int[Statut.values().length];
        for (Task task : tasks) {
            counts[task.getStatut().ordinal()]++;
        }

        for (Map.Entry<Statut, ColumnView> entry : columns.entrySet()) {
            Statut statut = entry.getKey();
            ColumnView column = entry.getValue();

            column.getContent().getChildren().clear();

            for (Task task : tasks) {
                if (task.getStatut() == statut) {
                    TaskCard card = new TaskCard(task, model);
                    column.getContent().getChildren().add(card.getView());

                    if (!task.getDependances().isEmpty()) {
                        DependanceBox dependanceBox = new DependanceBox(task, model);
                        column.getContent().getChildren().add(dependanceBox.getView());
                    }
                }
            }

            column.updateTitle(counts[statut.ordinal()]);
        }
    }
}
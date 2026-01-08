package tribollojfx.observer.observers;

import tribollojfx.observer.TaskObserver;
import tribollojfx.model.*;
import tribollojfx.view.components.*;
import javafx.scene.input.*;
import java.util.List;
import java.util.Map;

public class MainObserver implements TaskObserver {
    private Map<Statut, ColumnView> columns;
    private ColumnContainerView containerView;
    private TaskModel model;
    private static Task draggedTask;

    public MainObserver(TaskModel model, Map<Statut, ColumnView> columns,
                        ColumnContainerView containerView) {
        this.model = model;
        this.columns = columns;
        this.containerView = containerView;
        model.addObserver(this);
    }

    @Override
    public void onTasksChanged(List<Task> tasks) {
        Map<Integer, PersoColumnView> persoColumns = containerView.getPersoColumns();

        int[] counts = new int[Statut.values().length];

        for (ColumnView column : columns.values()) {
            column.getContent().getChildren().clear();
        }

        for (PersoColumnView persoColumn : persoColumns.values()) {
            persoColumn.getContent().getChildren().clear();
        }

        for (Task task : tasks) {
            if (task.getColonnePersoId() > 0) {
                PersoColumnView persoColumn = persoColumns.get(task.getColonnePersoId());
                if (persoColumn != null) {
                    TaskCard card = new TaskCard(task, model);
                    persoColumn.getContent().getChildren().add(card.getView());

                    if (!task.getDependances().isEmpty()) {
                        DependanceBox dependanceBox = new DependanceBox(task, model);
                        persoColumn.getContent().getChildren().add(dependanceBox.getView());
                    }
                }
            } else {
                counts[task.getStatut().ordinal()]++;

                ColumnView column = columns.get(task.getStatut());
                if (column != null) {
                    TaskCard card = new TaskCard(task, model);
                    column.getContent().getChildren().add(card.getView());

                    if (!task.getDependances().isEmpty()) {
                        DependanceBox dependanceBox = new DependanceBox(task, model);
                        column.getContent().getChildren().add(dependanceBox.getView());
                    }
                }
            }
        }

        for (Map.Entry<Statut, ColumnView> entry : columns.entrySet()) {
            Statut statut = entry.getKey();
            ColumnView column = entry.getValue();
            column.updateTitle(counts[statut.ordinal()]);
        }

        for (Map.Entry<Integer, PersoColumnView> entry : persoColumns.entrySet()) {
            int colonneId = entry.getKey();
            PersoColumnView persoColumn = entry.getValue();
            int count = model.getTachesParColonnePerso(colonneId).size();
            persoColumn.updateTitle(count);
        }
    }

    public static void setDraggedTask(Task task) {
        draggedTask = task;
    }

    public static Task getDraggedTask() {
        return draggedTask;
    }
}
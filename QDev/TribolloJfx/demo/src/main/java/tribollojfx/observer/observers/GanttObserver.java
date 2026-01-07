package tribollojfx.observer.observers;

import tribollojfx.observer.TaskObserver;
import tribollojfx.model.Task;
import tribollojfx.view.components.GanttView;
import java.util.List;

public class GanttObserver implements TaskObserver {
    private GanttView ganttView;

    public GanttObserver(GanttView ganttView) {
        this.ganttView = ganttView;
    }

    @Override
    public void onTasksChanged(List<Task> tasks) {
        ganttView.update(tasks);
    }
}
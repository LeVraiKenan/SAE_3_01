package tribollojfx.observer;

import tribollojfx.model.Task;
import java.util.List;

public interface TaskObserver {
    void onTasksChanged(List<Task> tasks);
}
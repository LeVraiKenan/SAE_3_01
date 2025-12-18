package tribollojfx.demo;

import java.util.List;

public class ArchiveControleur {
    private TaskModel model;

    public ArchiveControleur(TaskModel model) {
        this.model = model;
    }

    public void archiver(Task t) {
        model.updateTaskStatut(t, Statut.ARCHIVEE);
    }

    public void restaurer(Task t) {
        model.updateTaskStatut(t, Statut.A_FAIRE);
    }

    public List<Task> getArchives() {
        return model.getTachesByStatut(Statut.ARCHIVEE);
    }
}
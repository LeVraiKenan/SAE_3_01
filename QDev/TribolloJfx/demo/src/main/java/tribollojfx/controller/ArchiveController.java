package tribollojfx.controller;

import tribollojfx.model.*;

public class ArchiveController {
    private TaskModel model;

    public ArchiveController(TaskModel model) {
        this.model = model;
    }

    public void archiver(Task t) {
        model.updateTaskStatut(t, Statut.ARCHIVEE);
    }

    public void restaurer(Task t) {
        model.updateTaskStatut(t, Statut.A_FAIRE);
    }
}
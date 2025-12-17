package tribollojfx.demo;

import java.util.ArrayList;
import java.util.List;

public class ArchiveControleur {
    private TaskModel model; // Tout est gérée par une seule liste du model à présent

    public ArchiveControleur(TaskModel model) {
        this.model = model;
    }

    public void archiver(Task t){
        model.updateTaskStatut(t.getId(), Statut.ARCHIVEE);
    }

    public void restaurer(Task t){
        // On la rebascule sur A_FAIRE par défaut
        // (Note : Ici, nous perdons l'état précédent, exemple si elle était EN_COURS)
        model.updateTaskStatut(t.getId(), Statut.A_FAIRE);
    }

    public List<Task> getArchives() {
        return model.getTachesByStatut(Statut.ARCHIVEE);
    }
}

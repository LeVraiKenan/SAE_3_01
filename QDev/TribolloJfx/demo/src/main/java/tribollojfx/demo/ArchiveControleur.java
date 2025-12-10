package tribollojfx.demo;

import java.util.List;

public class ArchiveControleur {
    private List<Task> archivees;

    public void archiver(Task t){
        archivees.add(t);
    }

    public void restaurer(Task t){
        archivees.remove(t);
    }

    public List<Task> getArchives() {
        return archivees;
    }

    public void supprimerDefinitif(Task t){
        //TODO : cf persistance
    }
}

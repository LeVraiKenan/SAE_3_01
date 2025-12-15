package tribollojfx.demo;

import java.util.ArrayList;
import java.util.List;

public class ArchiveControleur {
    private List<Task> archivees;

    public ArchiveControleur() {
        this.archivees = new ArrayList<>();
    }

    public void archiver(Task t){
        archivees.add(t);
    }

    public void restaurer(Task t){
        archivees.remove(t);
    }

    public List<Task> getArchives() {
        return archivees;
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestArchiveControleur {

    private TaskModel model;
    private ArchiveControleur archiveControleur;
    private Task t1;
    private Task t2;

    @BeforeEach
    void setUp() {
        model = new TaskModel();
        archiveControleur = new ArchiveControleur(model);

        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);

        t1 = new Task("Tâche 1", "Desc 1", Priorite.NORMALE, debut, fin);
        t2 = new Task("Tâche 2", "Desc 2", Priorite.HAUTE, debut, fin);

        model.ajouterTask(t1);
        model.ajouterTask(t2);
    }

    @Test
    void testArchiverMetLaTacheEnArchiveeEtLaRendVisibleDansLesArchives() {
        archiveControleur.archiver(t1);

        assertEquals(Statut.ARCHIVEE, t1.getStatut());

        List<Task> archives = archiveControleur.getArchives();
        assertTrue(archives.contains(t1));
    }

    @Test
    void testRestaurerMetLaTacheEnAFaireEtLaRetireDesArchives() {
        archiveControleur.archiver(t1);
        archiveControleur.archiver(t2);

        archiveControleur.restaurer(t1);

        assertEquals(Statut.A_FAIRE, t1.getStatut());

        List<Task> archives = archiveControleur.getArchives();
        assertFalse(archives.contains(t1));
        assertTrue(archives.contains(t2));
    }
}

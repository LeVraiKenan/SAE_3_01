import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;
import static org.junit.jupiter.api.Assertions.*;

class TestArchiveControleur {

    private ArchiveControleur archiveControleur;
    private Task t1;
    private Task t2;

    @BeforeEach
    void setUp() {
        archiveControleur = new ArchiveControleur();
        t1 = TaskFactory.creerTask("Tâche 1", Priorite.NORMALE);
        t2 = TaskFactory.creerTask("Tâche 2", Priorite.HAUTE);
    }

    @Test
    void testArchiverAjouteLaTache() {
        archiveControleur.archiver(t1);
        assertTrue(archiveControleur.getArchives().contains(t1));
        assertEquals(1, archiveControleur.getArchives().size());
    }

    @Test
    void testRestaurerRetireLaTache() {
        archiveControleur.archiver(t1);
        archiveControleur.archiver(t2);

        archiveControleur.restaurer(t1);

        assertFalse(archiveControleur.getArchives().contains(t1));
        assertTrue(archiveControleur.getArchives().contains(t2));
        assertEquals(1, archiveControleur.getArchives().size());
    }
}

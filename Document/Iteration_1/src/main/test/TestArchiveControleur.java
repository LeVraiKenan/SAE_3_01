import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class TestArchiveControleur {

    private ArchiveControleur archiveControleur;
    private Task t1;
    private Task t2;

    @BeforeEach
    void setUp() {
        archiveControleur = new ArchiveControleur();

        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);

        t1 = TaskFactory.creerTask("Tâche 1", Priorite.NORMALE, debut, fin);
        t2 = TaskFactory.creerTask("Tâche 2", Priorite.HAUTE, debut, fin);
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

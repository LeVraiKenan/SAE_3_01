import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import tribollojfx.demo.*;

class TestTxtTaskRepository {

    private final TxtTaskRepository repo = new TxtTaskRepository();
    private final File file = new File("tasks.txt");

    @AfterEach
    void cleanFile() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSaveAllEtLoadAllAvecPlusieursTaches() {
        List<Task> toSave = new ArrayList<>();
        Task t1 = new Task("T1", "Desc1", Priorite.NORMALE,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0));
        t1.changerStatut(Statut.EN_COURS);

        Task t2 = new Task("T2", "Desc2", Priorite.HAUTE,
                LocalDateTime.of(2024, 2, 1, 10, 0),
                LocalDateTime.of(2024, 2, 2, 10, 0));
        t2.changerStatut(Statut.ARCHIVEE);

        toSave.add(t1);
        toSave.add(t2);

        repo.saveAll(toSave);

        assertTrue(file.exists());

        List<Task> loaded = repo.loadAll();
        assertEquals(2, loaded.size());

        Task lt1 = loaded.get(0);
        Task lt2 = loaded.get(1);

        assertEquals("T1", lt1.getTitre());
        assertEquals("Desc1", lt1.getDescription());
        assertEquals(Priorite.NORMALE, lt1.getPriorite());
        assertEquals(Statut.EN_COURS, lt1.getStatut());

        assertEquals("T2", lt2.getTitre());
        assertEquals("Desc2", lt2.getDescription());
        assertEquals(Priorite.HAUTE, lt2.getPriorite());
        assertEquals(Statut.ARCHIVEE, lt2.getStatut());
    }

    @Test
    void testLoadAllAvecFichierInexistantRenvoieListeVide() {
        if (file.exists()) {
            file.delete();
        }
        List<Task> loaded = repo.loadAll();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }
}

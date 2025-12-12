import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import tribollojfx.demo.*;

class TestTaskModel {

    private TaskModel model;

    @BeforeEach
    void setUp() {
        model = new TaskModel(); // chargera éventuellement des tâches depuis le fichier
        model.getTaches().clear(); // on part d’une liste vide pour un test déterministe
    }

    @Test
    void testAjouterTaskModifieLaListeEtNotifie() {
        AtomicBoolean notifie = new AtomicBoolean(false);
        model.addObserver(tasks -> notifie.set(true));

        Task t = TaskFactory.creerTask("T1", Priorite.NORMALE);
        model.ajouterTask(t);

        assertTrue(model.getTaches().contains(t));
        assertEquals(1, model.getTaches().size());
        assertTrue(notifie.get());
    }

    @Test
    void testSupprimerTaskModifieLaListeEtNotifie() {
        Task t1 = TaskFactory.creerTask("T1", Priorite.NORMALE);
        Task t2 = TaskFactory.creerTask("T2", Priorite.BASSE);
        model.ajouterTask(t1);
        model.ajouterTask(t2);

        AtomicInteger notifications = new AtomicInteger(0);
        model.addObserver(tasks -> notifications.incrementAndGet());

        model.supprimerTask(t1);

        assertFalse(model.getTaches().contains(t1));
        assertTrue(model.getTaches().contains(t2));
        assertEquals(1, model.getTaches().size());
        assertTrue(notifications.get() >= 1);
    }

    @Test
    void testNotifierAppelleLesObservateurs() {
        AtomicInteger notifications = new AtomicInteger(0);
        model.addObserver(tasks -> notifications.incrementAndGet());

        model.notifier();
        assertEquals(1, notifications.get());
    }
}

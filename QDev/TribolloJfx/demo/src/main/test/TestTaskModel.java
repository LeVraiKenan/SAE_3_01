import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tribollojfx.demo.*;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TestTaskModel {

    private TaskModel model;

    @BeforeEach
    void setUp() {
        model = new TaskModel();
        model.getTaches().clear();
    }

    @Test
    void testAjouterTaskModifieLaListeEtNotifie() {
        AtomicBoolean notifie = new AtomicBoolean(false);
        model.addObserver(tasks -> notifie.set(true));

        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);
        Task t = new Task("T1", "Desc", Priorite.NORMALE, debut, fin);

        model.ajouterTask(t);

        assertTrue(model.getTaches().contains(t));
        assertEquals(1, model.getTaches().size());
        assertTrue(notifie.get());
    }

    @Test
    void testSupprimerTaskModifieLaListeEtNotifie() {
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = debut.plusDays(1);

        Task t1 = new Task("T1", "Desc1", Priorite.NORMALE, debut, fin);
        Task t2 = new Task("T2", "Desc2", Priorite.BASSE, debut, fin);

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

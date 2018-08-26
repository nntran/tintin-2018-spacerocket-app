package fr.sqli.tintinspacerocketapp.game;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SimonGameTest {

    @Test
    public void testGenerateGamerId() {
        final HashSet<Integer> gamersIdsSet = new HashSet<>();

        assertEquals(0, SimonGame.generateGamerId(gamersIdsSet));

        gamersIdsSet.add(-1);

        assertEquals(0, SimonGame.generateGamerId(gamersIdsSet));

        gamersIdsSet.add(15);
        gamersIdsSet.add(0);
        gamersIdsSet.add(1);

        assertEquals(16, SimonGame.generateGamerId(gamersIdsSet));
    }
}

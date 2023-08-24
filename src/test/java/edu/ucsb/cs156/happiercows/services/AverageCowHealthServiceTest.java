package edu.ucsb.cs156.happiercows.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;

public class AverageCowHealthServiceTest {

    @InjectMocks
    AverageCowHealthService averageCowHealthService;

    @Mock
    UserCommonsRepository userCommonsRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAverageCowHealth_SomeCows() {
        UserCommons commons1 = mock(UserCommons.class);
        when(commons1.getCowHealth()).thenReturn(90.0);
        when(commons1.getNumOfCows()).thenReturn(2);

        UserCommons commons2 = mock(UserCommons.class);
        when(commons2.getCowHealth()).thenReturn(80.0);
        when(commons2.getNumOfCows()).thenReturn(3);

        when(userCommonsRepository.findByCommonsId(1L)).thenReturn(Arrays.asList(commons1, commons2));
        Double expectedAverage = ((90.0 * 2) + (80.0 * 3)) / (2 + 3);
        Double actualAverage = averageCowHealthService.getAverageCowHealth(1L);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    public void testGetAverageCowHealth_NoUserCommons() {
        when(userCommonsRepository.findByCommonsId(1L)).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> averageCowHealthService.getAverageCowHealth(1L));
    }

    @Test
    public void testGetAverageCowHealth_ZeroHealthNonZeroCows() {
        UserCommons commons1 = mock(UserCommons.class);
        when(commons1.getCowHealth()).thenReturn(0.0);
        when(commons1.getNumOfCows()).thenReturn(3);

        UserCommons commons2 = mock(UserCommons.class);
        when(commons2.getCowHealth()).thenReturn(90.0);
        when(commons2.getNumOfCows()).thenReturn(2);

        when(userCommonsRepository.findByCommonsId(1L)).thenReturn(Arrays.asList(commons1, commons2));
        Double expectedAverage = ((0.0 * 3) + (90.0 * 2)) / (3 + 2);
        Double actualAverage = averageCowHealthService.getAverageCowHealth(1L);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    public void testGetAverageCowHealth_PositiveHealthZeroCows() {
        UserCommons commons1 = mock(UserCommons.class);
        when(commons1.getCowHealth()).thenReturn(90.0);
        when(commons1.getNumOfCows()).thenReturn(0);

        UserCommons commons2 = mock(UserCommons.class);
        when(commons2.getCowHealth()).thenReturn(80.0);
        when(commons2.getNumOfCows()).thenReturn(3);

        when(userCommonsRepository.findByCommonsId(1L)).thenReturn(Arrays.asList(commons1, commons2));
        Double expectedAverage = (80.0 * 3) / 3;
        Double actualAverage = averageCowHealthService.getAverageCowHealth(1L);
        assertEquals(expectedAverage, actualAverage, 0.001);
    }

    @Test
    public void testGetAverageCowHealth_AllCowsZero() {
        UserCommons commons1 = mock(UserCommons.class);
        when(commons1.getCowHealth()).thenReturn(90.0);
        when(commons1.getNumOfCows()).thenReturn(0);

        UserCommons commons2 = mock(UserCommons.class);
        when(commons2.getCowHealth()).thenReturn(80.0);
        when(commons2.getNumOfCows()).thenReturn(0);

        when(userCommonsRepository.findByCommonsId(1L)).thenReturn(Arrays.asList(commons1, commons2));
        Double actualAverage = averageCowHealthService.getAverageCowHealth(1L);
        assertEquals(0.0, actualAverage, 0.001);
    }
}

package edu.ucsb.cs156.happiercows.jobs;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;

public class CommonStatsJobFactoryTests {

    @InjectMocks
    private CommonStatsJobFactory factory;

    @Mock
    private AverageCowHealthService averageCowHealthService;

    @Mock
    private CommonStatsRepository commonStatsRepository;

    @Mock
    private UserCommonsRepository userCommonsRepository;

    @Mock
    private CommonsRepository commonsRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate_returnsNonNullJobContextConsumer() {
        JobContextConsumer consumer = factory.create();
        assertNotNull(consumer);
        assertTrue(consumer instanceof CommonStatsJob);
    }
}

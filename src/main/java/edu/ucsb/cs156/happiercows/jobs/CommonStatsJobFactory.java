package edu.ucsb.cs156.happiercows.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;

@Service
public class CommonStatsJobFactory {

    private final AverageCowHealthService averageCowHealthService;
    private final CommonStatsRepository commonStatsRepository;
    private final UserCommonsRepository userCommonsRepository;
    private final CommonsRepository commonsRepository;

    @Autowired
    public CommonStatsJobFactory(
            AverageCowHealthService averageCowHealthService,
            CommonStatsRepository commonStatsRepository,
            UserCommonsRepository userCommonsRepository,
            CommonsRepository commonsRepository) {
        this.averageCowHealthService = averageCowHealthService;
        this.commonStatsRepository = commonStatsRepository;
        this.userCommonsRepository = userCommonsRepository;
        this.commonsRepository = commonsRepository;
    }

    public JobContextConsumer create() {
        return new CommonStatsJob(averageCowHealthService, commonStatsRepository, userCommonsRepository, commonsRepository);
    }
}

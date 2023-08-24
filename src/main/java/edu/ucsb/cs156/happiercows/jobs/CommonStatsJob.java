package edu.ucsb.cs156.happiercows.jobs;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.entities.CommonStats;
import edu.ucsb.cs156.happiercows.repositories.CommonStatsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.services.AverageCowHealthService;
import edu.ucsb.cs156.happiercows.services.jobs.JobContext;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonStatsJob implements JobContextConsumer {

    private final AverageCowHealthService averageCowHealthService;
    private final CommonStatsRepository commonStatsRepository;
    private final UserCommonsRepository userCommonsRepository;
    private final CommonsRepository commonsRepository;

    @Override
    public void accept(JobContext ctx) {
        ctx.log(message:"Starting to record common stats.");
        Iterable<Commons> allCommons = commonsRepository.findAll();

        for (Commons commons : allCommons) {
                recordCommonStatsForCommon(ctx, commons);
        }
    }

    private void recordCommonStatsForCommon(JobContext ctx, Commons commons) {
        Long commonId = commons.getId();
        ctx.log("Starting to record the common stats for commons: " + commons.getName());

        Double avgHealth = averageCowHealthService.getAverageCowHealth(commonId);
        Iterable<UserCommons> userCommonsList = userCommonsRepository.findByCommonsId(commonId);

        int numCows = 0;
        for (UserCommons userCommons : userCommonsList) {
            numCows += userCommons.getNumOfCows();
        }

        CommonStats stats = CommonStats.builder()
            .commonsId(commonId)
            .numCows(numCows)
            .avgHealth(avgHealth)
            .timestamp(LocalDateTime.now())
            .build();

        commonStatsRepository.save(stats);
        ctx.log("Saved stats for commons: " + commons.getName());
    }
}

package edu.ucsb.cs156.happiercows.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;

@Service
public class AverageCowHealthService {

    @Autowired
    private UserCommonsRepository userCommonsRepository;

    public Double getAverageCowHealth(Long commonsId) {
        Iterable<UserCommons> userCommonsList = userCommonsRepository.findByCommonsId(commonsId);
        Double totalHealth = 0.0;
        int totalCows = 0;

        for(UserCommons userCommons: userCommonsList){
            totalHealth += userCommons.getCowHealth() * userCommons.getNumOfCows();
            totalCows += userCommons.getNumOfCows();
        }

        if(!userCommonsList.iterator().hasNext()){
            throw new IllegalArgumentException("Unable to get average cow health");
        }

        return totalCows == 0 ? 0 :totalHealth / totalCows;
    }
}

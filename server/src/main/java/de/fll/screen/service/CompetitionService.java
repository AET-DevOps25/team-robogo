package de.fll.screen.service;

import de.fll.screen.model.Competition;
import de.fll.screen.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    public Optional<Competition> getCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public Competition saveCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }

    public void deleteCompetition(Long id) {
        competitionRepository.deleteById(id);
    }
} 
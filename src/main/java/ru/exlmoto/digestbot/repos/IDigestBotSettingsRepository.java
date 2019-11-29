package ru.exlmoto.digestbot.repos;

import org.springframework.data.repository.CrudRepository;

import ru.exlmoto.digestbot.entities.DigestBotSettings;

public interface IDigestBotSettingsRepository extends CrudRepository<DigestBotSettings, Integer> {
    public DigestBotSettings findDigestBotSettingsById(final Integer aId);
}

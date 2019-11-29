package ru.exlmoto.digestbot.entities;

import javax.persistence.*;

@Entity
@Table(name = "digestbot_settings")
public class DigestBotSettings {
    @Id
    private final Integer id = 1;

    private Boolean show_log_updates;
    private Boolean use_file_loader;
    private Boolean use_stack_delay;
    private Boolean show_greetings;
    private Boolean mute;

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getShow_log_updates() {
        return show_log_updates;
    }

    public void setShow_log_updates(Boolean show_log_updates) {
        this.show_log_updates = show_log_updates;
    }

    public Boolean getUse_file_loader() {
        return use_file_loader;
    }

    public void setUse_file_loader(Boolean use_file_loader) {
        this.use_file_loader = use_file_loader;
    }

    public Boolean getUse_stack_delay() {
        return use_stack_delay;
    }

    public void setUse_stack_delay(Boolean use_stack_delay) {
        this.use_stack_delay = use_stack_delay;
    }

    public Boolean getShow_greetings() {
        return show_greetings;
    }

    public void setShow_greetings(Boolean show_greetings) {
        this.show_greetings = show_greetings;
    }
}

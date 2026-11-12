package com.logistpulse.command;

public interface Command {
    String name();
    void execute();
    void undo();          // compensación (inverso del comando)
    String describe();
}

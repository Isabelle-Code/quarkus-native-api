package yacco.tech.terminal.model;

public class TerminalDTO {
    private final String id;
    private final String serial;
    private final String model;
    private final TerminalStatus status;

    private TerminalDTO(String id, String serial, String model, TerminalStatus status) {
        this.id = id;
        this.serial = serial;
        this.model = model;
        this.status = status;
    }

    public static TerminalDTO fromEntity(Terminal terminal) {
        return new TerminalDTO(
                terminal.id.toString(),
                terminal.serial,
                terminal.model,
                terminal.status
        );
    }

    public String getId() {
        return id;
    }

    public String getSerial() {
        return serial;
    }

    public String getModel() {
        return model;
    }

    public TerminalStatus getStatus() {
        return status;
    }
}

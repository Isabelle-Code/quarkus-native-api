package yacco.tech.terminal.model;

public class CreateTerminalDTO {
    private String serial;
    private String model;

    public CreateTerminalDTO() {
    }

    public CreateTerminalDTO(String serial, String model) {
        this.serial = serial;
        this.model = model;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

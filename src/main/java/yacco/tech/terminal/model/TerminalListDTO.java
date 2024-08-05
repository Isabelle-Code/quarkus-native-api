package yacco.tech.terminal.model;

import java.util.List;

public class TerminalListDTO {
    private List<TerminalDTO> data;

    public TerminalListDTO(List<TerminalDTO> data) {
        this.data = data;
    }

    public List<TerminalDTO> getData() {
        return data;
    }

    public void setData(List<TerminalDTO> data) {
        this.data = data;
    }
}

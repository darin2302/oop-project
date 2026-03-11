package bg.warehouse.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "warehouse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Warehouse {

    @XmlElementWrapper(name = "batches")
    @XmlElement(name = "batch")
    private List<Batch> batches = new ArrayList<>();

    @XmlElementWrapper(name = "log")
    @XmlElement(name = "entry")
    private List<LogEntry> logEntries = new ArrayList<>();

    public Warehouse() {
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }

    public List<LogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }
}

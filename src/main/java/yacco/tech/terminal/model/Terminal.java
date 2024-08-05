package yacco.tech.terminal.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;

import java.util.Optional;

@MongoEntity(collection = "terminals")
public class Terminal extends ReactivePanacheMongoEntity {

    public String serial = "";

    public String model = "";

    public TerminalStatus status = TerminalStatus.BLOCKED;

    public static Uni<Optional<Terminal>> findBySerialOptional(String serial) {
        return Terminal.find("serial", serial).firstResultOptional();
    }
}

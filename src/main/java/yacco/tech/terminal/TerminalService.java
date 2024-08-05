package yacco.tech.terminal;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import yacco.tech.ResourceAlreadyExistsException;
import yacco.tech.ResourceDoesNotExistsException;
import yacco.tech.terminal.model.Terminal;
import yacco.tech.terminal.model.TerminalStatus;

import java.util.Optional;

@ApplicationScoped
public class TerminalService {

    private static final Logger LOG = Logger.getLogger(TerminalService.class);

    public Uni<Terminal> createTerminal(
            String serial,
            String model
    ) {
        return Terminal
                .findBySerialOptional(serial)
                .map(Unchecked.function(optionalTerminal -> {

                    if (optionalTerminal.isPresent()) {
                        LOG.warn("Terminal with serial " + serial + " already exists!");
                        throw new ResourceAlreadyExistsException("Terminal with serial " + serial + " already exists");
                    }

                    Terminal toPersist = new Terminal();
                    toPersist.model = model;
                    toPersist.serial = serial;
                    toPersist.status = TerminalStatus.BLOCKED;

                    return toPersist;
                }))
                .flatMap(toPersist -> toPersist.persist().map(t -> (Terminal) t))
                .onItem()
                .invoke(savedTerminal -> LOG.infof("Created terminal with id %s", savedTerminal.id));
    }

    public Multi<Terminal> listAll() {
        return Terminal.streamAll();
    }

    public Uni<Optional<Terminal>> findById(String id) {
        return
                Terminal.findById(new ObjectId(id))
                        .map(Terminal.class::cast)
                        .map(Optional::ofNullable);
    }

    public Uni<Optional<Terminal>> findBySerial(String serial) {
        return
                Terminal.findBySerialOptional(serial);
    }

    public Uni<Terminal> deleteById(String id) {
        return
                findOneOrThrow(id)
                        .flatMap((term -> term.delete()
                                .onItem()
                                .transform(ignore -> {
                                    LOG.infof("Deleted terminal with id %s", id);
                                    return term;
                                })));
    }

    public Uni<Terminal> updateById(String id, TerminalStatus status) {
        return
                findOneOrThrow(id)
                        .flatMap((term -> {
                            term.status = status;
                            return term
                                    .update()
                                    .map(t -> {
                                        LOG.infof("Updated terminal with id %s", id);
                                        return (Terminal) t;
                                    });
                        }));
    }

    private Uni<Terminal> findOneOrThrow(String id) {
        return
                findById(id)
                        .map(Unchecked.function(optTerm -> {
                            if (optTerm.isEmpty()) {
                                LOG.warn("Terminal with id " + id + " does not exist!");
                                throw new ResourceDoesNotExistsException("Terminal with id " + id + " does not exist");
                            }

                            return optTerm.get();

                        }));
    }
}

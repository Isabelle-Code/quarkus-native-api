package yacco.tech.terminal;


import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeleteTerminalConsumer {

    private final static Logger LOG = Logger.getLogger(DeleteTerminalConsumer.class.getName());

    private final TerminalService service;

    public DeleteTerminalConsumer(TerminalService service) {
        this.service = service;
    }

    @Incoming("delete-terminal")
    @Transactional
    public Uni<Void> consumeTerminalDeletedEvent(ConsumerRecord<String, String> record) {
        String terminalId = record.value();
        LOG.info("Event to delete terminal with id " + terminalId);
        return service
                .deleteById(terminalId)
                .replaceWithVoid();
    }
}

package yacco.tech.terminal;


import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import yacco.tech.terminal.model.CreateTerminalDTO;

@ApplicationScoped
public class CreateTerminalConsumer {

    private final static Logger LOG = Logger.getLogger(CreateTerminalConsumer.class.getName());

    private final TerminalService service;

    public CreateTerminalConsumer(TerminalService service) {
        this.service = service;
    }

    @Incoming("create-terminal")
    @Transactional
    public Uni<Void> consumeTerminalCreatedEvent(ConsumerRecord<String, CreateTerminalDTO> record) {
        CreateTerminalDTO dto = record.value();
        LOG.info("Event to create terminal with serial number " + dto.getSerial());
        return service
                .createTerminal(dto.getSerial(), dto.getModel())
                .replaceWithVoid();

    }
}

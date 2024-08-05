package yacco.tech.terminal;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import yacco.tech.terminal.model.CreateTerminalDTO;
import yacco.tech.terminal.model.Terminal;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class CreateTerminalConsumerTest {

    @Inject
    CreateTerminalConsumer createTerminalConsumer;

    @Inject
    TerminalService service;

    @Test
    public void shouldConsumeTerminalCreatedEvent() throws ExecutionException, InterruptedException {
        // arrange
        CreateTerminalDTO dto = new CreateTerminalDTO();
        dto.setSerial("integration-test-create-serial-1");
        dto.setModel("integration-test-create-model-1");

        ConsumerRecord<String, CreateTerminalDTO> consumerRecord = new ConsumerRecord<>("create-terminal", 0, 0, null, dto);

        CompletableFuture<Void> future = createTerminalConsumer.consumeTerminalCreatedEvent(consumerRecord).subscribeAsCompletionStage();
        future.get();

        // assert
        Optional<Terminal> optTerm = service.findBySerial("integration-test-create-serial-1").await().indefinitely();

        Terminal term = optTerm.orElse(null);

        assertNotNull(term);
        assertEquals("integration-test-create-serial-1", term.serial);
        assertEquals("integration-test-create-model-1", term.model);

        // cleanup
        service.deleteById(term.id.toString()).await().indefinitely();

    }
}
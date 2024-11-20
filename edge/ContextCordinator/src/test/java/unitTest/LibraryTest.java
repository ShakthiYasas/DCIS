package unitTest;

import org.dcis.ContextCordinator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    @Test void healthCheck() {
        assertSame(ContextCordinator.health(), "Ping from the library.");
    }
}

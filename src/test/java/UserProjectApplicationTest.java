import com.lucasteixeira.bank.UserProjectApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = UserProjectApplication.class)
@ActiveProfiles("test")
public class UserProjectApplicationTest {

    @Test
    void contextLoads() {
    }
}

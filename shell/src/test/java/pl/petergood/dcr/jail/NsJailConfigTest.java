package pl.petergood.dcr.jail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class NsJailConfigTest {

    private static boolean isWindows = true;

    @BeforeAll
    public static void detectOS() {
        isWindows = System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void verifyFlagsAreGenerated() {
        // given
        NsJailConfig config = new NsJailConfig.Builder()
                .setConfig("/usr/share/config.cfg")
                .setLogFile("/usr/share/logs.log")
                .setHostJailPath(new File("/usr/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
                .build();

        // when
        String commandFlags = config.getCommandFlags();

        // then
        String jailPath = isWindows ? "C:\\usr\\nsjail\\jail" : "/usr/nsjail/jail";
        Assertions.assertThat(commandFlags).isEqualTo("--config /usr/share/config.cfg --cwd " + jailPath + " --log /usr/share/logs.log --bindmount " + jailPath);
    }

    @Test
    public void verifyRepeatedFlagsAreGenerated() {
        // given
        NsJailConfig config = new NsJailConfig.Builder()
                .readOnlyMount("/usr")
                .readOnlyMount("/bin")
                .setHostJailPath(new File("/usr/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_ONLY)
                .build();

        // when
        String commandFlags = config.getCommandFlags();

        // then
        String jailPath = isWindows ? "C:\\usr\\nsjail\\jail" : "/usr/nsjail/jail";
        Assertions.assertThat(commandFlags).isEqualTo("--cwd " + jailPath + " --bindmount_ro /usr --bindmount_ro /bin --bindmount_ro " + jailPath);
    }

    @Test
    public void verifyExcludedFlagsAreNotGenerated() {
        // given
        NsJailConfig config = new NsJailConfig.Builder()
                .readOnlyMount("/usr")
                .readOnlyMount("/bin")
                .setHostJailPath(new File("/usr/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_ONLY)
                .setLogFile("/usr/nsjail/jail.log")
                .build();

        // when
        String commandFlags = config.getCommandFlags("log");

        // then
        String jailPath = isWindows ? "C:\\usr\\nsjail\\jail" : "/usr/nsjail/jail";
        Assertions.assertThat(commandFlags).isEqualTo("--cwd " + jailPath + " --bindmount_ro /usr --bindmount_ro /bin --bindmount_ro " + jailPath);
    }

}

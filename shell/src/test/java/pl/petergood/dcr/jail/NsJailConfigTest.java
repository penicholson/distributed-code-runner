package pl.petergood.dcr.jail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NsJailConfigTest {

    @Test
    public void verifyFlagsAreGenerated() {
        // given
        NsJailConfig config = new NsJailConfig.Builder()
                .setConfig("/usr/share/config.cfg")
                .setLogFile("/usr/share/logs.log")
                .build();

        // when
        String commandFlags = config.getCommandFlags();

        // then
        Assertions.assertThat(commandFlags).isEqualTo("--log /usr/share/logs.log --config /usr/share/config.cfg");
    }

}

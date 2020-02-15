package pl.petergood.dcr.jail;

import org.junit.jupiter.api.Test;
import pl.petergood.dcr.shell.TerminalInteractor;

import static org.mockito.Mockito.*;

public class NsJailTest {

    @Test
    public void verifyNsJailIsCalled() {
        // given
        TerminalInteractor terminalInteractor = mock(TerminalInteractor.class);
        NsJailConfig jailConfig = new NsJailConfig.Builder()
                .setConfig("/usr/share/config.cfg")
                .build();
        Jail jail = new NsJail(jailConfig, terminalInteractor);

        // when
        jail.executeInJail(new String[] { "echo", "hello" });

        // then
        verify(terminalInteractor, times(1)).exec(new String[] { "nsjail", "--config /usr/share/config.cfg", "--", "echo", "hello" });
    }

}

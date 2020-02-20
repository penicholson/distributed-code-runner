package pl.petergood.dcr.runnerworker.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.file.FileInteractor;
import pl.petergood.dcr.file.FileSystemFileInteractor;
import pl.petergood.dcr.shell.ShellTerminalInteractor;
import pl.petergood.dcr.shell.TerminalInteractor;

@Configuration
public class SimpleRunnerWorkerConfiguration {

    @Bean
    public TerminalInteractor getTerminalInteractor() {
        return new ShellTerminalInteractor();
    }

    @Bean
    public FileInteractor getFileInteractor() {
        return new FileSystemFileInteractor();
    }

}

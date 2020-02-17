package pl.petergood.dcr.compilationworker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.petergood.dcr.jail.NsJailConfig;
import pl.petergood.dcr.jail.NsJailDirectoryMode;
import pl.petergood.dcr.shell.ShellTerminalInteractor;
import pl.petergood.dcr.shell.TerminalInteractor;

import java.io.File;

@Configuration
public class CompilationWorkerConfig {

    @Value("${dcr.compilationworker.jail.configuration.path}")
    private String jailConfigurationFilePath;

    @Bean
    public TerminalInteractor terminalInteractor() {
        return new ShellTerminalInteractor();
    }

    @Bean
    public NsJailConfig jailConfig() {
        return new NsJailConfig.Builder()
                .setConfig(jailConfigurationFilePath)
                .setHostJailPath(new File("/nsjail"))
                .setJailDirectoryName("jail", NsJailDirectoryMode.READ_WRITE)
                .build();
    }

}
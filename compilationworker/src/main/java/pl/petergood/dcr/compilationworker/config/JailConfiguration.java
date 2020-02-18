package pl.petergood.dcr.compilationworker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class JailConfiguration {

    private File jailRootPath;
    private String jailConfigurationPath;

    public JailConfiguration(@Value("${dcr.compilationworker.jail.root.path}") String jailRootPath,
                             @Value("${dcr.compilationworker.jail.configuration.path}") String jailConfigurationPath) {
        this.jailRootPath = new File(jailRootPath);
        this.jailConfigurationPath = jailConfigurationPath;
    }

    public File getJailRootPath() {
        return jailRootPath;
    }

    public String getJailConfigurationPath() {
        return jailConfigurationPath;
    }
}

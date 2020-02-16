package pl.petergood.dcr.jail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NsJailErrorDetectorTest {

    @Test
    public void verifyErrorDetectedInLogs() {
        // given
        String rawLogs = "[I][2020-02-16T16:26:24+0000] Gid map: inside_gid:0 outside_gid:0 count:1 newgidmap:false\n" +
                "[W][2020-02-16T16:26:24+0000][262] void cmdline::logParams(nsjconf_t*)():264 Process will be GID/EGID=0 in the global user namespace, and will have group root-level access to files\n" +
                "[E][2020-02-16T16:26:24+0000] some random error\n" +
                "[I][2020-02-16T16:26:24+0000] Executing '/usr/bin/touch' for '[STANDALONE MODE]'\n" +
                "[I][2020-02-16T16:26:24+0000] pid=263 ([STANDALONE MODE]) exited with status: 0, (PIDs left: 0)";
        NsJailErrorDetector errorDetector = new NsJailErrorDetector();

        // when
        boolean result = errorDetector.isErrorPresent(rawLogs);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void verifyErrorIsNotDetected() {
        // given
        String rawLogs = "[I][2020-02-16T16:26:24+0000] Mode: STANDALONE_ONCE\n" +
                "[I][2020-02-16T16:26:24+0000] Jail parameters: hostname:'NSJAIL', chroot:'', process:'/usr/bin/touch', bind:[::]:0, max_conns_per_ip:0, time_limit:600, personality:0, daemonize:false, clone_newnet:true, clone_newuser:true, clone_newns:true, clone_newpid:true, clone_newipc:true, clone_newuts:true, clone_newcgroup:true, keep_caps:false, disable_no_new_privs:false, max_cpus:0\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/' flags:MS_RDONLY type:'tmpfs' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/bin' -> '/bin' flags:MS_RDONLY|MS_BIND|MS_REC|MS_PRIVATE type:'' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/lib' -> '/lib' flags:MS_RDONLY|MS_BIND|MS_REC|MS_PRIVATE type:'' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/lib64' -> '/lib64' flags:MS_RDONLY|MS_BIND|MS_REC|MS_PRIVATE type:'' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/usr' -> '/usr' flags:MS_RDONLY|MS_BIND|MS_REC|MS_PRIVATE type:'' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Mount: '/nsjail/jail' -> '/nsjail/jail' flags:MS_BIND|MS_REC|MS_PRIVATE type:'' options:'' dir:true\n" +
                "[I][2020-02-16T16:26:24+0000] Uid map: inside_uid:0 outside_uid:0 count:1 newuidmap:false";
        NsJailErrorDetector errorDetector = new NsJailErrorDetector();

        // when
        boolean result = errorDetector.isErrorPresent(rawLogs);

        // then
        Assertions.assertThat(result).isFalse();
    }

}

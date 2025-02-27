package com.coyotesong.database.containers;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.zip.Checksum;

/**
 * TestContainer for SAP HANA Express databases
 *
 * Note: URL could contain 'failOver'.
 *
 *    // https://hub.docker.com/r/saplabs/hanaexpress
 *    // https://www.sap.com/docs/download/cmp/2016/06/sap-hana-express-dev-agmt-and-exhibit.pdf
 *
 * error: "ERROR: A URL for retrieving the SYSTEM user passwords MUST be provided via --passwords-url or --master-password."
 *
 * Requirements in /etc/sysctl.conf file:
 *   fs.file-max=20000000
 *   fs.aio-max-nr=262144
 *   vm.memory_failure_early_kill=1
 *   vm.max_map_count=135217728
 *   net.ipv4.ip_local_port_range=40000 60999
 *
 * Docker command line:
 * sudo docker run -p 39013:39013 -p 39017:39017 -p 39041-39045:39041-39045 -p 1128-1129:1128-1129 -p 59013-59014:59013-59014 -v /data/<directory_name>:/hana/mounts \
 * --ulimit nofile=1048576:1048576 \
 * --sysctl kernel.shmmax=1073741824 \
 * --sysctl net.ipv4.ip_local_port_range='40000 60999' \
 * --sysctl kernel.shmmni=524288 \
 * --sysctl kernel.shmall=8388608 \
 * --name <container_name> \
 * store/saplabs/hanaexpress:<tag> \
 * --passwords-url <file://<path_to_json_file> OR http/https://<url_to_json_file>> \
 * --agree-to-sap-license
 *
 * sudo docker run store/saplabs/hanaexpress:<tag> -h
 * usage: [options]
 * --dont-check-consistency Skip consistency check between mount points
 * --dont-check-mount-points Skip check for allowed mount points
 * --dont-check-version Skip compatibility check of current and last HANA version
 * --dont-check-system Skip check for incompatible /proc/sys values
 * --dont-exit-on-error Halt script on error to allow root cause analysis
 *
 * Checking syscalls ...
 * 	WARNING: Operation not permitted: move_pages
 * 	WARNING: Operation not permitted: mbind
 * Check failed: syscalls
 * Please add permissions for the named operations by whitelisting them in a seccomp profile.
 * You can get a matching profile by calling 'docker run --rm <hana_image> --print seccomp.json > seccomp.json' and apply it to the container start via 'docker run --security-opt seccomp=seccomp.json ...'.
 *
 *
 * @param <SELF>
 */
public class SapHanaContainer<SELF extends SapHanaContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    private static final Logger LOG = LoggerFactory.getLogger(SapHanaContainer.class);

    public static final String NAME = "SAP HANA";
    public static final String IMAGE = "saplabs/hanaexpress";
    public static final String DEFAULT_TAG = "2.00.061.00.20220519.1";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse(IMAGE);

    private static final int INSTANCE_ID = 90;
    // ports are for systemdb and first tenant.
    private static final Integer[] SAP_HANA_PORTS = { 30017 + 100 * INSTANCE_ID, 30041 + 100 * INSTANCE_ID };

    // SYSTEM/manager ?
    static final String DEFAULT_TENANT_DATABASE = "HXE";
    static final String DEFAULT_USER = "hxeadm";
    static final String DEFAULT_PASSWORD = "HXEHana1";

    private final String databaseName = DEFAULT_TENANT_DATABASE;
    private final String username = DEFAULT_USER;
    private final String password = DEFAULT_PASSWORD;

    public SapHanaContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public SapHanaContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        final String passwordUrl = "/hana/mounts/password.json";

        this.withCopyToContainer(Transferable.of("{ \"master_password\": \"" + password + "\" }"), passwordUrl);
        this.waitStrategy = new LogMessageWaitStrategy().withRegEx(".*Startup finished!.*\\s")
                        .withStartupTimeout(Duration.ofMinutes(5));
        // LOG.info("command parts: {}", String.join(",", this.getCommandParts()));
        this.setCommand("--passwords-url file://" + passwordUrl + " --agree-to-sap-license");

        this.addExposedPort(8000 + INSTANCE_ID);
        this.addExposedPort(50013 + 100 * INSTANCE_ID);
        for (Integer port : SAP_HANA_PORTS) {
            this.addExposedPort(port);
        }

        this.withLogConsumer(new MyLogConsumer(dockerImageName, MyLogConsumer.LoggingLevel.ALL));
    }

    @Override
    protected void configure() {
        // see https://help.sap.com/docs/SAP_HANA_PLATFORM/0eec0d68141541d1b07893a39944924e/109397c2206a4ab2a5386d494f4cf75e.html
        if (StringUtils.isNotBlank(databaseName)) {
            urlParameters.put("databaseName", databaseName);
        }

        urlParameters.put("user", username);
        // urlParameters.put("user", "SYSTEM");
        if (StringUtils.isNotBlank(password)) {
            urlParameters.put("password", password);
        }
        //
        // autocommit
    }

    @Override
    public String getDriverClassName() {
        return "com.sap.db.jdbc.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:sap://" + getHost() + ":" + getMappedPort(SAP_HANA_PORTS[1]) + "/" + additionalUrlParams;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1 FROM dummy";
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    public int getInstanceId() {
        return INSTANCE_ID;
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}

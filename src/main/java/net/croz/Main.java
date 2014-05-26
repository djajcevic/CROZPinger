package net.croz;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String CONF = "-conf";
    public static final String HOST = "-host";
    public static final String TIMEOUT = "-to";

    public static void main(String[] args) throws IOException {

        Resolver.setLogEnabled(true);
        boolean resolve = Resolver.resolve("esb-test.iskon.hr", "22", 7);

        List<String> address = new ArrayList<String>();

        CommandLine commandLine = new CommandLine();
        commandLine.saveFlagValue(CONF);
        commandLine.saveFlagValue(HOST);
        commandLine.saveFlagValue(TIMEOUT);
        commandLine.parse(args);


        if (commandLine.getNumberOfFlags() == 0) {
            String usageFormat = IOUtils.toString(Main.classpathResourceInputStream("/usage.txt"));
            System.out.printf(usageFormat, Resolver.DEFAULT_TIMEOUT);
            return;
        }

        String configurationFile = commandLine.getFlagValue(CONF);
        if (configurationFile != null && configurationFile.length() > 0) {
            loadHostsFromConfigurationFile(configurationFile, address);
        } else {
            if (commandLine.hasFlag(HOST)) {
                address.add(commandLine.getFlagValue(HOST));
            }
        }
        int countResolved = 0;
        List<String> failed = new ArrayList<String>();
        List<String> succeeded = new ArrayList<String>();
        for (String ip : address) {
            String[] strings = ip.split(":");
            boolean resolved;
            String toFlagValue = commandLine.getFlagValue("-to");
            int timeout = 0;
            try {
                timeout = Integer.parseInt(toFlagValue);
            } catch (Exception e) {

            }
            if (strings.length == 2) {
                resolved = Resolver.resolve(strings[0], strings[1], timeout);
            }
            else {
                resolved = Resolver.resolve(strings[0], null, timeout);
            }
            if (resolved) {
                countResolved++;
                succeeded.add(ip);
            }
            else {
                failed.add(ip);
            }
        }
        System.out.printf("\n\nResolved/connected to %d of %d endpoints.\n", countResolved, address.size());
        System.err.println("\nFailed endpoints: ");
        for (String endpoint : failed) {
            System.err.println("\t" + endpoint);
        }
        System.out.println("\nSucceeded endpoints: ");
        for (String endpoint : succeeded) {
            System.out.println("\t" + endpoint);
        }
    }

    public static InputStream classpathResourceInputStream(String fileName) {
        return Main.class.getResourceAsStream(fileName);
    }

    private static void loadHostsFromConfigurationFile(String configurationFile, List<String> address) throws IOException {
        File file = new File(configurationFile);
        if (file.exists() && file.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                address.add(line);
            }
        }
    }
}

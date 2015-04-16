package com.frostwire.bittorrent;

import java.io.File;

/**
 * @author gubatron
 * @author aldenml
 */
public final class BTContext {

    public File homeDir;
    public File torrentsDir;
    public File dataDir;
    public int port = 0;
    public int port1 = 0;
    public String iface;
    public boolean optimizeMemory;
}

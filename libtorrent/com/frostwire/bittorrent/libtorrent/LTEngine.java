/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.bittorrent.libtorrent;

import com.frostwire.bittorrent.BTDownload;
import com.frostwire.bittorrent.BTEngine;
import com.frostwire.bittorrent.BTEngineListener;
import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.frostwire.jlibtorrent.alerts.AlertType;
import com.frostwire.jlibtorrent.alerts.SaveResumeDataAlert;
import com.frostwire.jlibtorrent.alerts.TorrentAlert;
import com.frostwire.jlibtorrent.swig.entry;
import com.frostwire.logging.Logger;
import com.frostwire.util.OSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

/**
 * @author gubatron
 * @author aldenml
 */
public final class LTEngine implements BTEngine {

    private static final Logger LOG = Logger.getLogger(LTEngine.class);

    private final Session session;

    private File home;
    private BTEngineListener listener;

    private boolean isFirewalled;

    public LTEngine() {
        this.session = new Session();


        this.home = new File(".").getAbsoluteFile();

        addEngineListener();
    }

    private static class Loader {
        static LTEngine INSTANCE = new LTEngine();
    }

    public static LTEngine getInstance() {
        return Loader.INSTANCE;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public File getHome() {
        return home;
    }

    @Override
    public void setHome(File home) {
        this.home = home;
    }

    @Override
    public BTEngineListener getListener() {
        return listener;
    }

    @Override
    public void setListener(BTEngineListener listener) {
        this.listener = listener;
    }

    @Override
    public void download(File torrent, File saveDir) throws IOException {
        LTEngine e = LTEngine.getInstance();

        Session s = e.getSession();
        s.asyncAddTorrent(torrent, saveDir, null);
        saveResumeTorrent(torrent);
    }

    @Override
    public void restoreDownloads(File saveDir) {
        File[] torrents = home.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return FilenameUtils.getExtension(name).equals("torrent");
            }
        });

        for (File t : torrents) {
            try {
                File resumeFile = new File(home, FilenameUtils.getBaseName(t.getName()) + ".resume");
                session.asyncAddTorrent(t, saveDir, resumeFile);
            } catch (Throwable e) {
                LOG.error("Error restoring torrent download", e);
            }
        }
    }

    @Override
    public void start() {
        loadSettings();
    }

    @Override
    public void stop() {
        saveSettings();
// TODO:BITTORRENT
                    /*
                    if (AzureusStarter.isAzureusCoreStarted()) {
						LOG.debug("LifecycleManagerImpl.handleEvent - SHUTINGDOWN - Azureus core pauseDownloads()!");
						AzureusStarter.getAzureusCore().getGlobalManager().pauseDownloads();
						AzureusStarter.getAzureusCore().stop();
					}*/

        // TODO:BITTORRENT
        // see Session.abort()
        /*
        if (AzureusStarter.isAzureusCoreStarted()) {
            System.out.println("Waiting for Vuze core to shutdown...");
            AzureusStarter.getAzureusCore().stop();
            System.out.println("Vuze core shutdown.");
        }*/
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public boolean isFirewalled() {
        return isFirewalled;
    }

    @Override
    public long getDownloadRate() {
        return session.getStatus().getDownloadRate();
    }

    @Override
    public long getUploadRate() {
        return session.getStatus().getUploadRate();
    }

    @Override
    public long getTotalDownload() {
        return session.getStatus().getTotalDownload();
    }

    @Override
    public long getTotalUpload() {
        return session.getStatus().getTotalUpload();
    }

    @Override
    public int getDownloadRateLimit() {
        return session.getSettings().getDownloadRateLimit();
    }

    @Override
    public int getUploadRateLimit() {
        return session.getSettings().getDownloadRateLimit();
    }

    @Override
    public void revertToDefaultConfiguration() {
        if (OSUtils.isAndroid()) {
            // need to test
            session.setSettings(SessionSettings.newMinMemoryUsage());
        } else {
            session.setSettings(SessionSettings.newDefaults());
        }
    }

    @Override
    public List<BTDownload> getDownloads() {
        //session.
        return null;
    }

    private void addEngineListener() {
        session.addListener(new AlertListener() {
            @Override
            public boolean accept(Alert<?> alert) {
                return true;
            }

            @Override
            public void onAlert(Alert<?> alert) {
                //LOG.info(a.message());
                if (listener == null) {
                    return;
                }

                AlertType type = alert.getType();

                switch (type) {
                    case TORRENT_ADDED:
                        listener.downloadAdded(new LTDownload(((TorrentAlert<?>) alert).getTorrentHandle()));
                        break;
                    case SAVE_RESUME_DATA:
                        saveResumeData((SaveResumeDataAlert) alert);
                        break;
                    case BLOCK_FINISHED:
                        doResumeData((TorrentAlert<?>) alert);
                        break;
                    case PORTMAP:
                        isFirewalled = false;
                        break;
                    case PORTMAP_ERROR:
                        isFirewalled = true;
                        break;
                }
            }
        });
    }

    private void saveResumeTorrent(File torrent) {
        try {
            TorrentInfo ti = new TorrentInfo(torrent);
            byte[] arr = FileUtils.readFileToByteArray(torrent);
            entry e = entry.bdecode(Vectors.bytes2char_vector(arr));
            e.dict().set("torrent_orig_path", new entry(torrent.getAbsolutePath()));
            arr = Vectors.char_vector2bytes(e.bencode());
            FileUtils.writeByteArrayToFile(resumeTorrentFile(ti.getInfoHash().toString()), arr);
        } catch (Throwable e) {
            LOG.warn("Error saving resume torrent", e);
        }
    }

    private void saveResumeData(SaveResumeDataAlert alert) {
        try {
            TorrentHandle th = alert.getTorrentHandle();
            entry d = alert.getResumeData();
            byte[] arr = Vectors.char_vector2bytes(d.bencode());
            FileUtils.writeByteArrayToFile(resumeDataFile(th.getInfoHash().toString()), arr);
        } catch (Throwable e) {
            LOG.warn("Error saving resume data", e);
        }
    }

    private void doResumeData(TorrentAlert<?> alert) {
        TorrentHandle th = alert.getTorrentHandle();
        if (th.needSaveResumeData()) {
            th.saveResumeData();
        }
    }

    File resumeTorrentFile(String infoHash) {
        return new File(home, infoHash + ".torrent");
    }

    File resumeDataFile(String infoHash) {
        return new File(home, infoHash + ".resume");
    }

    File stateFile() {
        return new File(home, "settings.dat");
    }

    File readTorrentPath(String infoHash) {
        File torrent = null;

        try {
            byte[] arr = FileUtils.readFileToByteArray(resumeTorrentFile(infoHash));
            entry e = entry.bdecode(Vectors.bytes2char_vector(arr));
            torrent = new File(e.dict().get("torrent_orig_path").string());
        } catch (Throwable e) {
            // can't recover original torrent path
        }

        return torrent;
    }

    private void saveSettings() {
        byte[] data = session.saveState();
        try {
            FileUtils.writeByteArrayToFile(stateFile(), data);
        } catch (Throwable e) {
            LOG.error("Error saving session state", e);
        }
    }

    private void loadSettings() {
        try {
            File f = stateFile();
            if (f.exists()) {
                byte[] data = FileUtils.readFileToByteArray(f);
                session.loadState(data);
            } else {
                revertToDefaultConfiguration();
            }
        } catch (IOException e) {
            LOG.error("Error loading session state", e);
        }
    }
}

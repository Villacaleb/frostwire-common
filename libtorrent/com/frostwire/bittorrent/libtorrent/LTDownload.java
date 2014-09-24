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
import com.frostwire.bittorrent.BTDownloadListener;
import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import com.frostwire.logging.Logger;
import com.frostwire.transfers.TransferItem;
import com.frostwire.transfers.TransferState;

import java.io.File;
import java.util.*;

/**
 * @author gubatron
 * @author aldenml
 */
public final class LTDownload extends TorrentAlertAdapter implements BTDownload {

    private static final Logger LOG = Logger.getLogger(LTDownload.class);

    private final TorrentHandle th;

    private BTDownloadListener listener;

    public LTDownload(TorrentHandle th) {
        super(th);
        this.th = th;

        LTEngine.getInstance().getSession().addListener(this);
    }

    @Override
    public String getName() {
        return th.getName();
    }

    @Override
    public String getDisplayName() {
        return th.getDisplayName();
    }

    @Override
    public long getSize() {
        TorrentInfo ti = th.getTorrentInfo();
        return ti != null ? ti.getTotalSize() : 0;
    }

    @Override
    public boolean isPaused() {
        return th.isPaused();
    }

    @Override
    public boolean isSeeding() {
        return th.isSeeding();
    }

    @Override
    public boolean isFinished() {
        return th.isFinished();
    }

    @Override
    public TransferState getState() {
        TorrentStatus.State state = th.getStatus().getState();

        if (th.isPaused()) {
            return TransferState.PAUSED;
        }

        switch (state) {
            case QUEUED_FOR_CHECKING:
                return TransferState.QUEUED_FOR_CHECKING;
            case CHECKING_FILES:
                return TransferState.CHECKING;
            case DOWNLOADING_METADATA:
                return TransferState.DOWNLOADING_METADATA;
            case DOWNLOADING:
                return TransferState.DOWNLOADING;
            case FINISHED:
                return TransferState.FINISHED;
            case SEEDING:
                return TransferState.SEEDING;
            case ALLOCATING:
                return TransferState.ALLOCATING;
            case CHECKING_RESUME_DATA:
                return TransferState.CHECKING;
            default:
                throw new IllegalArgumentException("No enum value supported");
        }
    }

    @Override
    public String getSavePath() {
        return th.getSavePath();
    }

    @Override
    public int getProgress() {
        float fp = th.getStatus().getProgress();

        if (Float.compare(fp, 1f) == 0) {
            return 100;
        }

        int p = (int) (th.getStatus().getProgress() * 100);
        return Math.min(p, 100);
    }

    @Override
    public long getBytesReceived() {
        return th.getStatus().getTotalDownload();
    }

    @Override
    public long getTotalBytesReceived() {
        return th.getStatus().allTimeDownload;
    }

    @Override
    public long getBytesSent() {
        return th.getStatus().getTotalUpload();
    }

    @Override
    public long getTotalBytesSent() {
        return th.getStatus().allTimeUpload;
    }

    @Override
    public float getDownloadSpeed() {
        return th.getStatus().getDownloadRate();
    }

    @Override
    public float getUploadSpeed() {
        return th.getStatus().getUploadRate();
    }

    @Override
    public int getConnectedPeers() {
        return th.getStatus().getNumPeers();
    }

    @Override
    public int getTotalPeers() {
        return th.getStatus().listPeers;
    }

    @Override
    public int getConnectedSeeds() {
        return th.getStatus().getNumSeeds();
    }

    @Override
    public int getTotalSeeds() {
        return th.getStatus().listSeeds;
    }

    @Override
    public String getInfoHash() {
        return th.getInfoHash().toString();
    }

    @Override
    public Date getDateCreated() {
        return new Date(th.getStatus().getAddedTime());
    }

    @Override
    public long getETA() {
        TorrentInfo ti = th.getTorrentInfo();
        if (ti == null) {
            return 0;
        }

        TorrentStatus status = th.getStatus();
        long left = ti.getTotalSize() - status.getTotalDone();
        long rate = status.getDownloadPayloadRate();

        if (left <= 0) {
            return 0;
        }

        if (rate <= 0) {
            return -1;
        }

        return left / rate;
    }

    @Override
    public void pause() {
        th.pause();
    }

    @Override
    public void resume() {
        th.resume();
    }

    @Override
    public void stop() {
        this.stop(false, false);
    }

    @Override
    public void stop(boolean deleteTorrent, boolean deleteData) {
        String infoHash = this.getInfoHash();

        LTEngine engine = LTEngine.getInstance();
        Session s = engine.getSession();

        s.removeListener(this);
        if (deleteData) {
            s.removeTorrent(th, Session.Options.DELETE_FILES);
        } else {
            s.removeTorrent(th);
        }

        if (deleteTorrent) {
            File torrent = LTEngine.getInstance().readTorrentPath(infoHash);
            if (torrent.exists()) {
                torrent.delete();
            }
        }

        engine.resumeDataFile(infoHash).delete();
        engine.resumeTorrentFile(infoHash).delete();
    }

    @Override
    public BTDownloadListener getListener() {
        return listener;
    }

    @Override
    public void setListener(BTDownloadListener listener) {
        this.listener = listener;
    }

    TorrentHandle getTorrentHandle() {
        return th;
    }

    @Override
    public void onTorrentFinished(TorrentFinishedAlert alert) {
        if (listener != null) {
            try {
                listener.finished(this);
            } catch (Throwable e) {
                LOG.error("Error calling listener", e);
            }
        }
    }

    @Override
    public boolean isPartial() {
        return th.isPartial();
    }

    @Override
    public String makeMagnetUri() {
        return th.makeMagnetUri();
    }

    @Override
    public int getDownloadRateLimit() {
        return th.getDownloadLimit();
    }

    @Override
    public void setDownloadRateLimit(int limit) {
        th.setDownloadLimit(limit);
        th.saveResumeData();
    }

    @Override
    public int getUploadRateLimit() {
        return th.getUploadLimit();
    }

    @Override
    public void setUploadRateLimit(int limit) {
        th.setUploadLimit(limit);
        th.saveResumeData();
    }

    @Override
    public void requestTrackerAnnounce() {
        th.forceReannounce();
    }

    @Override
    public void requestTrackerScrape() {
        th.scrapeTracker();
    }

    @Override
    public Set<String> getTrackers() {
        List<AnnounceEntry> trackers = th.getTrackers();

        Set<String> urls = new HashSet<String>(trackers.size());

        for (AnnounceEntry e : trackers) {
            urls.add(e.getUrl());
        }

        return urls;
    }

    @Override
    public void setTrackers(Set<String> trackers) {
        List<AnnounceEntry> list = new ArrayList<AnnounceEntry>(trackers.size());

        for (String url : trackers) {
            list.add(new AnnounceEntry(url));
        }

        th.replaceTrackers(list);
        th.saveResumeData();
    }

    @Override
    public List<TransferItem> getItems() {
        if (!th.isValid()) {
            return Collections.emptyList();
        }

        TorrentInfo ti = th.getTorrentInfo();
        if (ti == null || !ti.isValid()) {
            return Collections.emptyList();
        }

        FileStorage fs = ti.getFiles();

        if (!fs.isValid()) {
            return Collections.emptyList();
        }

        int numFiles = fs.geNumFiles();
        String savePath = th.getSavePath();

        List<TransferItem> l = new ArrayList<TransferItem>(numFiles);

        for (int i = 0; i < numFiles; i++) {
            File f = new File(fs.getFilePath(i, savePath));
            l.add(new LTDownloadItem(f));
        }

        return l;
    }

    @Override
    public File getTorrentFile() {
        return LTEngine.getInstance().readTorrentPath(this.getInfoHash());
    }
}

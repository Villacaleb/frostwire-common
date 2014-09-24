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

package com.frostwire.bittorrent;

import com.frostwire.transfers.Transfer;
import com.frostwire.transfers.TransferState;

import java.io.File;
import java.util.Date;
import java.util.Set;

/**
 * @author gubatron
 * @author aldenml
 */
public interface BTDownload extends Transfer {

    public String getName();

    public String getDisplayName();

    public long getSize();

    public boolean isPaused();

    public boolean isSeeding();

    public boolean isFinished();

    public TransferState getState();

    public String getSavePath();

    /**
     * A value in the range [0, 100], that represents the progress of the torrent's
     * current task. It may be checking files or downloading.
     *
     * @return
     */
    public int getProgress();

    public long getBytesReceived();

    public long getTotalBytesReceived();

    public long getBytesSent();

    public long getTotalBytesSent();

    public float getDownloadSpeed();

    public float getUploadSpeed();

    public int getConnectedPeers();

    public int getTotalPeers();

    public int getConnectedSeeds();

    public int getTotalSeeds();

    public String getInfoHash();

    public Date getDateCreated();

    public long getETA();

    public void pause();

    public void resume();

    public void stop();

    /**
     * This method is specific for torrent downloads
     *
     * @param deleteTorrent
     * @param deleteData
     */
    public void stop(boolean deleteTorrent, boolean deleteData);

    public BTDownloadListener getListener();

    public void setListener(BTDownloadListener listener);

    public boolean isPartial();

    public String makeMagnetUri();

    public int getDownloadRateLimit();

    public void setDownloadRateLimit(int limit);

    public int getUploadRateLimit();

    public void setUploadRateLimit(int limit);

    public void requestTrackerAnnounce();

    public void requestTrackerScrape();

    public Set<String> getTrackers();

    public void setTrackers(Set<String> trackers);

    public File getTorrentFile();
}

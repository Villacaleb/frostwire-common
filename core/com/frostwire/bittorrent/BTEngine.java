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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author gubatron
 * @author aldenml
 */
public interface BTEngine {

    public File getHome();

    public void setHome(File home);

    public BTEngineListener getListener();

    public void setListener(BTEngineListener listener);

    public void start();

    public void stop();

    public boolean isStarted();

    public boolean isFirewalled();

    public void download(File torrent, File saveDir) throws IOException;

    public void restoreDownloads(File saveDir);

    public long getDownloadRate();

    public long getUploadRate();

    public long getTotalDownload();

    public long getTotalUpload();

    public int getDownloadRateLimit();

    public int getUploadRateLimit();

    public void revertToDefaultConfiguration();
}

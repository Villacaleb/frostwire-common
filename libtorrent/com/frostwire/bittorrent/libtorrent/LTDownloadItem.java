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

import com.frostwire.bittorrent.BTDownloadItem;

import java.io.File;

/**
 * @author gubatron
 * @author aldenml
 */
public final class LTDownloadItem implements BTDownloadItem {

    private final boolean skipped;
    private final File file;

    public LTDownloadItem(boolean skipped, File file) {
        this.skipped = skipped;
        this.file = file;
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    public File getFile() {
        return file;
    }
}

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

package com.frostwire.search.domainalias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Default Domain Alias Manifest "fetcher", creates and holds a Domain Alias manifest in memory
 * that can be used while we fetch a signed one from another source.
 * @author gubatron
 *
 */
public class DefaultDomainAliasManifestFetcher extends AbstractDomainAliasManifestFetcher {

    
    public DefaultDomainAliasManifestFetcher(DomainAliasManifestFetcherListener listener) {
        super(listener);
    }

    @Override
    public void fetchManifest() {
        DomainAliasManifest manifest = new DomainAliasManifest();
        manifest.lastUpdated = System.currentTimeMillis();
        manifest.version = 0;
        manifest.aliases = new HashMap<String, List<String>>();

        //KAT
        List<String> katAliases = new ArrayList<String>();
        katAliases.add("kickassunblock.info");
        katAliases.add("kickasstorrents.come.in");
        katAliases.add("kickass.pw");
        manifest.aliases.put("kickass.to", katAliases);
        
        //TPB
        
        List<String> tpbAliases = new ArrayList<String>();
        tpbAliases.add("pirateproxy.net"); //*
        tpbAliases.add("proxybay.de"); //*
        
        tpbAliases.add("pirateproxy.se");
        tpbAliases.add("tpb.unblocked.co");
        tpbAliases.add("thelitebay.com");
        tpbAliases.add("www.proxybay.eu");
        
        tpbAliases.add("proxybay.eu");
        tpbAliases.add("pirateproxy.ca");
        tpbAliases.add("tpbunion.com");
        tpbAliases.add("quluxingba.info");
        tpbAliases.add("tpb.pirati.cz");
        tpbAliases.add("tpb.al");
        tpbAliases.add("piratesniper.org.uk");
        tpbAliases.add("tpb.rebootorrents.com");
        tpbAliases.add("piratebay.io");
        tpbAliases.add("thepirate.al");
        manifest.aliases.put("thepiratebay.se", tpbAliases);
        
        //TORRENTS
        /*
        List<String> torrentsAliases = new ArrayList<String>();
        torrentsAliases.add("torrents.fm");
        manifest.aliases.put("torrents.com", torrentsAliases);
        */
        
        //ISOHUNT
        /*
        List<String> isoHuntAliases = new ArrayList<String>();
        isoHuntAliases.add("isohunt.come.in");
        manifest.aliases.put("isohunt.to",isoHuntAliases);
        */
        
        List<String> extraTorrentAliases = new ArrayList<String>();
        extraTorrentAliases.add("extratorrent.cc.prx.websiteproxy.co.uk");
        
        manifest.aliases.put("extratorrent.cc", extraTorrentAliases);//Collections.<String> emptyList());
        notifyManifestFetched(manifest);
    }
}
/*
 This file is part of Airsonic.

 Airsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Airsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Airsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2016 (C) Airsonic Authors
 Based upon Subsonic, Copyright 2009 (C) Sindre Mehus
 */
package org.airsonic.player.controller;

import org.airsonic.player.domain.Lyrics;
import org.airsonic.player.domain.MediaFile;
import org.airsonic.player.service.LyricsService;
import org.airsonic.player.service.MediaFileService;
import org.airsonic.player.service.SecurityService;
import org.airsonic.player.view.LyricsPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

/**
 * Controller for the lyrics popup.
 *
 * @author Sindre Mehus
 */
@Controller
@RequestMapping({"/lyrics", "/lyrics.view"})
public class LyricsController {

    private final LyricsService lyricsService;
    private final MediaFileService mediaFileService;
    private final SecurityService securityService;

    public LyricsController(
        LyricsService lyricsService,
        MediaFileService mediaFileService,
        SecurityService securityService
    ) {
        this.lyricsService = lyricsService;
        this.mediaFileService = mediaFileService;
        this.securityService = securityService;
    }

    @GetMapping
    protected ModelAndView handleRequestInternal(
        Principal principal,
        @RequestParam(value = "id", required = false) Integer id
    ) {

        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        if (mediaFile == null || !securityService.isFolderAccessAllowed(mediaFile, principal.getName())) {
            return new ModelAndView("notFound");
        }
        String artist = mediaFile.getArtist() != null ? mediaFile.getArtist() : mediaFile.getAlbumArtist();
        String song = mediaFile.getTitle();
        Lyrics lyrics = lyricsService.getLyricsFromMediaFile(mediaFile);
        LyricsPage lyricsPage = new LyricsPage(
            id,
            artist,
            song,
            lyrics != null ? lyrics.getLyrics() : null
        );
        return new ModelAndView("lyrics","view", lyricsPage);
    }
}

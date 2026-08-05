package com.samim.jarvis.voice.command

import org.junit.Test
import java.util.Locale

class CommandParserTest {
    @Test
    fun testOpenYoutube() {
        val c = CommandParser.parse("open youtube for relaxing music", Locale.ENGLISH)
        assert(c is Command.OpenYouTube)
    }

    @Test
    fun testCall() {
        val c = CommandParser.parse("call Alice", Locale.ENGLISH)
        assert(c is Command.CallContact)
    }

    @Test
    fun testOpenApp() {
        val c = CommandParser.parse("open Spotify", Locale.ENGLISH)
        assert(c is Command.LaunchApp)
    }

    @Test
    fun testHindiOpen() {
        val c = CommandParser.parse("Youtube kholo", Locale.forLanguageTag("hi-IN"))
        assert(c is Command.OpenYouTube)
    }
}

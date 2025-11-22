QS Toolkit (FORK OF BatteryTile)

An Android Quick Settings toolkit adding essential controls to your status bar. Originally a battery monitor, it has evolved into a complete utility belt.

⚠️⚠️⚠️ Disclaimer ⚠️⚠️⚠️

All of the features and all of the code in this fork were written by Gemini. Even though I didn't understand the language, I just asked Gemini what I wanted and made this.

Features

🔋 Battery Tile

Live Monitoring: Displays battery state, percentage, temperature, voltage, and current.

Smart Icons: Adaptive icons reflect charging status.

Shortcut: Long-press to open Android's Battery Settings (restoring functionality removed in Android 12).

Battery Saver: (Experimental) Toggles Battery Saver for a classic Android 11 experience.

🔊 Volume Tile

Quick Access: Instantly opens the system volume panel.

Hardware Saver: Reduces wear on physical volume buttons.


🔒 Lock Screen Tile

Instant Lock: Turns off your screen with a single tap.


🛡️ Private DNS Tile

Privacy Toggle: Quickly toggles your Private DNS provider (e.g., AdGuard).

Smart Switching: Switches between "Off" and your saved provider (defaults to "Automatic" if none saved).

Requires ADB: Needs a one-time permission grant.

Reasoning (The Origin Story)

Android 13 removed the Quick Settings shortcut to the battery page. This app restores that functionality via a custom tile while adding useful tools missing from stock Android.

Compatibility

Requires Android 10 (API 29) or higher for tile subtitle support.

Setup Guide

How to use the Lock Screen Tile

Add the Lock Screen tile.

Tap it once.

When prompted, enable "QS Toolkit" in Accessibility Settings.

Done. Tapping the tile now locks your screen without disabling fingerprint unlock.

How to enable the Private DNS Tile

This tile requires permission to modify secure settings. Connect your phone via USB (debugging enabled) and run:

adb shell pm grant com.cominatyou.batterytile android.permission.WRITE_SECURE_SETTINGS


How to customize the Battery Tile

Go to Settings > Apps > QS Toolkit > Additional settings to:

Select displayed stats (Voltage, Temperature, etc.).

Toggle dynamic icons.

Enable experimental Battery Saver mode.

FAQ

Why does the Lock Tile need Accessibility?

To simulate a physical power button press. Older methods (Device Admin) disable biometric unlock, forcing you to use a PIN. Accessibility keeps your fingerprint sensor working.

Why does Private DNS need ADB?

Private DNS is a "Secure" setting. Android requires explicit permission via ADB to prevent unauthorized apps from hijacking your network configuration.

Why are Battery Saver features "Experimental"?

Android lacks an official API for apps to toggle Battery Saver. We use a workaround via ADB, which may occasionally conflict with the system toggle.

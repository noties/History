
# Why not having essential Activity events passed to ScreenManager

I decided **not** to include `onActivityResult`, `onBackPressed` and `onRequestPermissionResult`
into `ScreenManager` in order to keep one single source of truth for them. Library provides
simple abstraction in shape of plugins, that should handle subscription for these events.
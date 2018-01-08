
# Why not having essential Activity events passed to ScreenManager

I decided **not** to include `onActivityResult`, `onBackPressed` and `onRequestPermissionResult`
into `ScreenManager` in order to keep one single source of truth for them. Library provides
simple abstraction in shape of plugins, that should handle subscription for these events.

**do not** block drawing in `onAttach` as it can possibly block drawing of the whole layout,
you still can use OnPreDrawListener, but it must return true. This is because `onAttach` can
be called after restoring state and if a screen has not-null Visibility, its `onAttach` will be
called, but never 

**why it's important to set start values** in change: if layout structure was restored,
views won't be holding information of their previous positions

**NB** if you implement own ChangeController (and not using builder) then in `back` method
to & from screens are swapped (indicating actual to & from transition), so when going back
the active screen will be `from` and inactive screen will be `to` (not how they were initially)
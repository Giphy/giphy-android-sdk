### The Giphy SDK for Android


### Installation

Add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:1.0.0'
``` 
    
### Basic usage

Before you start using the GIPHY SDK, you have to initialize it using your GIPHY API KEY. You can apply for a production key [here](https://developers.giphy.com/dashboard/)

```kotlin
    GiphyCoreUI.configure(this, YOUR_API_KEY)
```

All the magic is done by the `GiphyDialogFragment`. You can adjust the GIPHY SDK by passing a `GPHSettings` object when creating the dialog.

### `GPHSettings` properties
- `gridType` - you can choose between a `waterfall` and `carousel` layout
- `theme` - set the theme to be `LightTheme` or `DarkTheme`
- `mediaTypeConfig` - set the media type(s) you'd like to show: `gifsOnly`, `stickersOnly`, or `gifsAndStickers`
- `dimBackground` - lets you dim the background content while using the 'gifs picker'

#### Create a settings object
``` kotlin 
var settings = GPHSettings(gridType = GridType.waterfall, theme = LightTheme, dimBackground = true)
``` 

#### Instantiate a `GiphyDialogFragment` with the settings object

``` kotlin
val gifsDialog = GiphyDialogFragment.newInstance(settings)
```
#### Set a `GifSelectionListener` 

``` kotlin
 gifsDialog.gifSelectionListener = object : GiphyDialogFragment.GifSelectionListener {
                override fun onGifSelected(media: Media) {

                }
            }
```
#### Launch the GIFs picker 

```kotlin
gifsDialog.show(supportFragmentManager, "gifs_dialog")
```

### Using the SDK GIF buttons
The Giphy SDK also provides you with two button options, which you can easily personalize and use to trigger the GIFs pickup process. 

You can use a `GPHBrandButton` to generate a button with the Giphy Logo on it, or the `GPHGenericButton` for a more generic look.

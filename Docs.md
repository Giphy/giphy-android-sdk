## GIPHY UI SDK for Android 

#### Requirements 

- Giphy UI SDK only supports projects that have been upgraded to [androidx](https://developer.android.com/jetpack/androidx/). 
- Requires minSdkVersion 19
- A Giphy API key from the [Giphy Developer Portal](https://developers.giphy.com/dashboard/?create=true).
 

### Installation

Add the GIPHY Maven repository to your project's ```build.gradle``` file: 
 
``` gradle
maven {
    url "http://giphy.bintray.com/giphy-sdk"
}
```

Then add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:1.2.0'
``` 
    
### Basic Setup
Here's a basic setup to make sure everything's working. Configure the GIPHY SDK with your API key.

```kotlin

class GiphyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_API_KEY)
        
        GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
    }
}
```
### Templates: `GiphyDialogFragment`

Configure the SDK with your API key. 

```kotlin
    Giphy.configure(this, YOUR_API_KEY)
```
 
Create a new instance of `GiphyDialogFragment`, which takes care of all the magic. Adjust the layout and theme by passing a `GPHSettings` object when creating the dialog.
 
``` kotlin 
var settings = GPHSettings(gridType = GridType.waterfall, theme = LightTheme, dimBackground = true)
``` 

Instantiate a `GiphyDialogFragment` with the settings object.

``` kotlin
val gifsDialog = GiphyDialogFragment.newInstance(settings)
```

#### `GPHSettings` properties

- **Theme**: set the theme to be `DarkTheme` or `LightTheme`
```kotlin
settings.theme = LightTheme
```

- **Layout**: set the layout to be `.waterfall` (vertical) or `.carousel` (horizontal) 
```kotlin
settings.gridType = GridType.waterfall
```

- **Media types**: Set the content type(s) you'd like to show by setting the `mediaTypeConfig` property, which is an array of `GPHContentType`s 
<br> **Note**: Emoji only is not available for the carousel layout option. 
```kotlin
settings.mediaTypeConfig = arrayOf(GPHContentType.gif, GPHContentType.sticker, GPHContentType.text, GPHContentType.emoji)
```

- **Confirmation screen**:  we provide the option to show a secondary confirmation screen when the user taps a GIF, which shows a larger rendition of the asset.
This confirmation screen is only available for `.waterfall` mode - this property will be ignored if the `layout` is `.carousel`. 
```kotlin
setting.showConfirmationScreen = true 
```

- **Rating**: set a specific content rating for the search results. Default `pg13`.
```kotlin
settings.rating = RatingType.pg13
```

- **Rendition**:  You can change the rendition type for the grid and also for the confirmation screen, if you are using it.  Default rendition is  `fixedWidth` for the grid and `original` for the confirmation screen.
```kotlin
settings.renditionType = RenditionType.fixedWidth
settings.confirmationRenditionType = RenditionType.original 
```

#### Presentation 
Show your `GiphyDialogFragment` using the `SupportFragmentManager` and watch as the GIFs start flowin'.

```kotlin
gifsDialog.show(supportFragmentManager, "gifs_dialog")
```

#### Events 
To handle GIF selection you need to implement the `GifSelectionListener` interface.
``` kotlin
 giphyDialog.gifSelectionListener = object: GiphyDialogFragment.GifSelectionListener {
    override fun onGifSelected(media: Media) {
        //Your user tapped a GIF
    }

    override fun onDismissed() {
        //Your user dismissed the dialog without selecting a GIF
    }
}
```

From there, it's up to you to decide what to do with the GIF. 

Create a `GPHMediaView` to display the media. Optionaly, you can pass a rendition type to be loaded.

```kotlin
val mediaView = GPHMediaView(context)
mediaView.setMedia(media, RenditionType.original)
```
You can also populate a `GPHMediaView` with a media `id` like so: 
```kotlin
mediaView.setMediaWithId(media.id)  
```
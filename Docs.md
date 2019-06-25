## GIPHY SDK for Android

### Installation

Add the GIPHY Maven repository to your project's ```build.gradle``` file: 
 
``` gradle
maven {
    url "http://giphy.bintray.com/giphy-sdk"
}
```

Then add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:1.0.0'
``` 
    
### Getting started
Make sure to configure the GIPHY SDK with your Giphy API key, which you can grab from [Giphy Developer Portal](https://developers.giphy.com/dashboard/?create=true).

Here is a basic example of how everything should work:

```kotlin

class GiphyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GiphyCoreUI.configure(this, YOUR_API_KEY)
        
        GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
    }
}
```

### Basic usage

Before you start using GIPHY SDK, you have to initialize it using your GIPHY API KEY. You can apply for an API key [here](https://developers.giphy.com/dashboard/)

```kotlin
    GiphyCoreUI.configure(this, YOUR_API_KEY)
```

All the magic is done by the `GiphyDialogFragment`. You can adjust the GIPHY SDK by passing a `GPHSettings` object when creating the dialog.

Create a settings object to personalize your GIF picker. 
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

## Buttons

There are three button classes provided for you to use if you choose to.

#### GPHGiphyButton

GIPHY branded button available in the following styles:

- `logo` - full giphy logo
- `logoRounded` - same styles as `logo` with rounded corners
- `iconSquare` - square giphy icon logo with black background
- `iconSquareRounded` - same styles as `iconSquare` with rounded corners
- `iconColor` - color version of giphy icon logo with transparent background
- `iconBlack` - solid black version of the giphy icon logo with transparent background
- `iconWhite` - solid white version of the giphy icon logo with transparent background

```
val button = GPHGiphyButton(context)
button.style = GPHGiphyButtonStyle.iconBlack
```


#### GPHGifButton

Generic gif button with the text "GIF", available in the following styles:

- `rectangle` - rectuangular "pill" style button with solid background and transparent text
- `rectangleRounded` - same styles as `rectangle` with rounded corners
- `rectangleOutline` - rectuangluar "pill" style button with solid text and an outline
- `rectangleOutlineRounded` - same styles as `rectangleOutline` with rounded corners
- `square` - same styles as `rectangle` but square with smaller text
- `squareRounded` - same styles as `square` with rounded corners
- `squareOutline` - same styles as `rectangleOutline` but square with smaller text
- `squareOutlineRounded` - same styles as `squareOutline` with rounded corners
- `text` - transparent background button with "gif" text only

The `GPHGifButton` is also available in the following colors:

- `pink` - pink and purple gradient
- `blue` - blue and purple gradient
- `black` - solid black
- ` white` - solid white

``` kotlin
val button = GPHGifButton(context)
button.style = GPHGifButtonStyle.squareRounded
button.color = GPHGifButtonColor.blue
```


#### GPHContentTypeButton

Icon buttons for the different supported icon types. These come in the following styles:

- `stickers` - solid sticker icon
- `stickersOutline` - outline version of the `stickers` button
- `emoji` - solid emoji smiley icon
- `emojiOutline` - outline version of the `emoji` button
- `text` - solid text speech bubble icon
- `textOutline` - outline version of the `text` button

The `GPHContentTypeButton` is also available in the following colors:

- `pink` - pink and purple gradient
- `blue` - blue and purple gradient
- `black` - solid black
- ` white` - solid white

```
val button = GPHContentTypeButton(context)
button.style = GPHContentTypeButtonStyle.emoji
button.color = GPHGifButtonColor.blue
```

## GIPHY UI SDK for Android 

#### Requirements 

- Giphy UI SDK only supports projects that have been upgraded to [androidx](https://developer.android.com/jetpack/androidx/). 
- Requires minSdkVersion 19
- A Giphy Android SDK key from the [Giphy Developer Portal](https://developers.giphy.com/dashboard/?create=true).

### Installation

Add the GIPHY Maven repository to your project's ```build.gradle``` file: 
 
``` gradle
maven {
    url "https://giphy.bintray.com/giphy-sdk"
}
```

Then add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:2.0.7'
``` 
    
### Basic Setup
Here's a basic setup to make sure everything's working. Configure the GIPHY SDK with your API key. Apply for a new __Android SDK__ key. Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.

```kotlin

class GiphyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_ANDROID_SDK_KEY)
        
        GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
    }
}
```

or pass your API key as a fragment argument to configure the GIPHY SDK right before opening `GiphyDialogFragment` :

```kotlin
val dialog = GiphyDialogFragment.newInstance(settings.copy(selectedContentType = contentType), YOUR_ANDROID_SDK_KEY)
```

### Custom UI

We offer two solutions for the SDK user interface - pre-built templates which handle the entirety of the GIPHY experience, and a [Grid-Only implementation](https://developers.giphy.com/docs/sdk#grid) which allows for endless customization.  

See [customization](https://developers.giphy.com/docs/sdk#grid) to determine what's best for you.
 
_Skip ahead to [Grid-Only section](#the-giphy-grid)_

### Templates: `GiphyDialogFragment`

Configure the SDK with your API key. Apply for a new __Android SDK__ key. Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.

```kotlin
    Giphy.configure(this, YOUR_ANDROID_SDK_KEY)
```
 
Create a new instance of `GiphyDialogFragment`, which takes care of all the magic. Adjust the layout and theme by passing a `GPHSettings` object when creating the dialog.
 
```kotlin 
val settings = GPHSettings(GridType.waterfall, GPHTheme.Dark)
``` 

Instantiate a `GiphyDialogFragment` with the settings object.

``` kotlin
val gifsDialog = GiphyDialogFragment.newInstance(settings)
```

#### `Fresco` initialization
The SDK has special `Fresco` setup to support our use case, though this should not pose any conflicts with your use of `Fresco` outside of the GIPHY SDK. 
You can use our `GiphyFrescoHandler`:
``` kotlin
Giphy.configure(context, YOUR_API_KEY, verificationMode, frescoHandler = object : GiphyFrescoHandler {
                override fun handle(imagePipelineConfigBuilder: ImagePipelineConfig.Builder) {                    
                }
                override fun handle(okHttpClientBuilder: OkHttpClient.Builder) {                    
                }
}
``` 

#### `GPHSettings` properties

- **GPHTheme**: set the theme to be `Dark`, `Light` or `Automatic` which will match the application's `Night Mode` specifications for android P and newer. If you don't specify a theme, `Automatic` mode will be applied by default.
```kotlin
settings.theme = GPHTheme.Dark
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

Set default `GPHContentType`:
``` kotlin
settings.selectedContentType = GPHContentType.emoji
```

- **Recently Picked**: As of version `1.2.6` you can add an additional `GPHContentType` to you `mediaConfigs` array, called `GPHContentType.recents` which will automatically add a new tab with the recently picked GIFs and Stickers by the user. The tab will appear automatically if the user has picked any GIFs or Stickers.
```kotlin
val mediaTypeConfig = arrayOf(
    GPHContentType.gif,
    GPHContentType.sticker,
    GPHContentType.recents
)
```
Users can remove gifs from recents with a long-press on the GIF in the recents grid.

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
- **Checkered Background**: You can enable/disabled the checkered background for stickers and text media type.
```kotlin
settings.showCheckeredBackground = true
```

- **Blurred Background**: Use a translucent blurred background of the template container
```
settings.useBlurredBackground = true
```
- **Stickers Column Count**: Customise the number of columns for stickers (Accepted values between 2 and 4). We recommend using 3 columns for blurred mode.
```kotlin
settings.stickerColumnCount: Int = 3
```

- **Suggestions bar**: As of version `2.0.4` you can hide suggestions bar
```kotlin
settings.showSuggestionsBar = false
```

#### Presentation 
Show your `GiphyDialogFragment` using the `SupportFragmentManager` and watch as the GIFs start flowin'.

```kotlin
gifsDialog.show(supportFragmentManager, "gifs_dialog")
```

#### Events 
**Activity**
To handle GIF selection you need to implement the `GifSelectionListener` interface. If you are calling the GiphyDialogFragment from an activity instance, it is recommended that your activity implements the interface `GifSelectionListener`. When using this approach, the Giphy dialog will check at creation time, if the activity is implementing the `GifSelectionListener` protocol and set the activity as a callback, if no other listeners are set programatically.
``` kotlin
 class DemoActivity : AppCompatActivity(), GiphyDialogFragment.GifSelectionListener {
    override fun onGifSelected(media: Media, searchTerm: String?, selectedContentType: GPHContentType)
        //Your user tapped a GIF
    }

    override fun onDismissed(selectedContentType: GPHContentType) {
        //Your user dismissed the dialog without selecting a GIF
    }
    override fun didSearchTerm(term: String) {
        //Callback for search terms
    }
}
```

**Fragment**
- Option A: If you are calling the `GiphyDialogFragment` from a fragment context, you can also create a listener object to handle events.
``` kotlin
 giphyDialog.gifSelectionListener = object: GiphyDialogFragment.GifSelectionListener {
    fun onGifSelected(media: Media, searchTerm: String?)
        //Your user tapped a GIF
    }

    override fun onDismissed() {
        //Your user dismissed the dialog without selecting a GIF
    }
    override fun didSearchTerm(term: String) {
        //Callback for search terms
    }
}
```

#### GifSelectionListener technical note
As the `GiphyDialogFragment` is based on `DialogFragment`, this means that if the dialog is recreated after a process death caused by the Android System, your listener will not have a change to get set again so gif delivery will fail for that session.
To prevent such problems, we also implemented the android `setTargetFragment` API, to allow callbacks to be made reliably to the caller fragment in the event of process death.   

- Option B: `GiphyDialogFragment` also implements support for the `setTargetFragment` API. If it detects a target fragment attached, **it will deliver the content to the target fragment and ignore the `GifSelectionListener`**. Response data will contain the `Media` object selected and the search term used to discover that `Media`.
```kotlin

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GIFS) {
            val media = data?.getParcelableExtra<Media>(GiphyDialogFragment.MEDIA_DELIVERY_KEY)
            val keyword = data?.getStringExtra(GiphyDialogFragment.SEARCH_TERM_KEY)
            //TODO: handle received data
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
```

#### Customise the `GiphyDialogFragment`
You can easily customise the color scheme for the `GiphyDialogFragment` for a better blend-in with your application UI. Simply override the following color resources:
 - Light Theme:
```xml
    <color name="gph_channel_color_light">#FF4E4E4E</color>
    <color name="gph_handle_bar_light">#ff888888</color>
    <color name="gph_background_light">#ffF1F1F1</color>
    <color name="gph_text_color_light">#ffA6A6A6</color>
    <color name="gph_active_text_color_light">#ff000000</color>
```
- Dark Theme
```xml
    <color name="gph_channel_color_dark">#ffD8D8D8</color>
    <color name="gph_handle_bar_dark">#ff888888</color>
    <color name="gph_background_dark">#ff121212</color>
    <color name="gph_text_color_dark">#ffA6A6A6</color>
    <color name="gph_active_text_color_dark">#ff00FF99</color>
```

From there, it's up to you to decide what to do with the GIF. 

Create a `GPHMediaView` to display the media. Optionaly, you can pass a rendition type to be loaded.

```kotlin
val mediaView = GPHMediaView(context)
mediaView.setMedia(media, RenditionType.original)
```
You can populate a `GPHMediaView` with a media `id` like so:
```kotlin
mediaView.setMediaWithId(media.id) { result, e ->                
    e?.let {
        //your code here
    }
}  
```
Or just fetch media response for your needs
```kotlin
GPHCore.gifById(id) { result, e ->
    gifView.setMedia(result?.data, RenditionType.original)
    e?.let {
        //your code here
    }
}
```
Use the media's aspectRatio property to size the view:
```kotlin
val aspectRatio = media.aspectRatio 
```


## Grid-Only and `GiphyGridView`

The following section refers to the Grid-Only solution of the SDK. Learn more [here](https://developers.giphy.com/docs/sdk#grid)

If you require a more flexible experience, use the `GiphyGridView` instead of the `GiphyDialogFragment` to load and render GIFs.

`GiphyGridView` properties:
- **orientation** - tells the scroll direction of the grid. (e.g. `GiphyGridView.HORIZONTAL`, `GiphyGridView.VERTICAL`)
- **spanCount** - number of lanes in the grid
- **cellPadding** - spacing between rendered GIFs
- **showViewOnGiphy** - enables/disables the `Show on Giphy` action in the long-press menu
- **showCheckeredBackground** - use a checkered background (for stickers only)
- **fixedSizeCells** - display content in equally sized cells (for stickers only)

## Loading GIFs using `GPHContent`
 `GPHContent` contains all query params that necessary to load GIFs.
- **mediaType** - media type that should be loaded (e.g. `MediaType.gif`)
- **requestType** - tells the controller if the query should look for a trending feed of gifs or perform a search action (e.g. `GPHRequestType.trending`)
- **rating** - minimum rating (e.g. `RatingType.pg13`)
- **searchQuery**:- custom search input (e.g. `cats`)

Use one of the convenience methods avaiable in the `GPHContent` in order to create a query.

#### Trending
- `GPHContent.trending(mediaType: MediaType, ratingType: RatingType = RatingType.pg13)`
- `GPHContent.trendingGifs`, `GPHContent.trendingStickers`, etc.

#### Search
`GPHContent.searchQuery(search: String, mediaType: MediaType = MediaType.gif, ratingType: RatingType = RatingType.pg13)`

## Execute the query
After you have have defined your query using a `GPHContent` object, to load the media content pass this object to `GiphyGridView`
```kotlin
val gifsContent = GPHContent.searchQuery("cats")
gifsGridView.updateContent(gifsContent)
```

- **Recently Picked**: 
Show a user a collection of his recently picked GIFs/Stickers:
```kotlin
val gifsContent = GPHContent.recents
gifsGridView.updateContent(gifsContent)
```
Before showing a recents tab, make sure that the user actually has recent gifs stored, by checking the following property:
 ```kotlin
val recentsCount = Giphy.recents.count
```
Optionally, you can clear the recent gifs:
```kotlin
Giphy.recents.clear()
```
Users can remove gifs from recents with a long-press on the GIF in the recents grid.

## Integrating the `GiphyGridView`

#### Method A
It can be done by embedding in your layout XML. UI properties can also be applied as XML attributes

```xml
<com.giphy.sdk.ui.views.GiphyGridView
    android:id="@+id/gifsGridView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:gphDirection="horizontal"
    app:gphSpanCount="2"
    app:gphCellPadding="12dp"
    app:gphShowCheckeredBackground="false"/>
```

Perform a query
```kotlin
gifsGridView.updateContent(GPHContent.searchQuery("dogs"))
```

#### Method B
Create a `GiphyGridView` and set it's UI properties
```kotlin
val gridView = GiphyGridView(context)

gridView.direction = GiphyGridFragment.HORIZONTAL
gridView.spanCount = 3
gridView.cellPadding = 20
```
Add the `GiphyGridView` to your layout and start making queries.
```kotlin
parentView.addView(gridView)

gridFragment?.content = GPHContent.searchQuery("dogs")
```
## Customise the GIF Loading
You can customise the loading experience of GIFs in the `GridView` by using the `GiphyLoadingProvider` class.
```kotlin

    // Implement the GiphyLoadingProvider interface
    private val loadingProviderClient = object : GiphyLoadingProvider {
        override fun getLoadingDrawable(position: Int): Drawable {
            val shape = LoadingDrawable(if (position % 2 == 0) LoadingDrawable.Shape.Rect else LoadingDrawable.Shape.Circle)
            shape.setColorFilter(getPlaceholderColor(), PorterDuff.Mode.SRC_ATOP)
            return shape
        }
    }

    // Attach the loading provider to the GridView
    gifsGridView.setGiphyLoadingProvider(loadingProviderClient)

```

## Callbacks
In order to interact with the `GiphyGridView` you can apply the following callbacks

```kotlin
interface GPHGridCallback {
    fun contentDidUpdate(resultCount: Int)
    fun didSelectMedia(media: Media)
}

interface GPHSearchGridCallback {
    fun didTapUsername(username: String)
    fun didLongPressCell(cell: GifView)
    fun didScroll()
}

```

Example
```kotlin
gridView.callback = object: GPHGridCallback {
    override fun contentDidUpdate(resultCount: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didSelectMedia(media: Media) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

gridView.searchCallback = object: GPHSearchGridCallback {
    override fun didTapUsername(username: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didLongPressCell(cell: GifView) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun didScroll(dx: Int, dy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
```

### Proguard
The GIPHY UI SDK exposes the `consumer-proguard.pro` rules for proguard/R8 so there is no need to add anything to your projects proguard rules.

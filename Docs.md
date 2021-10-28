## GIPHY UI SDK for Android 

#### Requirements 

- Giphy UI SDK only supports projects that have been upgraded to [androidx](https://developer.android.com/jetpack/androidx/). 
- Requires minSdkVersion 19
- A Giphy Android SDK key from the [Giphy Developer Portal](https://developers.giphy.com/dashboard/?create=true).

### Installation

The latest release is available on [Maven Central](https://search.maven.org/artifact/com.giphy.sdk/ui/2.0.9/aar) 

Add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:2.1.9'
``` 
    
### Basic Setup
Here's a basic setup to make sure everything's working. Configure the GIPHY SDK with your API key. Apply for a new __Android SDK__ key. Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.

##### kotlin
#
```kotlin

class GiphyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, YOUR_ANDROID_SDK_KEY)
        
        GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
    }
}
```
##### java
#
```java
class GiphyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Giphy.INSTANCE.configure(DemoActivityJava.this, YOUR_ANDROID_SDK_KEY, false);

        final GPHSettings settings = new GPHSettings();
        final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings);
        dialog.setGifSelectionListener(listener);
        dialog.show(getSupportFragmentManager(), "giphy_dialog");
    }
}
```

or pass your API key as a fragment argument to configure the GIPHY SDK right before opening `GiphyDialogFragment` :

##### kotlin
#
```kotlin
val dialog = GiphyDialogFragment.newInstance(settings.copy(selectedContentType = contentType), YOUR_ANDROID_SDK_KEY)
```
##### java
#
```java
final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings, YOUR_ANDROID_SDK_KEY);
```

### Custom UI

We offer two solutions for the SDK user interface - pre-built templates which handle the entirety of the GIPHY experience, and a [Grid-Only implementation](https://developers.giphy.com/docs/sdk#grid) which allows for endless customization.  

See [customization](https://developers.giphy.com/docs/sdk#grid) to determine what's best for you.
 
_Skip ahead to [Grid-Only section](#the-giphy-grid)_

### Templates: `GiphyDialogFragment`

Configure the SDK with your API key. Apply for a new __Android SDK__ key. Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.

##### kotlin
#
```kotlin
Giphy.configure(this, YOUR_ANDROID_SDK_KEY)
```
##### java
#
```java
Giphy.INSTANCE.configure(DemoActivityJava.this, YOUR_ANDROID_SDK_KEY, false);
```
 
Create a new instance of `GiphyDialogFragment`, which takes care of all the magic. Adjust the layout and theme by passing a `GPHSettings` object when creating the dialog.

##### kotlin
#
```kotlin 
val settings = GPHSettings(GridType.waterfall, GPHTheme.Dark)
``` 
##### java
#
```java
final GPHSettings settings = new GPHSettings();
settings.setTheme(GPHTheme.Dark);
```

Instantiate a `GiphyDialogFragment` with the settings object.

##### kotlin
#
```kotlin
val gifsDialog = GiphyDialogFragment.newInstance(settings)
```
##### java
#
```java
final GiphyDialogFragment gifsDialog = GiphyDialogFragment.Companion.newInstance(settings);
```

#### `Fresco` initialization
The SDK has special `Fresco` setup to support our use case, though this should not pose any conflicts with your use of `Fresco` outside of the GIPHY SDK. 
You can use our `GiphyFrescoHandler`:

##### kotlin
#
```kotlin
Giphy.configure(context, YOUR_API_KEY, verificationMode, frescoHandler = object : GiphyFrescoHandler {
                override fun handle(imagePipelineConfigBuilder: ImagePipelineConfig.Builder) {                    
                }
                override fun handle(okHttpClientBuilder: OkHttpClient.Builder) {                    
                }
})
``` 
##### java
#
```java
Giphy.INSTANCE.configure(DemoActivityJava.this,
        YOUR_API_KEY,
        verificationMode,
        100 * 1024 * 1024,
        new HashMap<String, String>(),
        new GiphyFrescoHandler() {
@Override
public void handle(@NonNull OkHttpClient.Builder builder) {

        }

@Override
public void handle(@NonNull ImagePipelineConfig.Builder builder) {

        }
        });
```

#### `GPHSettings` properties

- **GPHTheme**: set the theme to be `Dark`, `Light` or `Automatic` which will match the application's `Night Mode` specifications for android P and newer. If you don't specify a theme, `Automatic` mode will be applied by default.

##### kotlin
#
```kotlin
settings.theme = GPHTheme.Dark
```
##### java
#
```java
settings.setTheme(GPHTheme.Dark);
```

As of version `2.1.9` you can set a custom theme
##### kotlin
#
```kotlin
GPHCustomTheme.channelColor= 0xffD8D8D8.toInt()
GPHCustomTheme.handleBarColor = 0xff888888.toInt()
GPHCustomTheme.backgroundColor = 0xff121212.toInt()
GPHCustomTheme.dialogOverlayBackgroundColor = 0xFF4E4E4E.toInt()
GPHCustomTheme.textColor = 0xffA6A6A6.toInt()
GPHCustomTheme.activeTextColor = 0xff00FF99.toInt()
GPHCustomTheme.imageColor = 0xC09A9A9A.toInt()
GPHCustomTheme.activeImageColor = 0xFF00FF99.toInt()
GPHCustomTheme.searchBackgroundColor = 0xFF4E4E4E.toInt()
GPHCustomTheme.searchQueryColor = 0xffffffff.toInt()
GPHCustomTheme.suggestionBackgroundColor = 0xFF212121.toInt()
GPHCustomTheme.moreByYouBackgroundColor = 0xFFF1F1F1.toInt()
GPHCustomTheme.backButtonColor = 0xFFFFFFFF.toInt()

val settings = GPHSettings(theme = GPHTheme.Custom)
val dialog = GiphyDialogFragment.newInstance(settings)
dialog.show(supportFragmentManager, "gifs_dialog")
```
##### java
#
```java
GPHCustomTheme.INSTANCE.setBackgroundColor(0xff121212);

final GPHTheme theme = GPHTheme.Custom;
final GPHSettings settings = new GPHSettings();
settings.setTheme(theme);
final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings);
dialog.show(getSupportFragmentManager(), "giphy_dialog");
```


- **Media types**: Set the content type(s) you'd like to show by setting the `mediaTypeConfig` property, which is an array of `GPHContentType`s 
<br> 
##### kotlin
#
```kotlin
settings.mediaTypeConfig = arrayOf(GPHContentType.gif, GPHContentType.sticker, GPHContentType.text, GPHContentType.emoji)
```
##### java
#
```java
final GPHContentType[] contentTypes = new GPHContentType[5];
contentTypes[1] = GPHContentType.sticker;
contentTypes[2] = GPHContentType.gif;
contentTypes[3] = GPHContentType.text;
contentTypes[4] = GPHContentType.emoji;
settings.setMediaTypeConfig(contentTypes);
```

Set default `GPHContentType`:
##### kotlin
#
``` kotlin
settings.selectedContentType = GPHContentType.emoji
```
##### java
#
```java
settings.setSelectedContentType(GPHContentType.emoji);
```

- **Recently Picked**: As of version `1.2.6` you can add an additional `GPHContentType` to you `mediaConfigs` array, called `GPHContentType.recents` which will automatically add a new tab with the recently picked GIFs and Stickers by the user. The tab will appear automatically if the user has picked any GIFs or Stickers.
##### kotlin
#
```kotlin
val mediaTypeConfig = arrayOf(
    GPHContentType.gif,
    GPHContentType.sticker,
    GPHContentType.recents
)
```
##### java
#
```java
final GPHContentType[] contentTypes = new GPHContentType[5];
        contentTypes[1] = GPHContentType.sticker;
        contentTypes[2] = GPHContentType.gif;
        contentTypes[3] = GPHContentType.text;
        contentTypes[4] = GPHContentType.recents;
        settings.setMediaTypeConfig(contentTypes);
```
Users can remove gifs from recents with a long-press on the GIF in the recents grid.

- **Confirmation screen**:  we provide the option to show a secondary confirmation screen when the user taps a GIF, which shows a larger rendition of the asset.
##### kotlin
#
```kotlin
setting.showConfirmationScreen = true 
```
##### java
#
```java
settings.setShowConfirmationScreen(true); 
```

- **Rating**: set a specific content rating for the search results. Default `pg13`.
##### kotlin
#
```kotlin
settings.rating = RatingType.pg13
```
##### java
#
```java
settings.setRating(RatingType.pg13); 
```

- **Rendition**:  You can change the rendition type for the grid and also for the confirmation screen, if you are using it.  Default rendition is  `fixedWidth` for the grid and `original` for the confirmation screen.
##### kotlin
#
```kotlin
settings.renditionType = RenditionType.fixedWidth
settings.confirmationRenditionType = RenditionType.original 
```
##### java
#
```java
settings.setRenditionType(RenditionType.fixedWidth);
settings.setConfirmationRenditionType(RenditionType.original);
```

- **Checkered Background**: You can enable/disabled the checkered background for stickers and text media type.
##### kotlin
#
```kotlin
settings.showCheckeredBackground = true
```
##### java
#
```java
settings.setShowCheckeredBackground(true);
```

- **Stickers Column Count**: Customise the number of columns for stickers (Accepted values between 2 and 4).
##### kotlin
#
```kotlin
settings.stickerColumnCount = 3
```
##### java
#
```java
settings.setStickerColumnCount(3); 
```

- **Suggestions bar**: As of version `2.0.4` you can hide suggestions bar
##### kotlin
#
```kotlin
settings.showSuggestionsBar = false
```
##### java
#
```java
settings.setShowSuggestionsBar(false);
```

- **Image Format**: You can choose a file type for the grid.
##### kotlin
#
```kotlin
settings.imageFormat = ImageFormat.WEBP
```
##### java
#
```java
settings.setImageFormat(ImageFormat.WEBP); 
```

#### Presentation 
Show your `GiphyDialogFragment` using the `SupportFragmentManager` and watch as the GIFs start flowin'.

##### kotlin
#
```kotlin
gifsDialog.show(supportFragmentManager, "gifs_dialog")
```
##### java
#
```java
gifsDialog.show(getSupportFragmentManager(), "gifs_dialog");
```

#### Events 
**Activity**
To handle GIF selection you need to implement the `GifSelectionListener` interface. If you are calling the GiphyDialogFragment from an activity instance, it is recommended that your activity implements the interface `GifSelectionListener`. When using this approach, the Giphy dialog will check at creation time, if the activity is implementing the `GifSelectionListener` protocol and set the activity as a callback, if no other listeners are set programatically.
##### kotlin
#
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
##### java
#
```java
public class DemoActivity extends AppCompatActivity implements GiphyDialogFragment.GifSelectionListener {
    @Override
    public void onGifSelected(@NonNull Media media, @androidx.annotation.Nullable String s, @NonNull GPHContentType gphContentType) {
        //Your user tapped a GIF
    }
    @Override
    public void onDismissed(@NonNull GPHContentType gphContentType) {
        //Your user dismissed the dialog without selecting a GIF
    }
    @Override
    public void didSearchTerm(@NonNull String s) {
        //Callback for search terms
    }
}
```

**Fragment**
- Option A: If you are calling the `GiphyDialogFragment` from a fragment context, you can also create a listener object to handle events.
##### kotlin
#
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
##### java
#
```java
giphyDialog.setGifSelectionListener(new GiphyDialogFragment.GifSelectionListener() {
    @Override
    public void onGifSelected(@NonNull Media media, @androidx.annotation.Nullable String s, @NonNull GPHContentType gphContentType) {
        //Your user tapped a GIF
    }

    @Override
    public void onDismissed(@NonNull GPHContentType gphContentType) {
        //Your user dismissed the dialog without selecting a GIF
    }

    @Override
    public void didSearchTerm(@NonNull String s) {
        //Callback for search terms
    }
});
```

#### GifSelectionListener technical note
As the `GiphyDialogFragment` is based on `DialogFragment`, this means that if the dialog is recreated after a process death caused by the Android System, your listener will not have a change to get set again so gif delivery will fail for that session.
To prevent such problems, we also implemented the android `setTargetFragment` API, to allow callbacks to be made reliably to the caller fragment in the event of process death.   

- Option B: `GiphyDialogFragment` also implements support for the `setTargetFragment` API. If it detects a target fragment attached, **it will deliver the content to the target fragment and ignore the `GifSelectionListener`**. Response data will contain the `Media` object selected and the search term used to discover that `Media`.
##### kotlin
#
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
##### java
#
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
    if (requestCode == REQUEST_GIFS && data != null) {
        Media media = data.getParcelableExtra(GiphyDialogFragment.MEDIA_DELIVERY_KEY);
        String[] keyword = data.getStringArrayExtra(GiphyDialogFragment.SEARCH_TERM_KEY);
        //TODO: handle received data
    }
    super.onActivityResult(requestCode, resultCode, data);
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

##### kotlin
#
```kotlin
val mediaView = GPHMediaView(context)
mediaView.setMedia(media, RenditionType.original)
```
##### java
#
```java
GPHMediaView mediaView = new GPHMediaView(context);
mediaView.setMedia(media, RenditionType.original, null);
```
You can populate a `GPHMediaView` with a media `id` like so:
##### kotlin
#
```kotlin
mediaView.setMediaWithId(media.id) { result, e ->                
    e?.let {
        //your code here
    }
}  
```
##### java
#
```java
mediaView.setMediaWithId(media.getId(), RenditionType.downsized, null, (result, e) -> {
    if (e != null) {
        //your code here
    }
    return null;
});
```
Or just fetch media response for your needs
##### kotlin
#
```kotlin
GPHCore.gifById(id) { result, e ->
    gifView.setMedia(result?.data, RenditionType.original)
    e?.let {
        //your code here
    }
}
```
##### java
#
```java
GPHCore.INSTANCE.gifById(id, (result, e) -> {
    if (result != null){
        gifView.setMedia(result.getData(),RenditionType.original, null);
    }
    if (e != null) {
        //your code here
    }
    return null;
});
```
Use the media's aspectRatio property to size the view:
##### kotlin
#
```kotlin
val aspectRatio = media.aspectRatio 
```
##### java
#
```java
float aspectRatio = MediaExtensionKt.getAspectRatio(media); 
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
- **imageFormat** - choose a file type for the grid
- **fixedSizeCells** - display content in equally sized cells (for stickers only)

## Loading GIFs using `GPHContent`
 `GPHContent` contains all query params that necessary to load GIFs.
- **mediaType** - media type that should be loaded (e.g. `MediaType.gif`)
- **requestType** - tells the controller if the query should look for a trending feed of gifs or perform a search action (e.g. `GPHRequestType.trending`)
- **rating** - minimum rating (e.g. `RatingType.pg13`)
- **searchQuery**:- custom search input (e.g. `cats`)

Use one of the convenience methods avaiable in the `GPHContent` in order to create a query.

#### Trending
##### kotlin
#
- `GPHContent.trending(mediaType: MediaType, ratingType: RatingType = RatingType.pg13)`
- `GPHContent.trendingGifs`, `GPHContent.trendingStickers`, etc.
##### java
#
- `GPHContent.Companion.trending(MediaType.gif, RatingType.pg13);`
- `GPHContent.Companion.getTrendingGifs();`, `GPHContent.Companion.getTrendingStickers();`, etc.

#### Search
##### kotlin
#
`GPHContent.searchQuery(search: String, mediaType: MediaType = MediaType.gif, ratingType: RatingType = RatingType.pg13)`
##### java
#
`GPHContent.Companion.searchQuery(search: String, MediaType.gif, RatingType.pg13);`

## Execute the query
After you have have defined your query using a `GPHContent` object, to load the media content pass this object to `GiphyGridView`
##### kotlin
#
```kotlin
val gifsContent = GPHContent.searchQuery("cats")
gifsGridView.content = gifsContent
```
##### java
#
```java
GPHContent gifsContent = GPHContent.Companion.searchQuery("cats", MediaType.gif, RatingType.pg13);
gifsGridView.setContent(gifsContent);
```

- **Recently Picked**: 
Show a user a collection of his recently picked GIFs/Stickers:
##### kotlin
#
```kotlin
val gifsContent = GPHContent.recents
gifsGridView.content = gifsContent
```
##### java
#
```java
GPHContent gifsContent = GPHContent.Companion.getRecents();
gifsGridView.setContent(gifsContent);
```
Before showing a recents tab, make sure that the user actually has recent gifs stored, by checking the following property:
##### kotlin
# 
```kotlin
val recentsCount = Giphy.recents.count
```
##### java
#
```java
int recentsCount = Giphy.INSTANCE.getRecents().getCount();
```
Optionally, you can clear the recent gifs:
##### kotlin
#
```kotlin
Giphy.recents.clear()
```
##### java
#
```java
Giphy.INSTANCE.getRecents().clear(); 
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
##### kotlin
#
```kotlin
gifsGridView.content = GPHContent.searchQuery("dogs")
```
##### java
#
```java
gifsGridView.setContent(GPHContent.Companion.searchQuery("dogs", MediaType.gif, RatingType.pg13));
```

#### Method B
Create a `GiphyGridView` and set it's UI properties
##### kotlin
#
```kotlin
val gridView = GiphyGridView(context)

gridView.direction = GiphyGridView.HORIZONTAL
gridView.spanCount = 3
gridView.cellPadding = 20
```
##### java
#
```java
GiphyGridView gridView = null;
gridView.setDirection(GiphyGridView.HORIZONTAL);
gridView.setSpanCount(3);
gridView.setCellPadding(20);
```
Add the `GiphyGridView` to your layout and start making queries.
##### kotlin
#
```kotlin
parentView.addView(gridView)

gridFragment?.content = GPHContent.searchQuery("dogs")
```
##### java
#
```java
parentView.addView(gridView);
```
## Customise the GIF Loading
You can customise the loading experience of GIFs in the `GridView` by using the `GiphyLoadingProvider` class.
##### kotlin
#
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
##### java
#
```java
    // Implement the GiphyLoadingProvider interface
    private GiphyLoadingProvider loadingProviderClient = new GiphyLoadingProvider() {
        @NonNull
        @Override
        public Drawable getLoadingDrawable(int position) {
            LoadingDrawable.Shape shape = LoadingDrawable.Shape.Circle;
            if (position % 2 == 0) {
                shape = LoadingDrawable.Shape.Rect;
            }
            return new LoadingDrawable(shape);
        }
    };

    // Attach the loading provider to the GridView
    gifsGridView.setGiphyLoadingProvider(loadingProviderClient);
```

## Callbacks
In order to interact with the `GiphyGridView` you can apply the following callbacks

##### kotlin
#
```kotlin
interface GPHGridCallback {
    /**
     * @param resultCount results count, in case of error it equals -1
     */
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
##### kotlin
#
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
##### java
#
```java
gridView.setCallback(new GPHGridCallback() {
    @Override
    public void contentDidUpdate(int resultCount) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Override
    public void didSelectMedia(@NonNull Media media) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
});

gridView.setSearchCallback(new GPHSearchGridCallback() {
    @Override
    public void didTapUsername(@NonNull String username) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Override
    public void didLongPressCell(@NonNull GifView cell) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Override
    public void didScroll(int dx, int dy) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
});
```

### Proguard
The GIPHY UI SDK exposes the `consumer-proguard.pro` rules for proguard/R8 so there is no need to add anything to your projects proguard rules.

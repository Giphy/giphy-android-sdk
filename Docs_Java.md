## GIPHY SDK for Android (JAVA version)

## _Table of contents_
**Setup**
- [Requirements](#requirements)
- [Installation](#installation)
- [API Key](#configure-your-api-key)
- [Customization](#custom-ui)

**Templates**
- [GiphyDialogFragment](#giphydialogfragment)
- [Fresco initialization](#fresco-initialization)
- [Settings](#gphsettings-properties)
  - [Theme](#theme)
  - [Media Types](#media-types)
- [GifSelectionListener](#events)

**GPHMedia**
- [GPHMediaView](#gphmediaview)
- [Media IDs](#media-ids)

**Caching & Dependencies**
- [Caching](#caching)
- [Dependencies](#dependencies)

**The Grid**
- [GiphyGridController](#grid-only-and-giphygridview)
- [GPHContent](#giphygridview-gphcontent)
- [Presentation](#integrating-the-giphygridview)
- [Callbacks](#callbacks)

**Clips (GIFs with Sound!) + Animated Text Creation**
- [Clips](https://github.com/Giphy/giphy-android-sdk/blob/main/clips.md)
- [Animated Text Creation](https://github.com/Giphy/giphy-android-sdk/blob/main/animate.md)

### Requirements
- The Giphy UI SDK only supports projects that have been upgraded to [androidx](https://developer.android.com/jetpack/androidx/).
- Requires minSdkVersion 19
- A Giphy Android SDK key from the [Giphy Developer Portal](https://developers.giphy.com/dashboard/?create=true).

### Installation

The latest release is available on [Maven Central](https://search.maven.org/artifact/com.giphy.sdk/ui/)

Add the GIPHY SDK dependency in the module ```build.gradle``` file:
```
implementation 'com.giphy.sdk:ui:2.3.17'
``` 

### Configure your API key
Configure your API key. Apply for a new __Android SDK__ key [here](https://developers.giphy.com/dashboard/). Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.
Here's a basic setup to make sure everything's working.
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

```java
final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings, YOUR_ANDROID_SDK_KEY);
```

## Custom UI

We offer two solutions for the SDK user interface - pre-built templates which handle the entirety of the GIPHY experience, and a [Grid-Only implementation](https://developers.giphy.com/docs/sdk#grid) which allows for endless customization.

See [customization](https://developers.giphy.com/docs/sdk#grid) to determine what's best for you.

_Skip ahead to [Grid-Only section](#grid-only-and-giphygridview)_

## Templates:

### GiphyDialogFragment

Configure the SDK with your API key. Apply for a new __Android SDK__ key. Please remember, you should use a separate key for every platform (Android, iOS, Web) you add our SDKs to.

```java
Giphy.INSTANCE.configure(DemoActivityJava.this, YOUR_ANDROID_SDK_KEY, false);
```

Create a new instance of `GiphyDialogFragment`, which takes care of all the magic. Adjust the layout and theme by passing a `GPHSettings` object when creating the dialog.

```java
final GPHSettings settings = new GPHSettings();
settings.setTheme(GPHTheme.Dark);
```

Instantiate a `GiphyDialogFragment` with the settings object.

```java
final GiphyDialogFragment gifsDialog = GiphyDialogFragment.Companion.newInstance(settings);
```

### Fresco initialization
The SDK has special `Fresco` setup to support our use case, though this should not pose any conflicts with your use of `Fresco` outside of the GIPHY SDK.
You can use our `GiphyFrescoHandler`:

```java
Giphy.INSTANCE.configure(DemoActivityJava.this,
        YOUR_API_KEY,
        verificationMode,
        100*1024*1024,
        new HashMap<String, String>(),
        new GiphyFrescoHandler(){
            @Override
            public void handle(@NonNull OkHttpClient.Builder builder){
            
                    }
            
            @Override
            public void handle(@NonNull ImagePipelineConfig.Builder builder){

            }
});
```

## GPHSettings properties

### _Theme_
Set the theme type (`GPHTheme`) to be `Dark`, `Light` or `Automatic` which will match the application's `Night Mode` specifications for android P and newer. If you don't specify a theme, `Automatic` mode will be applied by default.

```java
settings.setTheme(GPHTheme.Dark);
```

### _Customise the GiphyDialogFragment_
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

### _Custom Theme_
As of version `2.1.9` you can set a custom theme
```java
GPHCustomTheme.INSTANCE.setChannelColor(0xffD8D8D8);
GPHCustomTheme.INSTANCE.setHandleBarColor(0xff888888);
GPHCustomTheme.INSTANCE.setBackgroundColor(0xff121212);
GPHCustomTheme.INSTANCE.setDialogOverlayBackgroundColor(0xFF4E4E4E);
GPHCustomTheme.INSTANCE.setTextColor(0xffA6A6A6);
GPHCustomTheme.INSTANCE.setActiveTextColor(0xff00FF99);
GPHCustomTheme.INSTANCE.setImageColor(0xC09A9A9A);
GPHCustomTheme.INSTANCE.setActiveImageColor(0xFF00FF99);
GPHCustomTheme.INSTANCE.setSearchBackgroundColor(0xFF4E4E4E);
GPHCustomTheme.INSTANCE.setSearchQueryColor(0xffffffff);
GPHCustomTheme.INSTANCE.setSuggestionBackgroundColor(0xFF212121);
GPHCustomTheme.INSTANCE.setMoreByYouBackgroundColor(0xFFF1F1F1);
GPHCustomTheme.INSTANCE.setBackButtonColor(0xFFFFFFFF);

final GPHTheme theme = GPHTheme.Custom;
final GPHSettings settings = new GPHSettings();
settings.setTheme(theme);
final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings);
dialog.show(getSupportFragmentManager(), "giphy_dialog");
```

Version 2.3.5 offers a wider range of colors for customization. We have made modifications to the color names, but we have prepared a [visual scheme](https://github.com/Giphy/giphy-android-sdk/blob/main/assets/Android_theme_scheme.pdf) to assist you with this update.

### _Media Types_

Set the content type(s) you'd like to show by setting the `mediaTypeConfig` property, which is an array of `GPHContentType`s

```java
final GPHContentType[] contentTypes = new GPHContentType[5];
contentTypes[1] = GPHContentType.sticker;
contentTypes[2] = GPHContentType.gif;
contentTypes[3] = GPHContentType.text;
contentTypes[4] = GPHContentType.emoji;
settings.setMediaTypeConfig(contentTypes);
```

Set default `GPHContentType`:
```java
settings.setSelectedContentType(GPHContentType.emoji);
```

### _Recently Picked_

As of version `1.2.6` you can add an additional `GPHContentType` to you `mediaConfigs` array, called `GPHContentType.recents` which will automatically add a new tab with the recently picked GIFs and Stickers by the user. The tab will appear automatically if the user has picked any GIFs or Stickers.
```java
final GPHContentType[] contentTypes = new GPHContentType[5];
contentTypes[1] = GPHContentType.sticker;
contentTypes[2] = GPHContentType.gif;
contentTypes[3] = GPHContentType.text;
contentTypes[4] = GPHContentType.recents;
settings.setMediaTypeConfig(contentTypes);
```

Users can remove gifs from recents with a long-press on the GIF in the recents grid.

### _Additional Settings_

- **Confirmation screen**:  we provide the option to show a secondary confirmation screen when the user taps a GIF, which shows a larger rendition of the asset.
```java
settings.setShowConfirmationScreen(true); 
```

- **Rating**: set a specific content rating for the search results. Default `pg13`.
```java
settings.setRating(RatingType.pg13); 
```

- **Rendition**:  You can change the rendition type for the grid and also for the confirmation screen, if you are using it.  Default rendition is  `fixedWidth` for the grid and `original` for the confirmation screen.
```java
settings.setRenditionType(RenditionType.fixedWidth);
settings.setConfirmationRenditionType(RenditionType.original);
```

- **Checkered Background**: You can enable/disabled the checkered background for stickers and text media type.
```java
settings.setShowCheckeredBackground(true);
```

- **Stickers Column Count**: Customise the number of columns for stickers (Accepted values between 2 and 4).
```java
settings.setStickerColumnCount(3); 
```

- **Suggestions bar**: As of version `2.0.4` you can hide suggestions bar
```java
settings.setShowSuggestionsBar(false);
```

- **Image Format**: You can choose a file type for the grid.
```java
settings.setImageFormat(ImageFormat.WEBP); 
```

### _Presentation_
Show your `GiphyDialogFragment` using the `SupportFragmentManager` and watch as the GIFs start flowin'.

```java
gifsDialog.show(getSupportFragmentManager(), "gifs_dialog");
```

### _Events_

**Activity**

To handle GIF selection you need to implement the `GifSelectionListener` interface. If you are calling the GiphyDialogFragment from an activity instance, it is recommended that your activity implements the interface `GifSelectionListener`. When using this approach, the Giphy dialog will check at creation time, if the activity is implementing the `GifSelectionListener` protocol and set the activity as a callback, if no other listeners are set programatically.
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

##### GifSelectionListener technical note
As the `GiphyDialogFragment` is based on `DialogFragment`, this means that if the dialog is recreated after a process death caused by the Android System, your listener will not have a change to get set again so gif delivery will fail for that session.
To prevent such problems, we also implemented the android `setTargetFragment` API, to allow callbacks to be made reliably to the caller fragment in the event of process death.

- Option B: `GiphyDialogFragment` also implements support for the `setTargetFragment` API. If it detects a target fragment attached, **it will deliver the content to the target fragment and ignore the `GifSelectionListener`**. Response data will contain the `Media` object selected and the search term used to discover that `Media`.
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

From there, it's up to you to decide what to do with the GIF.

### _GPHMediaView_

Create a `GPHMediaView` to display the media. Optionaly, you can pass a rendition type to be loaded.

```java
GPHMediaView mediaView = new GPHMediaView(context);
mediaView.setMedia(media, RenditionType.original, null);
```

Use the media's aspectRatio property to size the view:
```java
float aspectRatio = MediaExtensionKt.getAspectRatio(media); 
```

You can populate a `GPHMediaView` with a media `id` like so:
```java
mediaView.setMediaWithId(media.getId(), RenditionType.downsized, null, (result, e) -> {
    if (e != null) {
        //your code here
    }
    return null;
});
```

### _Media IDs_

In a messaging app context, you may want to send media `id`s rather than `Media` objects or image assets.

Obtain a `Media`'s `id` property via `media.id`

On the receiving end, obtain a `Media` from the `id` like so:

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

### _Caching_
We use `Fresco` for GIF/WebP playback, which caches media assets itself.
You can provide your own cache config using `GiphyFrescoHandler`. See the [Fresco initialization](#fresco-initialization) section.
```java
@Override
public void handle(@NonNull ImagePipelineConfig.Builder imagePipelineConfigBuilder) {
  imagePipelineConfigBuilder
    .setMainDiskCacheConfig(
      DiskCacheConfig.newBuilder(DemoActivityJava.this)
        .setMaxCacheSize(150)
        .setMaxCacheSizeOnLowDiskSpace(50)
        .setMaxCacheSizeOnVeryLowDiskSpace(10)
        .build()
    );
}
```

#### *Dependencies*
- [Fresco](https://github.com/facebook/fresco): GIF/WebP playback <br>
- *Removed starting from SDK v2.2.0* [ExoPlayer](https://github.com/google/ExoPlayer): Clip playback <br>
- [Timber](https://github.com/JakeWharton/timber): Logger <br>
- [Lottie](https://github.com/JakeWharton/timber): Animations <br>

#### *Buttons*

Download the Sketch file [here](https://s3.amazonaws.com/sdk.mobile.giphy.com/design/GIPHY-SDK-UI-Kit.sketch) if you're looking for a great button icon to prompt the GIPHY SDK experience.

## Grid-Only and GiphyGridView

The following section refers to the Grid-Only solution of the SDK. Learn more [here](https://developers.giphy.com/docs/sdk#grid)

_See the [Template section](#giphydialogfragment) for template setup instructions._

If you require a more flexible experience, use the `GiphyGridView` instead of the `GiphyDialogFragment` to load and render GIFs.

`GiphyGridView` properties:
- **orientation** - tells the scroll direction of the grid. (e.g. `GiphyGridView.HORIZONTAL`, `GiphyGridView.VERTICAL`)
- **spanCount** - number of lanes in the grid
- **cellPadding** - spacing between rendered GIFs
- **showViewOnGiphy** - enables/disables the `Show on Giphy` action in the long-press menu
- **showCheckeredBackground** - use a checkered background (for stickers only)
- **imageFormat** - choose a file type for the grid
- **fixedSizeCells** - display content in equally sized cells (for stickers only)

## GiphyGridView: GPHContent
`GPHContent` contains all query params that necessary to load GIFs.
- **mediaType** - media type that should be loaded (e.g. `MediaType.gif`)
- **requestType** - tells the controller if the query should look for a trending feed of gifs or perform a search action (e.g. `GPHRequestType.trending`)
- **rating** - minimum rating (e.g. `RatingType.pg13`)
- **searchQuery**:- custom search input (e.g. `cats`)

Use one of the convenience methods avaiable in the `GPHContent` in order to create a query.

#### Trending
- `GPHContent.Companion.trending(MediaType.gif, RatingType.pg13);`
- `GPHContent.Companion.getTrendingGifs();`, `GPHContent.Companion.getTrendingStickers();`, etc.

#### Search
```java
GPHContent.Companion.searchQuery(search: String, MediaType.gif, RatingType.pg13);
```

#### Emoji
```java
GPHContent.Companion.getEmoji();
```

#### Recents

Show GIFs that the user has previously picked.
```java
GPHContent.Companion.getRecents();
```

Only show a "recents" tab if there are any recents. Get the number of recents via:
```java 
Giphy.INSTANCE.getRecents().getCount();
```

Optionally, we also provide the option to clear the set of recents:
```java
Giphy.INSTANCE.getRecents().clear();
```
Users can remove gifs from recents with a long-press on the GIF in the recents grid.

### Updating the content
After you have defined your query using a `GPHContent` object, to load the media content pass this object to `GiphyGridView`
```java
GPHContent gifsContent = GPHContent.Companion.searchQuery("cats", MediaType.gif, RatingType.pg13);
gifsGridView.setContent(gifsContent);
```

### Integrating the `GiphyGridView`

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
```java
gifsGridView.setContent(GPHContent.Companion.searchQuery("dogs", MediaType.gif, RatingType.pg13));
```

#### Method B
Create a `GiphyGridView` and set it's UI properties
```java
GiphyGridView gridView = null;
gridView.setDirection(GiphyGridView.HORIZONTAL);
gridView.setSpanCount(3);
gridView.setCellPadding(20);
```

Add the `GiphyGridView` to your layout and start making queries.
```java
parentView.addView(gridView);
```

### Customise the GIF Loading
You can customise the loading experience of GIFs in the `GridView` by using the `GiphyLoadingProvider` class.
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

### Callbacks
In order to interact with the `GiphyGridView` you can apply the following callbacks

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

### Data Collected by our SDK

_We deeply respect the privacy of your users and only collect anonymized information about GIPHY use in order to improve the service. If you have any questions or concerns about GIPHY’s data collection practices, please reach out to developers@giphy.com_

| Title | Description | Data use | Linked to user | Tracking |
| --- | --- | --- | --- | --- | 
| Search History | Information about searches performed in the app | Analytics, Product Personalization, App Functionality | No | No |
| Product Interaction | Such as app launches, taps, clicks, scrolling information, music listening data, video views, saved place in a game, video, or song, or other information about how the user interacts with the app | Analytics, Product Personalization, App Functionality| No | No |

### Proguard
The GIPHY UI SDK exposes the `consumer-proguard.pro` rules for proguard/R8 so there is no need to add anything to your projects proguard rules.
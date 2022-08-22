## GIPHY Clips: GIFs with Sound

Introducing GIPHY Clips, aka GIFs with Sound. 

Millions of people use GIPHY to communicate and express themselves every day.  GIPHY Clips is our newest content format at the intersection of GIFs and Video. 

The Clips Library is built with all of the unforgettable quotes, cultural moments, reactions and characters that we need to express how we are feeling, what we think and who we are. Everyday you’ll find new Clips from the biggest names in Entertainment, Sports, News, Pop Culture and viral moments. If your favorite quote isn’t a Clip yet, it will be soon...

Integrating the GIPHY Clips SDK will allow your users to seamlessly express themselves with this new format, all while staying in the experience in your app. 

### Requirements

- GIPHY SDK v2.1.2 (or above)  
- GIPHY Clips is available for integration into our community of messaging and social apps for users to search and share.  Clips is not approved to be integrated into creation experience where derivative works can be created. 

### Setting Up ExoPlayer

The GIPHY SDK uses [`ExoPlayer`](https://github.com/google/ExoPlayer) to power video playback. As of version v2.2.0 of the GIPHY SDK, ExoPlayer must be manually added as a dependency. See below for directions prior to v2.2.

<details>
  <summary>SDK v2.1.x:</summary>
The SDK has `Exoplayer` video cache setup.
It's enabled by default: the `videoCacheMaxBytes` value must be greater than 0, otherwise, the SDK will skip cache initialization and [Clips](https://github.com/Giphy/giphy-android-sdk/blob/main/clips.md) won't work.

```kotlin
Giphy.configure(
  videoCacheMaxBytes: 100 * 1024 * 1024
)
``` 
</details>


#### Using clips in SDK v2.2.0 (or above):

We removed the `ExoPlayer` dependency as of version `2.2.0` because of issues resulting from versioning conflicts, and so developers not using the Clips content type in their GIPHY integration wouldn't have to adopt this dependency.

Manually add the `ExoPlayer` dependency to your project like so:

```
implementation("com.google.android.exoplayer:exoplayer-core:2.18.1")
implementation("com.google.android.exoplayer:exoplayer-ui:2.18.1")
```

Initialize the [VideoCache](https://github.com/Giphy/giphy-android-sdk/blob/main/app/src/main/java/com/giphy/sdk/uidemo/VideoPlayer/VideoCache.kt) to use `Clips`:

```
VideoCache.initialize(this, 100 * 1024 * 1024)
Giphy.configure(this, YOUR_API_KEY, true)
```

Provide a `GPHAbstractVideoPlayer` implementation, which is required by the GIPHY SDK to play Clips.

We prepared an example based on the `ExoPlayer` 2.18.1 version - this may need to be modified for different versions of ExoPlayer:
- [VideoPlayerExoPlayer2181Impl](https://github.com/Giphy/giphy-android-sdk/blob/main/app/src/main/java/com/giphy/sdk/uidemo/VideoPlayer/VideoPlayerExoPlayer2181Impl.kt)

```
 val dialog = GiphyDialogFragment.newInstance(
    settings.copy(selectedContentType = contentType),
    videoPlayer = { playerView, repeatable, showCaptions ->
        VideoPlayerExoPlayer2181Impl(playerView, repeatable, showCaptions)
    }
 )
```


### Showing Clips in the GiphyDialogFragment

Add the new  `.clips`  `GPHContentType` to your `mediaTypeConfig` array.
```
settings.mediaTypeConfig = arrayOf(GPHContentType.gif, GPHContentType.sticker, GPHContentType.clips) 
```

### New MediaType: .video

The new  `video` type signifies that a `Media` instance is a GIPHY Clip, and is intended to be played back as a video with sound.

```
if (media.type == MediaType.video) {
 
}

when (mediaType) {                
    MediaType.video -> println("clip")
```

---

<details>
  <summary>Using clips in SDK v2.1.x:</summary>


#### GPHVideoPlayer + GPHVideoPlayerView

Playing back a Clips video asset in your Android app is easy thanks to Google’s `ExoPlayer`, an application level media player for Android.

Giphy SDK already comes with `ExoPlayer` dependency and provides a wrapper around `ExoPlayer` to ease `Clips` integration:

Create `GPHVideoPlayer` instance.

Attach the player to `GPHVideoPlayerView`. There are two ways to do that:
- using `GPHVideoPlayer` constructor
- pass as param of `GPHVideoPlayer.loadMedia` function.

Prepare the player with a `Media` item to play: `GPHVideoPlayer.loadMedia(media)`

Release the player by calling `GPHVideoPlayer.onDestroy()` func when done.


Create and load a `GPHVideoPlayer + GPHVideoPlayerView` with a `GPHMedia`
```
val playerView = GPHVideoPlayerView(context)
val player = GPHVideoPlayer(playerView, true)
player.loadMedia(media)
```

Pause, Resume, Mute, Unmute: 

```
GPHVideoPlayer.onPause()  
videoView.onResume()  
GPHVideoPlayer.setVolume(audioVolume: 0) 
GPHVideoPlayer.setVolume(audioVolume: 1) 
```

To subscribe to `GPHVideoPlayer` events:
```
GPHVideoPlayer.addListener(GPHPlaybackStateListener)
```

It's preferable to use only one `GPHVideoPlayer` instance and share it between `GPHVideoPlayerView`(s):
```
val playerView = GPHVideoPlayerView(context)
val player = GPHVideoPlayer(null, true)
player.loadMedia(media, view = playerView)
```
</details>

---

#### To use GPHAbstractVideoPlayer + GPHVideoPlayerView in grids

Attach the player to `GPHVideoPlayerView`. There are two ways to do that:
- using `GPHAbstractVideoPlayer` implementation constructor.
```
  videoPlayer = VideoPlayerExoPlayer2181Impl(videoPlayerView, true)
 ```
- pass as param of `GPHAbstractVideoPlayer.loadMedia` function.
```
  player.loadMedia(media, view = viewBinding.videoPlayerView)
```  

Prepare the player with a `Media` item to play: `GPHAbstractVideoPlayer.loadMedia(media)`
```
  val playerView = GPHVideoPlayerView(context)  
  val videoPlayer = VideoPlayerExoPlayer2181Impl(null, true)
  videoPlayer.loadMedia(media, view = viewBinding.videoPlayerView)
  
  OR
  
  val playerView = GPHVideoPlayerView(context)
  val videoPlayer = VideoPlayerExoPlayer2181Impl(videoPlayerView, true)
  player.loadMedia(media)
```  

Release the player by calling `GPHAbstractVideoPlayer.onDestroy()`
```  
  override fun onDestroyView() {
    super.onDestroyView()
    videoPlayer?.onDestroy()
  }
```  

Pause, Resume, Mute, Unmute:

```
GPHAbstractVideoPlayer.onPause()  
GPHAbstractVideoPlayer.onResume()  
GPHAbstractVideoPlayer.setVolume(audioVolume: 0) 
GPHAbstractVideoPlayer.setVolume(audioVolume: 1) 
```

To subscribe to `GPHAbstractVideoPlayer` events:
```
GPHAbstractVideoPlayer.addListener(GPHPlaybackStateListener)
```

It's preferable to use only one `GPHAbstractVideoPlayer` implementation instance and share it between `GPHVideoPlayerView`(s):
```
val playerView = GPHVideoPlayerView(context)
val player = VideoPlayerExoPlayer2181Impl(null, true)
player.loadMedia(media, view = playerView)
```

---

### Sending Clips & Renditions

As with sending / storing GIFs and Stickers, it's best practice to use GIPHY IDs to represent GIPHY Clips, rather than asset urls, and use the `gifByID` or `gifsByID` endpoints to retrieve the associated `Media`(s).

Video urls (`.mp4`) may be accessed directly within the `video` property (with type `Video`) of the encompassing `Media`.


### Rendition Limitations

Certain renditions (cases of the `RenditionType` enum) are not available for Clips. These include:

- `preview`
- `previewGif`
- `looping`
- `fixedWidthSmall`
- `fixedWidthSmallStill`
- `fixedHeightSmall`
- `fixedHeighSmallStill`
- `downsizedSmall`
- `downsizedStill`
- `downsized`

As a result, if you set the `renditionType` property of `GPHSettings` or `GiphyGridView` to any of these, clips previews may not play back correctly in the grid.

To account for this limitation, we created a new property specifically for clips call `clipsPreviewRenditionType` which is available as a property of both `GPHSettings` and `GiphyGridView`. Setting this property to one of the above `RenditionType` options will throw an exception.

As with `renditionType` the default for `clipsPreviewRenditionType` is the `RenditionType` option  `.fixedWidth`.

### Showing Clips in the GiphyGridView

Display silent Clips previews in the `GiphyGridView` collection view in the same way you would with GIFs and stickers.

```
val trending = GPHContent.trendingVideos  
val search = GPHContent.searchQuery("hello", MediaType.video)
```

We strongly recommend providing your users with the option to play back the clip, including audio, before enabling them to send or share the clip asset, as is the case with the existing experience offered by the `GiphyDialogFragment`. This can be accomplished by presenting a `GPHVideoPlayerView` following user selection of a clip preview.
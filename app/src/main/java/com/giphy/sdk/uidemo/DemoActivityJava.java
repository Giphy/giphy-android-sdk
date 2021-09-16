package com.giphy.sdk.uidemo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.themes.GPHTheme;
import com.giphy.sdk.ui.themes.GridType;
import com.giphy.sdk.ui.views.GPHMediaView;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.giphy.sdk.uidemo.databinding.ActivityDemoJavaBinding;
import java.util.Objects;

public class DemoActivityJava extends AppCompatActivity {

    private ActivityDemoJavaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Giphy.INSTANCE.configure(DemoActivityJava.this, "YOUR_API_KEY", false);
        binding = ActivityDemoJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.launchGiphyBtn.setOnClickListener(view -> show(true, new GiphyDialogFragment.GifSelectionListener() {
            @Override
            public void didSearchTerm(@NotNull String s) {
            }

            @Override
            public void onDismissed(@NotNull GPHContentType gphContentType) {
            }

            @Override
            public void onGifSelected(@NotNull Media media, @Nullable String s, @NotNull GPHContentType gphContentType) {
                // To fetch a particular id
                /*GPHCore.INSTANCE.gifById(media.getId(), (mediaResponse, throwable) -> {
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);
                    Glide.with(DemoActivityJava.this).load(mediaResponse.getData().getImages().getOriginal().getGifUrl()).into(imageView);
                    return null;
                });*/

                // you shouldn't provide context, this is for internal needs
                /*GPHCore.INSTANCE.gifsByIds(Arrays.asList(media.getId()) , null, (mediaResponse, throwable) -> {
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setVisibility(View.VISIBLE);


                        if (mediaResponse.getData().size() > 0) {
                            Glide.with(DemoActivityJava.this).
                                    load(mediaResponse.getData().get(0).
                                            getImages().getOriginal().getGifUrl()).into(imageView);
                        }

                    return null;
                });*/

                // To use the selected media:
                boolean useGifView = false;
                GPHMediaView gifView = binding.gifView;
                ImageView imageView = binding.imageView;
                gifView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                if (useGifView) {
                    gifView.setVisibility(View.VISIBLE);
                    gifView.setBackgroundVisible(false);
                    gifView.setMedia(media, RenditionType.original, null);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(DemoActivityJava.this).load(Objects.requireNonNull(media.getImages().getOriginal()).getGifUrl()).into(imageView);
                }
            }
        }));
    }

    @NonNull
    public GiphyDialogFragment show(final  boolean withDarkTheme, @NonNull final  GiphyDialogFragment.GifSelectionListener listener)
    {
        final GPHTheme theme = (withDarkTheme)
                ? GPHTheme.Dark
                : GPHTheme.Light;

        final GPHSettings settings = new GPHSettings();
        settings.setTheme(theme);
        settings.setRating(RatingType.pg13);
        settings.setRenditionType(RenditionType.fixedWidth);
        settings.setGridType(GridType.waterfall);
        settings.setShowCheckeredBackground(false);

        final GPHContentType[] contentTypes = new GPHContentType[5];
        contentTypes[3] = GPHContentType.sticker;
        contentTypes[2] = GPHContentType.gif;
        contentTypes[4] = GPHContentType.text;
        contentTypes[1] = GPHContentType.emoji;
        contentTypes[0] = GPHContentType.recents;
        settings.setMediaTypeConfig(contentTypes);
        settings.setSelectedContentType(GPHContentType.emoji);

        settings.setGridType(GridType.waterfall);
        settings.setTheme(GPHTheme.Light);
        settings.setStickerColumnCount(3);
        final GiphyDialogFragment dialog = GiphyDialogFragment.Companion.newInstance(settings);
        dialog.setGifSelectionListener(listener);
        dialog.show(getSupportFragmentManager(), "giphy_dialog");

        return dialog;
    }
}
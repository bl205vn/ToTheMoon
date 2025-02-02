package com.khanhpham.tothemoon.datagen.sounds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.khanhpham.tothemoon.utils.helpers.CompactedLanguage;
import com.khanhpham.tothemoon.utils.helpers.ModUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Deprecated
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SoundDataProvider implements DataProvider {
    public static final ArrayList<CompactedLanguage> soundLanguages = new ArrayList<>();
    private final DataGenerator dataProvider;
    private final String modid;
    private final ArrayList<SerializedSoundEvent> sounds = new ArrayList<>();

    public SoundDataProvider(DataGenerator dataProvider, String modid) {
        this.dataProvider = dataProvider;
        this.modid = modid;
    }

    public void run(CachedOutput pCache) {
        this.registerSounds();
        Path path = this.dataProvider.getOutputFolder().resolve("assets/" + this.modid + "/sounds.json");
        sounds.forEach(sound -> {
            try {
                DataProvider.saveStable(pCache, sound.toJson(), path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected abstract void registerSounds();

    protected void add(SoundEvent sound, SoundSource source, String subtitleTranslate) {
        add(sound, source, subtitleTranslate, modLoc(sound.getLocation().getPath()));
    }

    private void add(SoundEvent sound, SoundSource source, String subtitleTranslate, String... sounds) {
        String soundPath = ModUtils.getPath(sound);
        String soundSubtitle = source.getName() + '.' + this.modid + "." + soundPath;
        SerializedSoundEvent event = new SerializedSoundEvent(soundPath, source.getName(), soundSubtitle, sounds);
        soundLanguages.add(new CompactedLanguage(soundSubtitle, subtitleTranslate));
        this.sounds.add(event);
    }

    protected void add(SoundEvent soundEvent, SoundSource source, int i, String subtitleTranslate) {
        String[] sounds = new String[i];
        String soundPath = ModUtils.getPath(soundEvent);
        for (int a = 1; a <= i; a++) {
            sounds[a - 1] = modLoc(soundPath + a);
        }

        this.add(soundEvent, source, subtitleTranslate, sounds);
    }

    private String modLoc(String path) {
        return this.modid + ":" + path;
    }

    @Override
    public final String getName() {
        return "Sounds - TTM";
    }

    public static final class SerializedSoundEvent {
        private static final JsonObject mainObject = new JsonObject();
        private final String soundPath;
        private final String[] sounds;
        private final String category;
        private final String subtitle;

        public SerializedSoundEvent(String soundPath, String category, String subtitle, String... sounds) {
            this.soundPath = soundPath;
            this.sounds = sounds;
            this.category = category;
            this.subtitle = subtitle;
        }

        private JsonObject toJson() {
            JsonObject soundObject = new JsonObject();
            soundObject.addProperty("category", this.category);

            JsonArray array = new JsonArray();
            for (String sound : this.sounds) {
                array.add(sound);
            }

            soundObject.add("sounds", array);
            soundObject.addProperty("subtitle", this.subtitle);
            mainObject.add(this.soundPath, soundObject);
            return mainObject;
        }
    }
}

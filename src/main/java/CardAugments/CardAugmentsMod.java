package CardAugments;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.cardmods.rare.SanctifiedMod;
import CardAugments.dynvars.DynamicDynamicVariableManager;
import CardAugments.util.TextureLoader;
import basemod.*;
import basemod.helpers.CardBorderGlowManager;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostCreateStartingDeckSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class CardAugmentsMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        PostCreateStartingDeckSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(CardAugmentsMod.class.getName());
    private static String modID;

    public static boolean isMintyLoaded;

    // Mod-settings settings. This is if you want an on/off savable button
    public static SpireConfig cardAugmentsConfig;
    public static String FILE_NAME = "CardsAugmentsConfig";

    public static final String ENABLE_MODS_SETTING = "enableMods";
    public static boolean enableMods = true; // The boolean we'll be setting on/off (true/false)

    public static final String MOD_PROBABILITY = "modChance";
    public static int modProbabilityPercent = 50;

    public static final String COMMON_WEIGHT = "commonWeight";
    public static int commonWeight = 5;

    public static final String UNCOMMON_WEIGHT = "uncommonWeight";
    public static int uncommonWeight = 3;

    public static final String RARE_WEIGHT = "rareWeight";
    public static int rareWeight = 1;

    public static final String MODIFY_STARTERS = "modifyStarters";
    public static boolean modifyStarters = false;

    public static final String ALLOW_ORBS = "allowOrbs";
    public static boolean allowOrbs = false;

    public static final String RARITY_BIAS = "rarityBias";
    public static int rarityBias = 0;

    //Cardmod Lists
    public static final ArrayList<AbstractAugment> commonMods = new ArrayList<>();
    public static final ArrayList<AbstractAugment> uncommonMods = new ArrayList<>();
    public static final ArrayList<AbstractAugment> rareMods = new ArrayList<>();

    //List of orbies
    public static final ArrayList<AbstractPlayer.PlayerClass> ORB_CHARS = new ArrayList<>(Collections.singletonList(AbstractPlayer.PlayerClass.DEFECT));


    //This is for the in-game mod settings panel.
    public static UIStrings uiStrings;
    public static String[] TEXT;
    public static String[] EXTRA_TEXT;
    private static final String AUTHOR = "Mistress Alison";
    
    // =============== INPUT TEXTURE LOCATION =================
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "CardAugmentsResources/images/Badge.png";
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================
    
    public CardAugmentsMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("CardAugments");
        
        logger.info("Done subscribing");

        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        Properties cardAugmentsDefaultSettings = new Properties();
        cardAugmentsDefaultSettings.setProperty(ENABLE_MODS_SETTING, Boolean.toString(enableMods));
        cardAugmentsDefaultSettings.setProperty(MOD_PROBABILITY, String.valueOf(modProbabilityPercent));
        cardAugmentsDefaultSettings.setProperty(COMMON_WEIGHT, String.valueOf(commonWeight));
        cardAugmentsDefaultSettings.setProperty(UNCOMMON_WEIGHT, String.valueOf(uncommonWeight));
        cardAugmentsDefaultSettings.setProperty(RARE_WEIGHT, String.valueOf(rareWeight));
        cardAugmentsDefaultSettings.setProperty(MODIFY_STARTERS, Boolean.toString(modifyStarters));
        cardAugmentsDefaultSettings.setProperty(ALLOW_ORBS, Boolean.toString(allowOrbs));
        cardAugmentsDefaultSettings.setProperty(RARITY_BIAS, String.valueOf(rarityBias));
        try {
            cardAugmentsConfig = new SpireConfig(modID, FILE_NAME, cardAugmentsDefaultSettings);
            enableMods = cardAugmentsConfig.getBool(ENABLE_MODS_SETTING);
            modProbabilityPercent = cardAugmentsConfig.getInt(MOD_PROBABILITY);
            commonWeight = cardAugmentsConfig.getInt(COMMON_WEIGHT);
            uncommonWeight = cardAugmentsConfig.getInt(UNCOMMON_WEIGHT);
            rareWeight = cardAugmentsConfig.getInt(RARE_WEIGHT);
            modifyStarters = cardAugmentsConfig.getBool(MODIFY_STARTERS);
            allowOrbs = cardAugmentsConfig.getBool(ALLOW_ORBS);
            rarityBias = cardAugmentsConfig.getInt(RARITY_BIAS);
        } catch (IOException e) {
            logger.error("Card Augments SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("Card Augments CONFIG OPTIONS LOADED:");

        logger.info("Done adding mod settings");
        
    }

    public static void registerOrbCharacter(AbstractPlayer.PlayerClass clz) {
        ORB_CHARS.add(clz);
    }

    public static void registerAugment(AbstractAugment a) {
        if (a instanceof DynvarCarrier) {
            DynamicDynamicVariableManager.registerDynvarCarrier((DynvarCarrier) a);
        }
        switch (a.getModRarity()) {
            case COMMON:
                commonMods.add(a);
                break;
            case UNCOMMON:
                uncommonMods.add(a);
                break;
            case RARE:
                rareMods.add(a);
                break;
        }
        CardBorderGlowManager.GlowInfo i = a.getGlowInfo();
        if (i != null) {
            CardBorderGlowManager.addGlowInfo(i);
        }
    }

    public static void setModID(String ID) {
        modID = ID;
    }
    
    public static String getModID() {
        return modID;
    }
    
    public static void initialize() {
        logger.info("========================= Initializing Card Augments. =========================");
        CardAugmentsMod cardAugmentsMod = new CardAugmentsMod();
        logger.info("========================= /Card Augments Initialized/ =========================");
    }
    
    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        //Minty be messing with my stuff
        isMintyLoaded = Loader.isModLoaded("mintyspire");

        //Grab the strings
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        TEXT = uiStrings.TEXT;
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], AUTHOR, EXTRA_TEXT[1], settingsPanel);

        //Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset = getSliderPosition(labelStrings.subList(1,5));
        labelStrings.clear();
        float currentYposition = 740f;
        float spacingY = 55f;

        //Used to set the unused self damage setting.
        ModLabeledToggleButton enableModsButton = new ModLabeledToggleButton(TEXT[0],400.0f - 40f, currentYposition - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(ENABLE_MODS_SETTING), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(ENABLE_MODS_SETTING, button.enabled);
            enableMods = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for probability of a mod being applied
        ModLabel probabilityLabel = new ModLabel(TEXT[1], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider probabilitySlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                0, 100, cardAugmentsConfig.getInt(MOD_PROBABILITY), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(MOD_PROBABILITY, Math.round(slider.getValue()));
            modProbabilityPercent = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for common mod weight
        ModLabel commonLabel = new ModLabel(TEXT[2], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider commonSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, cardAugmentsConfig.getInt(COMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(COMMON_WEIGHT, Math.round(slider.getValue()));
            commonWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for uncommon mod weight
        ModLabel uncommonLabel = new ModLabel(TEXT[3], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider uncommonSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, cardAugmentsConfig.getInt(UNCOMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(UNCOMMON_WEIGHT, Math.round(slider.getValue()));
            uncommonWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for rare mod weight
        ModLabel rareLabel = new ModLabel(TEXT[4], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider rareSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, cardAugmentsConfig.getInt(RARE_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(RARE_WEIGHT, Math.round(slider.getValue()));
            rareWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for bias weight
        ModLabel biasLabel = new ModLabel(TEXT[7], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider biasSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                0, 5, cardAugmentsConfig.getInt(RARITY_BIAS), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(RARITY_BIAS, Math.round(slider.getValue()));
            rarityBias = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used to modify starter cards
        ModLabeledToggleButton enableStarterModificationButton = new ModLabeledToggleButton(TEXT[5],400.0f - 40f, currentYposition - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(MODIFY_STARTERS), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(MODIFY_STARTERS, button.enabled);
            modifyStarters = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used to allow orbs without prismatic shard
        ModLabeledToggleButton enableAllowOrbsButton = new ModLabeledToggleButton(TEXT[6],400.0f - 40f, currentYposition - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(ALLOW_ORBS), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(ALLOW_ORBS, button.enabled);
            allowOrbs = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        settingsPanel.addUIElement(enableModsButton);
        settingsPanel.addUIElement(probabilityLabel);
        settingsPanel.addUIElement(probabilitySlider);
        settingsPanel.addUIElement(commonLabel);
        settingsPanel.addUIElement(commonSlider);
        settingsPanel.addUIElement(uncommonLabel);
        settingsPanel.addUIElement(uncommonSlider);
        settingsPanel.addUIElement(rareLabel);
        settingsPanel.addUIElement(rareSlider);
        settingsPanel.addUIElement(enableStarterModificationButton);
        settingsPanel.addUIElement(enableAllowOrbsButton);
        settingsPanel.addUIElement(biasLabel);
        settingsPanel.addUIElement(biasSlider);

        logger.info("Done loading badge Image and mod options");

        logger.info("Loading card mods...");

        new AutoAdd(modID)
                .packageFilter("CardAugments.cardmods")
                .any(AbstractAugment.class, (info, abstractAugment) -> {registerAugment(abstractAugment);});

        logger.info("Done loading card mods");

        logger.info("Setting up Dynamic Dynamic Variable Manager...");

        BaseMod.addDynamicVariable(DynamicDynamicVariableManager.instance);

        logger.info("Done");

    }

    //Get the longest text so all sliders are centered
    private float getSliderPosition (List<String> stringsToCompare) {
        float longest = 0;
        for (String s : stringsToCompare) {
            longest = Math.max(longest, FontHelper.getWidth(FontHelper.charDescFont, s, 1f /Settings.scale));
        }
        return longest + 40f;
    }
    
    // =============== / POST-INITIALIZE/ =================

    // ================ LOAD THE LOCALIZATION ===================

    private String loadLocalizationIfAvailable(String fileName) {
        if (!Gdx.files.internal(getModID() + "Resources/localization/" + Settings.language.toString().toLowerCase()+ "/" + fileName).exists()) {
            logger.info("Language: " + Settings.language.toString().toLowerCase() + ", not currently supported for " +fileName+".");
            return "eng" + "/" + fileName;
        } else {
            logger.info("Loaded Language: "+ Settings.language.toString().toLowerCase() + ", for "+fileName+".");
            return Settings.language.toString().toLowerCase() + "/" + fileName;
        }
    }

    // ================ /LOAD THE LOCALIZATION/ ===================

    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        // UIStrings
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/"+loadLocalizationIfAvailable("CardAugments-UI-Strings.json"));

        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/"+loadLocalizationIfAvailable("CardAugments-Power-Strings.json"));

        logger.info("Done editing strings");
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword

        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID()+"Resources/localization/"+loadLocalizationIfAvailable("CardAugments-Keyword-Strings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
            }
        }
    }

    @Override
    public void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass playerClass, CardGroup deck) {
        AbstractCard[] cardsToTest = {

        };
        AbstractAugment modToTest = null;
        if (modToTest != null) {
            for (AbstractCard c : cardsToTest) {
                if (modToTest.canRoll(c)) {
                    CardModifierManager.addModifier(c, modToTest.makeCopy());
                    deck.addToBottom(c);
                }
            }
        }
    }
}

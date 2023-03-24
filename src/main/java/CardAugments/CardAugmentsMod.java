package CardAugments;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.commands.Chimera;
import CardAugments.dynvars.DynamicDynamicVariableManager;
import CardAugments.patches.RolledModFieldPatches;
import CardAugments.ui.BiggerModButton;
import CardAugments.ui.CenteredModLabel;
import CardAugments.util.TextureLoader;
import basemod.*;
import basemod.abstracts.AbstractCardModifier;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.CardBorderGlowManager;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import javassist.CtBehavior;
import mintySpire.patches.cards.betterUpdatePreview.CardFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SpireInitializer
public class CardAugmentsMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        EditKeywordsSubscriber {
    public static final Logger logger = LogManager.getLogger(CardAugmentsMod.class.getName());
    private static String modID;

    public static boolean isMintyLoaded;

    public static SpireConfig cardAugmentsConfig;
    public static SpireConfig cardAugmentsCrossoverConfig;
    public static String FILE_NAME = "CardsAugmentsConfig";
    public static String CROSSOVER_FILE_NAME = "CardsAugmentsCrossoverConfig";

    public static final String ENABLE_MODS_SETTING = "enableMods";
    public static boolean enableMods = true;

    public static final String MOD_PROBABILITY = "modChance";
    public static int modProbabilityPercent = 10;

    public static final String COMMON_WEIGHT = "commonWeight";
    public static int commonWeight = 5;

    public static final String UNCOMMON_WEIGHT = "uncommonWeight";
    public static int uncommonWeight = 3;

    public static final String RARE_WEIGHT = "rareWeight";
    public static int rareWeight = 1;

    public static final String MODIFY_STARTERS = "modifyStarters";
    public static boolean modifyStarters = false;

    public static final String MODIFY_INSTANT_OBTAIN = "modifyInstantObtain";
    public static boolean modifyInstantObtain = false;

    public static final String MODIFY_SHOP = "modifyShop";
    public static boolean modifyShop = false;

    public static final String ALLOW_ORBS = "allowOrbs";
    public static boolean allowOrbs = false;

    public static final String RARITY_BIAS = "rarityBias";
    public static int rarityBias = 1;

    public static final String EVENT_ADDONS = "eventAddons";
    public static boolean eventAddons = true;

    //Cardmod Lists
    public static final ArrayList<AbstractAugment> commonMods = new ArrayList<>();
    public static final ArrayList<AbstractAugment> uncommonMods = new ArrayList<>();
    public static final ArrayList<AbstractAugment> rareMods = new ArrayList<>();
    public static final HashMap<String, AbstractAugment> modMap = new HashMap<>();
    public static final HashMap<AbstractAugment, String> crossoverMap = new HashMap<>();
    public static final HashMap<String, String> crossoverLabelMap = new HashMap<>();
    public static final HashMap<String, Integer> crossoverSizeMap = new HashMap<>();
    public static final HashMap<String, Boolean> crossoverEnableMap = new HashMap<>();
    public static final String UNMANAGED_ID = "UnmanagedChimeraID";
    //List of orbies
    public static final ArrayList<AbstractPlayer.PlayerClass> ORB_CHARS = new ArrayList<>(Collections.singletonList(AbstractPlayer.PlayerClass.DEFECT));


    //This is for the in-game mod settings panel.
    public static UIStrings uiStrings;
    public static UIStrings crossoverUIStrings;
    public static String[] TEXT;
    public static String[] EXTRA_TEXT;
    private static final String AUTHOR = "Mistress Alison";

    public static ModPanel settingsPanel;
    public static ModLabel noCrossoverLabel;
    public static HashMap<Integer, ArrayList<IUIElement>> pages = new HashMap<>();
    public static final float LAYOUT_Y = 740f;
    public static final float LAYOUT_X = 400f;
    public static final float SPACING_Y = 52f;
    public static final float FULL_PAGE_Y = (SPACING_Y * 10);
    public static float deltaY = 0;
    public static int currentPage = 0;
    
    // =============== INPUT TEXTURE LOCATION =================
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "CardAugmentsResources/images/Badge.png";
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, INITIALIZE =================
    
    public CardAugmentsMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("CardAugments");
        
        logger.info("Done subscribing");

        logger.info("Adding mod settings");
        Properties cardAugmentsDefaultSettings = new Properties();
        cardAugmentsDefaultSettings.setProperty(ENABLE_MODS_SETTING, Boolean.toString(enableMods));
        cardAugmentsDefaultSettings.setProperty(MOD_PROBABILITY, String.valueOf(modProbabilityPercent));
        cardAugmentsDefaultSettings.setProperty(COMMON_WEIGHT, String.valueOf(commonWeight));
        cardAugmentsDefaultSettings.setProperty(UNCOMMON_WEIGHT, String.valueOf(uncommonWeight));
        cardAugmentsDefaultSettings.setProperty(RARE_WEIGHT, String.valueOf(rareWeight));
        cardAugmentsDefaultSettings.setProperty(MODIFY_STARTERS, Boolean.toString(modifyStarters));
        cardAugmentsDefaultSettings.setProperty(ALLOW_ORBS, Boolean.toString(allowOrbs));
        cardAugmentsDefaultSettings.setProperty(RARITY_BIAS, String.valueOf(rarityBias));
        cardAugmentsDefaultSettings.setProperty(MODIFY_INSTANT_OBTAIN, Boolean.toString(modifyInstantObtain));
        cardAugmentsDefaultSettings.setProperty(MODIFY_SHOP, Boolean.toString(modifyShop));
        cardAugmentsDefaultSettings.setProperty(EVENT_ADDONS, Boolean.toString(eventAddons));
        try {
            cardAugmentsConfig = new SpireConfig(modID, FILE_NAME, cardAugmentsDefaultSettings);
            cardAugmentsCrossoverConfig = new SpireConfig(modID, CROSSOVER_FILE_NAME);
            enableMods = cardAugmentsConfig.getBool(ENABLE_MODS_SETTING);
            modProbabilityPercent = cardAugmentsConfig.getInt(MOD_PROBABILITY);
            commonWeight = cardAugmentsConfig.getInt(COMMON_WEIGHT);
            uncommonWeight = cardAugmentsConfig.getInt(UNCOMMON_WEIGHT);
            rareWeight = cardAugmentsConfig.getInt(RARE_WEIGHT);
            modifyStarters = cardAugmentsConfig.getBool(MODIFY_STARTERS);
            allowOrbs = cardAugmentsConfig.getBool(ALLOW_ORBS);
            rarityBias = cardAugmentsConfig.getInt(RARITY_BIAS);
            modifyInstantObtain = cardAugmentsConfig.getBool(MODIFY_STARTERS);
            modifyShop = cardAugmentsConfig.getBool(MODIFY_SHOP);
            eventAddons = cardAugmentsConfig.getBool(EVENT_ADDONS);
        } catch (IOException e) {
            logger.error("Card Augments SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("Card Augments CONFIG OPTIONS LOADED:");

        logger.info("Done adding mod settings");
        
    }

    public static void registerMod(String modID, String labelText) {
        if (!cardAugmentsCrossoverConfig.has(modID)) {
            logger.info("Created config for modID: "+modID);
            cardAugmentsCrossoverConfig.setBool(modID, true);
        }
        crossoverEnableMap.put(modID, cardAugmentsCrossoverConfig.getBool(modID));
        crossoverLabelMap.put(modID, labelText);
        ModLabeledToggleButton enableCrossoverButton = new ModLabeledToggleButton(labelText,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsCrossoverConfig.getBool(modID), settingsPanel,
                (label) -> {
                    label.text = crossoverLabelMap.get(modID) + " (" + crossoverSizeMap.get(modID) + " " + crossoverUIStrings.TEXT[2] + ")";
                },
                (button) -> {
                    cardAugmentsCrossoverConfig.setBool(modID, button.enabled);
                    crossoverEnableMap.put(modID, button.enabled);
                    try {cardAugmentsCrossoverConfig.save();} catch (IOException e) {e.printStackTrace();}
                });
        registerUIElement(enableCrossoverButton);
        logger.info("Loaded config for modID: "+modID);
    }

    public static void registerOrbCharacter(AbstractPlayer.PlayerClass clz) {
        ORB_CHARS.add(clz);
    }

    @Deprecated
    public static void registerAugment(AbstractAugment a) {
        logger.warn("Augment "+ a +" does not include a modID, Chimera Cards can not manage the spawning of this mod! Please call registerMod then pass your modID when registering augments.");
        registerAugment(a, UNMANAGED_ID);
    }

    public static void registerAugment(AbstractAugment a, String modID) {
        if (!Objects.equals(modID, UNMANAGED_ID) && !crossoverEnableMap.containsKey(modID)) {
            logger.warn("Augment "+a+" with modID "+modID+" does not match any registered configs, Chimera Cards can not manage the spawning of this mod! Please call registerMod with your ID to set up a config.");
        }
        crossoverMap.put(a, modID);
        crossoverSizeMap.merge(modID, 1, Integer::sum);
        if (a instanceof DynvarCarrier) {
            DynamicDynamicVariableManager.registerDynvarCarrier((DynvarCarrier) a);
        }
        if (!a.identifier(null).equals("")) {
            modMap.put(a.identifier(null), a);
        } else {
            logger.warn("Augment "+ a +" does not set an identifier, Chimera Cards can not add this mod via console commands!");
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
        //Minty be messing with my stuff
        isMintyLoaded = Loader.isModLoaded("mintyspire");

        logger.info("Setting up dev commands");

        ConsoleCommand.addCommand("chimera", Chimera.class);

        logger.info("Done setting up dev commands");

        logger.info("Setting up Dynamic Dynamic Variable Manager...");

        BaseMod.addDynamicVariable(DynamicDynamicVariableManager.instance);

        logger.info("Done");

    }

    private static void setupSettingsPanel() {
        logger.info("Loading badge image and mod options");
        settingsPanel = new ModPanel();

        //Grab the strings
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        crossoverUIStrings = CardCrawlGame.languagePack.getUIString(makeID("CrossoverConfig"));
        EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        TEXT = uiStrings.TEXT;
        // Create the Mod Menu

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], AUTHOR, EXTRA_TEXT[1], settingsPanel);

        //Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset = getSliderPosition(labelStrings.subList(1,5));
        labelStrings.clear();

        //Enable or disable the mod entirely.
        ModLabeledToggleButton enableModsButton = new ModLabeledToggleButton(TEXT[0],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(ENABLE_MODS_SETTING), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(ENABLE_MODS_SETTING, button.enabled);
            enableMods = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for probability of a mod being applied
        ModLabel probabilityLabel = new ModLabel(TEXT[1], LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider probabilitySlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 100, cardAugmentsConfig.getInt(MOD_PROBABILITY), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(MOD_PROBABILITY, Math.round(slider.getValue()));
            modProbabilityPercent = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for common mod weight
        ModLabel commonLabel = new ModLabel(TEXT[2], LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider commonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                1, 10, cardAugmentsConfig.getInt(COMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(COMMON_WEIGHT, Math.round(slider.getValue()));
            commonWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for uncommon mod weight
        ModLabel uncommonLabel = new ModLabel(TEXT[3], LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider uncommonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                1, 10, cardAugmentsConfig.getInt(UNCOMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(UNCOMMON_WEIGHT, Math.round(slider.getValue()));
            uncommonWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for rare mod weight
        ModLabel rareLabel = new ModLabel(TEXT[4], LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider rareSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                1, 10, cardAugmentsConfig.getInt(RARE_WEIGHT), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(RARE_WEIGHT, Math.round(slider.getValue()));
            rareWeight = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for bias weight
        ModLabel biasLabel = new ModLabel(TEXT[7], LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider biasSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 5, cardAugmentsConfig.getInt(RARITY_BIAS), "%.0f", settingsPanel, slider -> {
            cardAugmentsConfig.setInt(RARITY_BIAS, Math.round(slider.getValue()));
            rarityBias = Math.round(slider.getValue());
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used to modify starter cards
        ModLabeledToggleButton enableStarterModificationButton = new ModLabeledToggleButton(TEXT[5],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(MODIFY_STARTERS), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(MODIFY_STARTERS, button.enabled);
            modifyStarters = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used to modify starter cards
        ModLabeledToggleButton enableInstantObtainModificationButton = new ModLabeledToggleButton(TEXT[8],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(MODIFY_INSTANT_OBTAIN), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(MODIFY_INSTANT_OBTAIN, button.enabled);
            modifyInstantObtain = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used to modify starter cards
        ModLabeledToggleButton enableShopButton = new ModLabeledToggleButton(TEXT[9],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(MODIFY_SHOP), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(MODIFY_SHOP, button.enabled);
            modifyShop = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used to allow orbs without prismatic shard
        ModLabeledToggleButton enableAllowOrbsButton = new ModLabeledToggleButton(TEXT[6],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(ALLOW_ORBS), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(ALLOW_ORBS, button.enabled);
            allowOrbs = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used to allow orbs without prismatic shard
        ModLabeledToggleButton enableEventsButtom = new ModLabeledToggleButton(TEXT[10],LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                cardAugmentsConfig.getBool(EVENT_ADDONS), settingsPanel, (label) -> {}, (button) -> {
            cardAugmentsConfig.setBool(EVENT_ADDONS, button.enabled);
            eventAddons = button.enabled;
            try {cardAugmentsConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        registerUIElement(enableModsButton);
        registerUIElement(probabilityLabel, false);
        registerUIElement(probabilitySlider);
        registerUIElement(commonLabel, false);
        registerUIElement(commonSlider);
        registerUIElement(uncommonLabel, false);
        registerUIElement(uncommonSlider);
        registerUIElement(rareLabel, false);
        registerUIElement(rareSlider);
        registerUIElement(biasLabel, false);
        registerUIElement(biasSlider);
        registerUIElement(enableStarterModificationButton);
        registerUIElement(enableInstantObtainModificationButton);
        registerUIElement(enableShopButton);
        registerUIElement(enableAllowOrbsButton);
        registerUIElement(enableEventsButtom);

        CenteredModLabel pageLabel = new CenteredModLabel(crossoverUIStrings.TEXT[1], Settings.WIDTH/2f/Settings.xScale, 830f, settingsPanel, l -> {
            l.text = crossoverUIStrings.TEXT[1] + " " + (currentPage + 1) + "/" + (pages.size());
        });
        BiggerModButton leftButton = new BiggerModButton(Settings.WIDTH/2F/Settings.xScale - 100f - ImageMaster.CF_LEFT_ARROW.getWidth()/2F, 805f, -5f, ImageMaster.CF_LEFT_ARROW, settingsPanel, b -> {
            if (currentPage > 0) {
                previousPage();
            }
        });
        BiggerModButton rightButton = new BiggerModButton(Settings.WIDTH/2F/Settings.xScale + 100f - ImageMaster.CF_LEFT_ARROW.getWidth()/2F, 805f, -5f, ImageMaster.CF_RIGHT_ARROW, settingsPanel, b -> {
            if (currentPage < pages.size()-1) {
                nextPage();
            }
        });

        settingsPanel.addUIElement(pageLabel);
        settingsPanel.addUIElement(leftButton);
        settingsPanel.addUIElement(rightButton);

        logger.info("Done loading badge Image and mod options");

        logger.info("Loading card mods...");

        registerMod(modID, crossoverUIStrings.TEXT[0]);
        new AutoAdd(modID)
                .packageFilter("CardAugments.cardmods")
                .any(AbstractAugment.class, (info, abstractAugment) -> registerAugment(abstractAugment, modID));

        logger.info("Done loading card mods");
    }

    private static void registerUIElement(IUIElement elem) {
        registerUIElement(elem, true);
    }

    private static void registerUIElement(IUIElement elem, boolean decrement) {
        settingsPanel.addUIElement(elem);
        if (pages.isEmpty()) {
            pages.put(0, new ArrayList<>());
        }
        int page = pages.size()-1;
        pages.get(page).add(elem);
        elem.setY(elem.getY() - deltaY);
        elem.setX(elem.getX() + (page * Settings.WIDTH));
        if (decrement) {
            deltaY += SPACING_Y;
            if (deltaY > FULL_PAGE_Y) {
                deltaY = 0;
                pages.put(page+1, new ArrayList<>());
            }
        }
    }

    private static void nextPage() {
        for (ArrayList<IUIElement> elems : pages.values()) {
            for (IUIElement elem : elems) {
                elem.setX(elem.getX() - Settings.WIDTH);
            }
        }
        currentPage++;
    }

    private static void previousPage() {
        for (ArrayList<IUIElement> elems : pages.values()) {
            for (IUIElement elem : elems) {
                elem.setX(elem.getX() + Settings.WIDTH);
            }
        }
        currentPage--;
    }

    //Get the longest text so all sliders are centered
    private static float getSliderPosition(List<String> stringsToCompare) {
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

    private void loadLocalizedStrings(Class<?> stringClass, String fileName) {
        //Load English first
        BaseMod.loadCustomStringsFile(stringClass, modID + "Resources/localization/eng/"+fileName+".json");

        //Attempt loading localization
        if (!Settings.language.toString().equalsIgnoreCase("eng")) {
            String path = modID + "Resources/localization/" + Settings.language.toString().toLowerCase() + "/" + fileName + ".json";
            if (Gdx.files.internal(path).exists()) {
                BaseMod.loadCustomStringsFile(stringClass, path);
            }
        }
    }

    // ================ /LOAD THE LOCALIZATION/ ===================

    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        loadLocalizedStrings(UIStrings.class, "CardAugments-UI-Strings");
        loadLocalizedStrings(PowerStrings.class, "CardAugments-Power-Strings");
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
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID()+"Resources/localization/"+loadLocalizationIfAvailable("CardAugments-Keyword-Strings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    public static boolean isCrossOverEnabled(AbstractAugment m) {
        return crossoverEnableMap.getOrDefault(crossoverMap.getOrDefault(m, UNMANAGED_ID), true);
    }

    public static void rollCardAugment(AbstractCard c) {
        rollCardAugment(c, -1);
    }

    public static void rollCardAugment(AbstractCard c, int index) {
        if (enableMods && !RolledModFieldPatches.RolledModField.rolled.get(c) && AbstractDungeon.miscRng.random(99) < modProbabilityPercent) {
            applyWeightedCardMod(c, rollRarity(c.rarity), index);
        }
        RolledModFieldPatches.RolledModField.rolled.set(c, true);
    }

    public static AbstractAugment.AugmentRarity rollRarity(AbstractCard.CardRarity rarity) {
        int c = commonWeight;
        int u = uncommonWeight;
        int r = rareWeight;
        switch (rarity) {
            case BASIC:
            case COMMON:
                c += rarityBias;
                break;
            case UNCOMMON:
                u += rarityBias;
                break;
            case RARE:
                r += rarityBias;
                break;
        }
        int roll = AbstractDungeon.miscRng.random(c + u + r - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= c) < 0) {
            return AbstractAugment.AugmentRarity.COMMON;
        } else if (roll - u < 0) {
            return AbstractAugment.AugmentRarity.UNCOMMON;
        } else {
            return AbstractAugment.AugmentRarity.RARE;
        }
    }

    public static void applyWeightedCardMod(AbstractCard c, AbstractAugment.AugmentRarity rarity, int index) {
        ArrayList<AbstractAugment> validMods = new ArrayList<>();
        switch (rarity) {
            case COMMON:
                validMods.addAll(commonMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case UNCOMMON:
                validMods.addAll(uncommonMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case RARE:
                validMods.addAll(rareMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
        }
        if (!validMods.isEmpty()) {
            AbstractCardModifier m = validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
            CardModifierManager.addModifier(c, m);
            if (index != -1 && isMintyLoaded) {
                if (index < CardFields.SCVPopup.unupgradedCardRewards.get(CardCrawlGame.cardPopup).size()) {
                    CardModifierManager.addModifier(CardFields.SCVPopup.unupgradedCardRewards.get(CardCrawlGame.cardPopup).get(index), m);
                }
            }
        }
    }

    public static boolean canReceiveModifier(AbstractCard card) {
        for (AbstractAugment a : commonMods) {
            if (a.validCard(card)) {
                return true;
            }
        }
        for (AbstractAugment a : uncommonMods) {
            if (a.validCard(card)) {
                return true;
            }
        }
        for (AbstractAugment a : rareMods) {
            if (a.validCard(card)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<AbstractAugment> getAllValidMods(AbstractCard c) {
        ArrayList<AbstractAugment> validMods = new ArrayList<>();
        validMods.addAll(commonMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        validMods.addAll(uncommonMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        validMods.addAll(rareMods.stream().filter(m -> m.canRoll(c) && isCrossOverEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        return validMods;
    }

    public static AbstractAugment getTrulyRandomValidCardMod(AbstractCard c) {
        ArrayList<AbstractAugment> validMods = getAllValidMods(c);
        if (!validMods.isEmpty()) {
            return (AbstractAugment) validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
        }
        return null;
    }

    public static void applyTrulyRandomCardMod(AbstractCard c) {
        AbstractAugment a = getTrulyRandomValidCardMod(c);
        if (a != null) {
            CardModifierManager.addModifier(c, a);
        }
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "create")
    public static class PostLoadFontsPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void load() {
            setupSettingsPanel();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(AbstractCard.class, "initializeDynamicFrameWidths");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}

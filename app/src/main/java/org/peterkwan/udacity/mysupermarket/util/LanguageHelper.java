package org.peterkwan.udacity.mysupermarket.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

public class LanguageHelper {

    private static final String PREF_LANGUAGE_KEY = "lang";
    private static final int LOCALE_LANGUAGE_INDEX = 0;
    private static final int LOCALE_COUNTRY_INDEX = 1;

    public static Context onAttach(Context context, String defaultLanguage) {
        return setLanguage(context, defaultLanguage);
    }

    public static String getLanguage(Context context) {
        return getPreferredLanguage(context, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
    }

    private static Context setLanguage(Context context, String language) {
        savePreferredLanguage(context, language);

        if (SDK_INT >= N)
            return updateResources(context, language);
        else
            return updateResourceLegacy(context, language);
    }

    private static String getPreferredLanguage(Context context, String defaultLanguage, String defaultCountry) {
        return SharedPreferenceHelper.getPreference(context, PREF_LANGUAGE_KEY, defaultLanguage + "-" + defaultCountry);
    }

    private static void savePreferredLanguage(Context context, String language) {
        SharedPreferenceHelper.savePreference(context, PREF_LANGUAGE_KEY, language);
    }

    @TargetApi(N)
    private static Context updateResources(Context context, String language) {
        Configuration configuration = context.getResources().getConfiguration();

        String[] localeStringArray = language.split("-");
        if (localeStringArray.length >= 2) {
            LocaleList localeList = new LocaleList(new Locale(localeStringArray[LOCALE_LANGUAGE_INDEX], localeStringArray[LOCALE_COUNTRY_INDEX]));
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        }

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourceLegacy(Context context, String language) {
        String[] localeStringArray = language.split("-");
        if (localeStringArray.length >= 2) {
            Locale locale = new Locale(localeStringArray[LOCALE_LANGUAGE_INDEX], localeStringArray[LOCALE_COUNTRY_INDEX]);
            Locale.setDefault(locale);

            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.locale = locale;

            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        return context;
    }
}

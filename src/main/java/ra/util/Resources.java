package ra.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Resources {

    private static Logger LOG = Logger.getLogger(Resources.class.getName());

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(
            "i18n.displayStrings", LocaleUtil.currentLocale, new UTF8Control());

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String get(String key, Object... arguments) {
        return MessageFormat.format(Resources.get(key), arguments);
    }

    public static String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            LOG.warning("Missing resource for key: "+key);
            return key;
        }
    }
}

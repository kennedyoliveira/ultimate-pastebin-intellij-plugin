# Translation Contribution

##### Hello there! Thanks for considering helping with the translation!

### Supported Languages

Currently the plugin only supports the languages below:

- English (Native By Kennedy Oliveira - Owner and Plugin Developer)
- Português Brasileiro (By Aninha Pessoni)
- French (By Vinetos)
- Norwegian (By Kim Iversen [kimfy])

### Translation

To help with translation you have two options, doing all by yourself and sending a pull request to project, or only translating the messages file and sending to me, and i'll do the rest.

#### Translation Process (Doing all by yourself)

First, you need to copy the `resources/messages.properties` and translate to your desired language.

Obs: If your language has some special characters, like `é á Å Ø Æ`, please instead of using this characters access this link [Unicode Table](http://unicode-table.com/en/), search for the character, for example the `Æ`, click on it and there will be a `Unicode number: U+00C6`, you will get the part after `U+` and will use in the message file like `\u00C6` and it'll work just fine. See the image below:

![Example](https://www.evernote.com/l/AdL1RDAKNiFCxa2nQ988hVdnW1FO02q1l-AB/image.png)

After doing that, you'll rename the file including your language at end, for example, suppose you are translating to `japanese`,
then you 'll rename the file to `messages_jp.properties`.

With the message file translated, now it's time to tell the plugin that your language exists, to do it, go to the file 
`src/com/github/kennedyoliveira/ultimatepastebin/i18n/MessageBundle.java`, there you'll find a `static block` with a
`availableTranslations` map inside, there you'll put a new entry in the map where the key is the language that will show
in the language options on plugins settings and the value of map entry is the `Locale` representing your language locale.

See the example:

```java
package com.github.kennedyoliveira.ultimatepastebin.i18n;

import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurationService;
import com.intellij.CommonBundle;
import com.intellij.openapi.components.ServiceManager;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author kennedy
 */
public class MessageBundle {

    private final static Map<String, Locale> availableTranslations;

   // some code

    static {
        pasteBinConfigurationService = ServiceManager.getService(PasteBinConfigurationService.class);

        // Defines the available languages in the format of key = Name of the language, Value the locale used
        availableTranslations = new HashMap<>();

        availableTranslations.put("Portugues Brasileiro", new Locale.Builder().setLanguage("pt").setRegion("BR").build());
        availableTranslations.put("English", new Locale.Builder().setLanguage("en").build());
        
        // here you will put your new language
        availableTranslations.put("My Language", new Locale.Builder().setLanguage("lg").build());
    }
    
    // rest of the class omitted....
}
```

That's it! Now just send a pull request and i'll put your translation in the next release.

#### Simple Translation

If you don't understand java but wants to help, that is not a problem!

Just get the `message.properties` file, translate it, and send it back by e-mail to me at `kennedy.oliveira@outlook.com`,
i'll put the language in the plugin for you and release in the next version!

Thanks in advanced!

// MLKitTranslator: Translation is a no-op since only English is supported
object MLKitTranslator {
    /**
     * Translate text from source language to target language.
     * 
     * @param context Android context
     * @param text Text to translate
     * @param sourceLang Source language code
     * @param targetLang Target language code
     * @param onSuccess Callback with translated text
     * @param onFailure Callback with exception if translation fails
     */
    fun translate(
        context: android.content.Context,
        text: String,
        sourceLang: String,
        targetLang: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        onSuccess(text) // No translation, just return original text
    }

    /**
     * Get a translator instance for the given languages.
     * 
     * @param sourceLang Source language code
     * @param targetLang Target language code
     * @return Translator instance (currently a no-op)
     */
    fun getTranslator(sourceLang: String, targetLang: String): Any {
        return object {} // No-op
    }

    /**
     * Get the ML Kit language code for the given app language code.
     * 
     * @param appLangCode App language code
     * @return ML Kit language code (currently always English)
     */
    fun getMLKitLangCode(appLangCode: String): String {
        return "en" // Always return English
    }
}

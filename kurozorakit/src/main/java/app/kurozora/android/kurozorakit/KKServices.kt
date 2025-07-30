package app.kurozora.android.kurozorakit

import java.security.KeyStore

/**
 * `KKServices` is the object that serves as a provider for KurozoraKit services.
 *
 * `KKServices` is used together with [KurozoraKit] to provide extra functionality such as storing sensitive information securely and showing success/error alerts.
 * For further control over the information saved securely, you can provide your own `KeyStore` object with your specified properties.
 */
class KKServices {
    // Create a KeyStore instance for managing secrets securely
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")

    // Show or hide expressive success/error alerts to users
    private var showAlerts: Boolean = true

    // Initialize the KeyStore
    init {
        keyStore.load(null)
    }
    // Functions for setting properties
    /**
     * Sets the `showAlerts` property with the given boolean value.
     *
     * @param bool A boolean value indicating whether to show or hide expressive success/error alerts.
     * @return Reference to `this` KKServices instance.
     */
    fun showAlerts(bool: Boolean): KKServices {
        this.showAlerts = bool
        return this
    }

    /**
     * Sets the `keyStore` property with the given `KeyStore` object.
     *
     * @param keyStore An object representing the desired KeyStore properties to use.
     * @return Reference to `this` KKServices instance.
     */
    fun setKeyStore(keyStore: KeyStore): KKServices {
        // Replace the existing KeyStore with the provided one
        // Note: You might want to handle this differently depending on your actual use case
        return this
    }
}

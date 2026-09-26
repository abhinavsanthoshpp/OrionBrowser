package org.orion.browser.adblock

object CosmeticFilterManager {

    private const val HIDE_AD_CSS = """
        [id*="google_ads_"],
        [id*="banner-ad"],
        [class*="sponsored-post"],
        [class*="ad-container"],
        [class*="advertisement"],
        ins.adsbygoogle,
        .native-ad,
        .taboola,
        .outbrain,
        div[data-adunit],
        iframe[src*="doubleclick.net"] {
            display: none !important;
            height: 0px !important;
            min-height: 0px !important;
            visibility: hidden !important;
        }
    """

    private const val OLED_TRUE_BLACK_CSS = """
        html, body {
            background-color: #000000 !important;
            color: #E0E0E0 !important;
        }
        div, section, article, nav, header, footer, main {
            background-color: transparent !important;
            border-color: #222222 !important;
        }
        p, span, h1, h2, h3, h4, h5, h6, li, a {
            color: #DDDDDD !important;
        }
        a {
            color: #BB86FC !important;
        }
        img, video {
            opacity: 0.9 !important;
        }
    """

    fun getCosmeticInjectionJs(enableOledBlack: Boolean = false): String {
        val cssToInject = buildString {
            append(HIDE_AD_CSS.replace("\n", "").replace("\"", "\\\""))
            if (enableOledBlack) {
                append(OLED_TRUE_BLACK_CSS.replace("\n", "").replace("\"", "\\\""))
            }
        }

        return """
            (function() {
                var style = document.getElementById('orion-cosmetic-style');
                if (!style) {
                    style = document.createElement('style');
                    style.id = 'orion-cosmetic-style';
                    style.type = 'text/css';
                    style.appendChild(document.createTextNode("$cssToInject"));
                    (document.head || document.documentElement).appendChild(style);
                }
            })();
        """.trimIndent()
    }
}

(ns asmaxxing.core
  (:require [reagent.dom.client :as rdc]
            [asmaxxing.i18n :as i18n]
            [asmaxxing.ui :as ui]))

(defonce root (atom nil))

(defn mount []
  (when-not @root
    (reset! root (rdc/create-root (js/document.getElementById "app"))))
  (rdc/render @root [ui/root]))

(defn init []
  (i18n/set-locale! @i18n/locale)
  (mount))

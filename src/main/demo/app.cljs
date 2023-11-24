(ns demo.app
  (:require
    [shadow.cljs.modern :refer (js-await)]
    [shadow.esm :as esm]
    [goog.object :as gobj]
    ["react" :as react]
    [reagent.core :as reagent]
    [reagent.dom :as rdom]))

;; using CDN variant https://cdnjs.com/libraries/pdf.js
;; pdfjs-dist npm lib often many versions behind what is available via CDN
;; also easier to manage worker, since that is prebuilt

;; set in init
(defonce ^js pdfjs nil)

(defn pdf-canvas [{:keys [url]}]
  ;; ref
  (let [canvas-ref (react/useRef nil)]

    ;; initialize and attach pdfjs when the canvas is mounted
    (react/useEffect
      (fn []
        (js-await [^js pdf (.-promise (.getDocument pdfjs url))]
          (js/console.log "pdf" pdf)
          (js-await [^js page (.getPage pdf 1)]
            (js/console.log "page" page)
            (let [viewport (.getViewport page #js {:scale 1.5})
                  canvas (.-current canvas-ref)
                  context (.getContext canvas "2d")

                  render-context
                  #js {:canvasContext context
                       :viewport viewport}]

              (set! canvas -height (.-height viewport))
              (set! canvas -width (.-width viewport))

              (js-await [_ (.-promise (.render page render-context))]
                (js/console.log "Page rendered."))
              )))

        (fn []
          ;; not sure if there is supposed to be any cleanup for the pdfjs objects
          ;; might need to store those somewhere and dispose of them properly here
          (js/console.log "cleanup")))

      ;; ensure this only re-runs when url changes
      #js [url])

    [:canvas {:ref canvas-ref}]))

(defn app []
  [:f> pdf-canvas {:url "https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/examples/learning/helloworld.pdf"}])

(def root-el
  (js/document.getElementById "root"))

(defn ^:dev/after-load start []
  (rdom/render [app] root-el))

(defn init []
  ;; import pdfjs from CDN using JS import(), which returns a promise. so we wait for it to complete before starting our app
  ;; could start our app and show a loading spinner or something, but to keep this example simple we just wait
  (js-await [^js mod (esm/dynamic-import "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.189/pdf.min.mjs")]
    (set! (.. mod -GlobalWorkerOptions -workerSrc) "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.189/pdf.worker.min.mjs")
    (set! pdfjs mod)
    (start)))

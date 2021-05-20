(ns demo.app
  (:require
    [goog.object :as gobj]
    ["react" :as react]
    [reagent.core :as reagent]
    [reagent.dom :as rdom]))

;; using CDN variant https://cdnjs.com/libraries/pdf.js
;; pdfjs-dist npm lib often many versions behind what is available via CDN
;; also easier to manage worker, since that is prebuilt

;; included via script tag in public/index.html
;; it provides the window['pdfjs-dist/build/pdf'] global variable

(def ^js pdfjs (gobj/get js/window "pdfjs-dist/build/pdf"))

(defn pdf-canvas [{:keys [url]}]
  ;; ref
  (let [canvas-ref (react/useRef nil)]

    ;; initialize and attach pdfjs when the canvas is mounted
    (react/useEffect
      (fn []
        (-> (.getDocument pdfjs url)
            (.-promise)
            (.then (fn [^js pdf]
                     (js/console.log "pdf" pdf)
                     (.getPage pdf 1)))
            (.then (fn [^js page]
                     (js/console.log "page" page)
                     (let [viewport (.getViewport page #js {:scale 1.5})
                           canvas (.-current canvas-ref)
                           context (.getContext canvas "2d")

                           render-context
                           #js {:canvasContext context
                                :viewport viewport}]

                       (set! canvas -height (.-height viewport))
                       (set! canvas -width (.-width viewport))

                       (-> (.render page render-context)
                           (.-promise)
                           (.then (fn []
                                    (js/console.log "Page rendered."))))

                       ))))

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

  ;; need to tell the lib where to load the worker from, also using same CDN
  (set! (.. pdfjs -GlobalWorkerOptions -workerSrc) "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.8.335/pdf.worker.min.js")

  (start))

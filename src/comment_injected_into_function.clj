(ns comment-injected-into-function
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]))

;; so here is the situation we have
(comment
  ()
  (defn foo "a comment" [x] (inc x))

  (:doc (meta #'foo))
;; => "a comment"
;; when what i want is this
  (comment-fn 'foo
              (foo 1)
  ;; => 2
              )

  (:doc (meta #'foo))
;; => "a comment

  (foo 1)
      ;; => 2"

;; the goal is to put the comment body (2nd arg) into the function of the first
  (alter-meta! #'foo update :doc str "extra")
  )

(defn comment-fn* [f rest]
  (alter-meta! f update :doc str (str/join \newline (for [r rest]
                                                      (str \newline (with-out-str (pp/pprint r)))))))

(defmacro comment-fn
  [f & rest]
  `(comment-fn* (var ~f)
                '~rest))

;; comment inception...
(comment
  (defn foo "a comment" [x] (inc x))

  (comment-fn foo
              (inc 1))

  (:doc (meta #'foo))
  ;; => "a comment\n(inc 1)\n"


  nil)

(ns result-clj.core
  (:require [result-clj.protocol :as p]))

(defrecord Result [ok? value-or-error]
  p/IResult
  (ok? [_] ok?)
  (fail? [_] (not ok?))
  (value [_] value-or-error)
  (error [_] value-or-error))

(defn ok
  ([]
   (ok nil))
  ([value]
   (Result. true value)))

(defn fail
  ([]
   (fail nil))
  ([error]
   (Result. false error)))

(defn is-result?
  [result]
  (satisfies? p/IResult result))

(defn ok?
  [result]
  (p/ok? result))

(defn fail?
  [result]
  (p/fail? result))

(defn value
  [result]
  (p/value result))

(defn error
  [result]
  (p/error result))

(defmacro when-ok?
  [result & body]
  `(when (ok? ~result)
     (do ~@body)))

(defmacro when-fail?
  [result & body]
  `(when (fail? ~result)
     (do ~@body)))

(defmacro until-fail
  "Execute forms until result is failed."
  [& forms]
  (if (empty? forms)
    (ok)
    (let [[last-form & forms] (reverse forms)]
      (reduce (fn [acc form]
                `(let [form# ~form]
                   (cond
                     (and (is-result? form#)
                          (fail? form#))
                     form#

                     (is-result? form#)
                     (do form#
                         ~acc)

                     :else
                     (do (ok form#)
                         ~acc))))
              `(let [last-form# ~last-form]
                 (if (is-result? last-form#)
                   last-form#
                   (ok last-form#))) forms))))

(defmacro let-until-fail
  "Binding until result is failed."
  [bindings & body]
  (when-not (and (vector? bindings)
                 (not-empty bindings)
                 (even? (count bindings)))
    (throw (new IllegalArgumentException
                "bindings has to be a vector with event number of elements")))

  (->> (reverse (partition 2 bindings))
       (reduce (fn [acc [l r]]
                 `(let [r# ~r]
                    (cond
                      (and (is-result? r#)
                           (fail? r#))
                      r#

                      (is-result? r#)
                      (let [~l (value r#)] ~acc)

                      :else
                      (let [~l r#] ~acc))))
               `(let [r# (do ~@body)]
                  (if (is-result? r#)
                    r#
                    (ok r#))))))

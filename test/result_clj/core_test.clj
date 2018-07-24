(ns result-clj.core-test
  (:require [clojure.test :refer [deftest testing is are]])
  (:require [result-clj
             [core :as core]
             [protocol :as p]]))

(deftest ok
  (testing "create *ok*"
    (let [result (core/ok)]
      (is (p/ok? result))
      (is (not (p/fail? result)))))
  (testing "create *ok* with string value"
    (let [value "foobar"
          result (core/ok value)]
      (is (p/ok? result))
      (is (not (p/fail? result)))
      (is (= value (p/value result))))))

(deftest fail
  (testing "create *fail*"
    (let [result (core/fail)]
      (is (p/fail? result))
      (is (not (p/ok? result)))))

  (testing "create *fail* with string error"
    (let [value "foobar"
          result (core/fail value)]
      (is (p/fail? result))
      (is (not (p/ok? result)))
      (is (= value (p/error result))))))

(deftest ok?
  (testing "invalid result should be thrown"
    (are [result] (thrown? IllegalArgumentException (core/ok? result))
      42
      "foobar"
      {}
      "result"
      []))

  (testing "*fail* should not be ok"
    (are [result] (not (core/ok? result))
      (core/fail)
      (core/fail "foo")
      (core/fail "bar")
      (core/fail nil)
      (core/fail {:key 42})
      (core/fail [42])
      (core/fail #{42})))

  (testing "*ok* should be ok"
    (are [result] (core/ok? result)
      (core/ok)
      (core/ok "foo")
      (core/ok "bar")
      (core/ok nil)
      (core/ok {:key 42})
      (core/ok [42])
      (core/ok #{42}))))

(deftest fail?
  (testing "invalid result should be thrown"
    (are [result] (thrown? IllegalArgumentException (core/fail? result))
      42
      "foobar"
      {}
      "result"
      []))

  (testing "*ok* should not be fail"
    (are [result] (not (core/fail? result))
      (core/ok)
      (core/ok "foo")
      (core/ok "bar")
      (core/ok nil)
      (core/ok {:key 42})
      (core/ok [42])
      (core/ok #{42})))

  (testing "*fail* should be fail"
    (are [result] (core/fail? result)
      (core/fail)
      (core/fail "foo")
      (core/fail "bar")
      (core/fail nil)
      (core/fail {:key 42})
      (core/fail [42])
      (core/fail #{42}))))

(deftest when-ok?
  (testing "invalid result should be thrown"
    (are [result] (thrown? IllegalArgumentException (core/when-ok? result
                                                      (do
                                                        "a lots things")
                                                      true))
      42
      "foobar"
      {}
      "result"
      []))

  (testing "*fail* should not do"
    (are [result] (not (core/when-ok? result
                         (do
                           "a lots things")
                         true))
      (core/fail)
      (core/fail "foo")
      (core/fail "bar")
      (core/fail nil)
      (core/fail {:key 42})
      (core/fail [42])
      (core/fail #{42})))

  (testing "when *ok* should do"
    (are [result] (core/when-ok? result
                    (do
                      "a lots things")
                    true)
      (core/ok)
      (core/ok "foo")
      (core/ok "bar")
      (core/ok nil)
      (core/ok {:key 42})
      (core/ok [42])
      (core/ok #{42}))))

(deftest when-fail?
  (testing "invalid result should be thrown"
    (are [result] (thrown? IllegalArgumentException (core/when-fail? result
                                                      (do
                                                        "a lots things")
                                                      true))
      42
      "foobar"
      {}
      "result"
      []))

  (testing "*ok* should not do"
    (are [result] (not (core/when-fail? result
                         (do
                           "a lots things")
                         true))
      (core/ok)
      (core/ok "foo")
      (core/ok "bar")
      (core/ok nil)
      (core/ok {:key 42})
      (core/ok [42])
      (core/ok #{42})))

  (testing "when *fail* should do"
    (are [result] (core/when-fail? result
                    (do
                      "a lots things")
                    true)
      (core/fail)
      (core/fail "foo")
      (core/fail "bar")
      (core/fail nil)
      (core/fail {:key 42})
      (core/fail [42])
      (core/fail #{42}))))

(deftest until-fail
  (testing "should stop when *fail*"
    (let [failed-error "foo"
          result (core/until-fail
                   (core/fail failed-error)
                   (core/fail "bar"))]
      (is (core/fail? result))
      (is (= failed-error (p/error result))))

    (let [failed-error "foo"
          result (core/until-fail
                   (core/ok "baz")
                   (core/fail failed-error)
                   (core/ok "bar"))]
      (is (core/fail? result))
      (is (= failed-error (p/error result))))

    (let [failed-error "foo"
          result (core/until-fail
                   (core/fail failed-error)
                   (throw (new Exception "should not throw")))]
      (is (core/fail? result))
      (is (= failed-error (p/error result)))))

  (testing "should continue when not *fail*"
    (let [value "foo"
          result (core/until-fail
                   (core/ok value))]
      (is (= value (core/value result))))

    (let [value "foo"
          result (core/until-fail
                   value)]
      (is (= value (core/value result))))

    (let [value "foo"
          result (core/until-fail
                   (do 1)
                   (do 2)
                   value)]
      (is (= value (core/value result))))

    (let [value "foo"
          result (core/until-fail
                   (do 1)
                   (do 2)
                   (core/ok "bar")
                   (core/ok value))]
      (is (core/ok? result))
      (is (= value (p/value result))))))

(deftest let-until-fail
  (testing "should stop when *fail*"
    (let [result
          (core/let-until-fail [v1 (core/fail :foo)]
            (throw (AssertionError. "should not reach here")))]
      (is (core/fail? result))
      (is (= :foo (core/value result))))

    (let [result
          (core/let-until-fail [v1 (core/ok 1)
                                v2 (core/fail :foo)
                                v3 (core/ok (inc v1))]
            (throw (AssertionError. "should not reach here")))]
      (is (core/fail? result))
      (is (= :foo (core/value result)))))

  (testing "should treat non-result value as *ok*"
    (let [result
          (core/let-until-fail [v1 1
                                v2 (inc v1)]
            (core/ok v2))]
      (is (core/ok? result))
      (is (= 2 (core/value result)))))

  (testing "should continue when *ok*, and bind ok value"
    (let [result
          (core/let-until-fail [v1 (core/ok 1)
                                v2 (core/ok (inc v1))]
            (core/ok v2))]
      (is (core/ok? result))
      (is (= 2 (core/value result))))))

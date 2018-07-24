# result-clj

[![Build Status](https://travis-ci.org/0x636363/result-clj.svg?branch=master)](https://travis-ci.org/0x636363/result-clj)
[![Coverage Status](https://coveralls.io/repos/github/0x636363/result-clj/badge.svg)](https://coveralls.io/github/0x636363/result-clj)
[![Clojars](https://img.shields.io/clojars/v/result-clj.svg?maxAge=3600)](https://clojars.org/result-clj)
[![License](https://img.shields.io/github/license/0x636363/result-clj.svg?maxAge=3600)]()

A Clojure library for introducing **Result**, which supports returning and propagating errors.

## Installation

To install, add the following to your project `:dependencies`

```clojure
[eftest "0.5.2"]
```

## Usage

### Import package

```clojure
(:require [result-clj.core :as result])
```

### Ok or Fail

```clojure
(if success
  (result/ok with-something-to-return)
  (result/fail an-error))
```

### Check if ok or fail

```clojure
(let [result (do-something)]
  (if (result/ok? result)
    (do-something-when-success (result/value result))
    (do-something-when-fail (result/error result))))
```

### Pipeline executing until fail

```clojure
(result/until-fail
  (when-not (validate-something)
    (result/fail an-error))
  
  (when-not (validate-another-thing)
    (result/fail another-error))
    
  (do-something)
  (do-another-thing) ;; if return a **Non-Result**, `until-fail` will wrap a **Result** for it.
  )
```

### Pipeline binding until fail

```clojure
(let [result
      (result/let-until-fail [v1 (do-something-1)
                              v2 (do-something-2 v1)
                              v3 (do-something-3 v1 v2)
        (do-something v1 v2 v3)])]  ;; no matter evaluated value is a **Result**, it will wrap a **Result** for it.
  (if (result/ok? result-or-a-value)
    (do-something-when-ok (result/value result))
    (do-something-when-fail (result/error result))))
```

### Threads the expr through the forms until fail

**TODO**

## License

Copyright © 2018 ccc

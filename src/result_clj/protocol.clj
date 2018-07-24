(ns result-clj.protocol)

(defprotocol IResult
  (ok? [_]
    "If the result ok?")
  (fail? [_]
    "If the result fail?")
  (value [_]
    "Get result value")
  (error [_]
    "Get error"))

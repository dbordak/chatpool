(ns chatpool.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [chatpool.core-test]))

(doo-tests 'chatpool.core-test)

(ns app.subs
  (:require
   [re-frame.core :as re-frame]))


(re-frame/reg-sub
  :lists
  (fn [db]
    (sort-by :id > (vals (:lists db)))))


(re-frame/reg-sub
  :todos
  (fn [db]
    (sort-by :id > (vals (:todos db)))))

(re-frame/reg-sub
  :filter-keyword
  (fn [db]
    (:filter db)))

(re-frame/reg-sub
  :visible-todos
  :<- [:todos]
  :<- [:filter-keyword]
  (fn [[todos filter-keyword] _]
    (filter (case filter-keyword
              :active (complement :done)
              :done :done
              :all identity) todos)))

(re-frame/reg-sub
  :active-list
  (fn [db]
    (get-in db [:lists (:active-list-id db)])))
